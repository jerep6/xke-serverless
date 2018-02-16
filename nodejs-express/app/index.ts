import * as express from 'express';
import * as bodyParser from 'body-parser';
import * as morgan from 'morgan';

import ErrorsMiddleware from './middlewares/ErrorsMiddleware';
import booksRoutes from './controllers/BookController';

const app = express();
const router = express.Router();

app.use(morgan('common'));
app.use(bodyParser.json());

app.use('/:env?*/books', booksRoutes)

app.use(ErrorsMiddleware)

export default app;
