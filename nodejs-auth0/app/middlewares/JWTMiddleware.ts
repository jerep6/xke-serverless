import { Request, Response, NextFunction } from 'express';
import * as jwt from 'jsonwebtoken';

export default function (req: Request, res: Response, next: NextFunction) {

  const token = extractToken(<any>req.headers);
  const tokenDecoded = decodeToken(token);
  putTokenInRequest(req, tokenDecoded);

  console.log(tokenDecoded);
  next();
}


function extractToken(headers: { [name: string]: string }): string {
  if (!headers || !headers.authorization) {
    throw new Error('No authorization header found');
  }

  const parts = headers.authorization.split(' ');

  if (parts.length != 2 || !/^Bearer$/i.test(parts[0])) {
    throw new Error('Authorization header malformed');
  }

  return parts[1];
}

function decodeToken(encodedToken: string) {
  return jwt.decode(encodedToken);
}

function putTokenInRequest(req: Request, decodedToken) {
  (<any>req).jwt_token = decodedToken;
}