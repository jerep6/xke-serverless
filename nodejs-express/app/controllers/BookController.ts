import * as express from 'express';
import {Request, Response, NextFunction} from 'express';

import * as repo from '../repositories/BookRepository'
import logger from '../utils/logger.utils';
import { Book } from '../../typings/Book';

const router = express.Router();
router.get('/', list);
router.get('/:bookId', get);
export default router;

async function list(req: Request, res: Response, next: NextFunction) {
  logger.debug('list books');

  try {
    const books: Book[] = await repo.list();
    res
      .json(books);
  }
  catch (error) {
    next(error);
  }
};

async function get(req: Request, res: Response, next: NextFunction) {
  const { bookId } = req.params;
  logger.debug(`get book ${bookId}`);

  try {
    const book: Book = await repo.get(bookId);
    res
      .status(book ? 200: 404)
      .json(book)
  }
  catch (error) {
    next(error);
  }
};
