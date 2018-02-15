import { Context } from 'koa';
import { LambdaApplicationNext } from '../../typings/Lambda';
import * as Router from 'koa-router';
import * as repo from '../repositories/BookRepository'

export function routes(): Router {
    const router = new Router();
    router
        .get('/', list)
        .get('/:bookId', get);

    return router;
}

async function list(ctx: Context, next: LambdaApplicationNext) {
    const books = await repo.list();
    ctx.body = books;
};

async function get(ctx: Context, next: LambdaApplicationNext) {
    const { bookId } = ctx.params;
    const book = repo.get(bookId);

    ctx.body = book;
};
