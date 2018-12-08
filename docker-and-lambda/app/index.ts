import * as Koa from 'koa';
import * as Router from 'koa-router';
import * as bodyParser from 'koa-bodyparser';
import * as cors from '@koa/cors';
import errorsMiddleware from './middlewares/ErrorsMiddleware';

import {routes as booksRoutes} from './controllers/BookController'

const app = new Koa();
app.use(errorsMiddleware);
app.use(cors());
app.use(bodyParser());

const rootRouter = new Router({"prefix": '/:stage?'});
rootRouter.use('/books', booksRoutes().routes());


app.use(rootRouter.routes());
app.use(rootRouter.allowedMethods());

export default app;

