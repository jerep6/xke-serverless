
export default {
  logs : {
    json: process.env.LOG_FORMAT === 'json'
  },
  dynamodb: {
    book: process.env.TABLE_BOOK || 'books'
  },
  xray: process.env.XRAY === 'true',
  jwt: {
    jwksUri: 'https://xebia-xke.eu.auth0.com/.well-known/jwks.json',
    audience: 'myapi1',
    issuer: 'https://xebia-xke.eu.auth0.com/'
  }
}
