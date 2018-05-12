import * as express from 'express';
import * as bodyParser from 'body-parser';
import * as cors from 'cors';
import * as morgan from 'morgan';

import ErrorsMiddleware from './middlewares/ErrorsMiddleware';
import NotFoundMiddleware from './middlewares/NotFoundMiddleware';
import booksRoutes from './controllers/BookController';
import authRoutes from './controllers/AuthController';

import * as jwt from 'express-jwt';
import * as jwksRsa from 'jwks-rsa';
import config from './utils/config';

const app = express();

app.use(morgan('common'));
app.use(cors());
app.use(bodyParser.json());

// ##### JWT middleware #####
const jwtMiddleware = jwt({
  // Dynamically provide a signing key based on the kid in the header and the signing keys provided by the JWKS endpoint.
  secret: jwksRsa.expressJwtSecret({
    cache: true,
    rateLimit: true,
    jwksRequestsPerMinute: 5,
    jwksUri: config.jwt.jwksUri
  }),

  // Validate the audience and the issuer.
  audience: config.jwt.audience,
  issuer: config.jwt.issuer,
});
app.use(jwtMiddleware);


app.use('/:env?*/books', booksRoutes);
app.use('/:env?*/auth', authRoutes);

app.use(ErrorsMiddleware);
app.use(NotFoundMiddleware);

export default app;
