import { Context } from 'koa';
import logger from '../utils/logger.utils'

export default async function (ctx: Context, next) {
    try {
        await next();
    }
    catch (err) {
        logger.warn('Unhandled error', err);
        ctx.response.status = 500;
        ctx.response.body = withError(err);
    }
}

function withError(err: any) {
    if (err instanceof Error) {
        return { 'message': err.message };
    }
    return err;
}
