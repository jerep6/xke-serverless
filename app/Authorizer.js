'use strict';

var jwksClient = require('jwks-rsa');
var jwt = require('jsonwebtoken');


const client = jwksClient({
  cache: true,
  rateLimit: true,
  jwksRequestsPerMinute: 10, // Default value
  jwksUri: process.env.JWKS_URI
});


function getPolicyDocument(effect, resource) {
  return {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Action": "execute-api:Invoke",
        "Effect": effect,
        "Resource": resource
      }
    ]
  };
}

function getToken(params) {
  if (!params.type || params.type !== 'TOKEN') {
    throw new Error("Expected 'event.type' parameter to have value TOKEN");
  }

  const tokenString = params.authorizationToken;
  if (!tokenString) {
    throw new Error("Expected 'event.authorizationToken' parameter to be set");
  }

  const match = tokenString.match(/^Bearer: (.*)$/);
  if (!match || match.length < 2) {
    throw new Error("Invalid Authorization token - '" + tokenString + "' does not match 'Bearer .*'");
  }
  return match[1];
}

function verifyJWTToken(params) {
  return new Promise((resolve, reject) => {
    try {
      const token = getToken(params);

      const decoded = jwt.decode(token, {complete: true});
      if (!decoded) {
        reject("Decoded token is undefined");
        return;
      }

      const kid = decoded.header.kid;
      client.getSigningKey(kid, (err, key) => {
        if (err) {
          console.log('Error getting key', err);
          reject("Error getting key");
          return;
        }

        const signingKey = key.publicKey || key.rsaPublicKey;
        jwt.verify(token, signingKey, {
          audience: process.env.AUDIENCE,
          issuer: process.env.TOKEN_ISSUER
        }, (err, decoded) => {
          if (err) {
            console.log('Error verifying signature', err);
            reject("Error verifying signature");
            return;
          }

          resolve({
            principalId: decoded.sub,
            context: {
              scope: decoded.scope
            }
          });

        });

      });
    }
    catch (err) {
      console.log('Error getting token', err);
      reject(e);
    }
  })
}

module.exports.handler = function (event, context, callback) {
  console.log("event", JSON.stringify(event));

  verifyJWTToken(event)
    .then(result => {
      console.log("Authorization successful");
      callback(null, Object.assign(result, {policyDocument: getPolicyDocument("Allow", event.methodArn)}));
    })
    .catch(error => {
      console.log("Authorization deny", error);
      callback(null, {
        principalId: "fake",
        policyDocument: getPolicyDocument("Deny", event.methodArn),
      });
    })

}
