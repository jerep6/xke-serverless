import * as express from 'express';
import {Request, Response, NextFunction} from 'express';

import * as repo from '../repositories/BookRepository'
// import * as repo from '../repositories/BookRepositoryDynamo'
import logger from '../utils/logger.utils';
import { Book } from '../../typings/Book';

const router = express.Router();
router.get('/', list);
router.get('/:bookId', get);
router.post('/', create);
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

async function create(req: Request, res: Response, next: NextFunction) {
  logger.debug(`post book`, req.body);

  try {
    const book: Book = await repo.create(req.body);
    res
      .status(201)
      .json(book)
  }
  catch (error) {
    next(error);
  }
};
