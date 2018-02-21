import * as express from 'express';

import * as repo from '../repositories/BookRepository'
import logger from '../utils/logger.utils';

const router = express.Router();
router.get('/', list);
router.get('/:bookId', get);

export default router;

async function list(req: express.Request, res: express.Response) {
  logger.debug('list books')
  const books = await repo.list();
  res.send(books);
};

async function get(req: express.Request, res: express.Response) {
  const { bookId } = req.params;
  logger.debug(`get book ${bookId}`)
  const book = await repo.get(bookId);

  res.send(book)
};
