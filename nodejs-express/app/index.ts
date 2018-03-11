import * as express from 'express';
import * as bodyParser from 'body-parser';
import * as morgan from 'morgan';
import * as cors from 'cors';

import ErrorsMiddleware from './middlewares/ErrorsMiddleware';
import NotFoundMiddleware from './middlewares/NotFoundMiddleware';
import booksRoutes from './controllers/BookController';

const app = express();

app.use(morgan('common'));
app.use(bodyParser.json());
app.use(cors());

app.use('/:env?*/books', booksRoutes);

app.use(ErrorsMiddleware);
app.use(NotFoundMiddleware);

export default app;
