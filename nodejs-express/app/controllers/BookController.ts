import * as express from 'express';

import * as repo from '../repositories/BookRepository'
import logger from '../utils/logger.utils';

const router = express.Router();

router.get('/', list);
router.get('/:bookId', get);

export default router;

function list(req: express.Request, res: express.Response) {
  logger.debug('list books')
  const books = repo.list();
  res.send(books);
};

function get(req: express.Request, res: express.Response) {
  const { bookId } = req.params;
  logger.debug(`get book ${bookId}`)
  const book = repo.get(bookId);

  res.send(book)
};
