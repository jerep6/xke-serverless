import * as express from 'express';
import {Request, Response, NextFunction} from 'express';

import * as repo from '../repositories/BookRepository'
import logger from '../utils/logger.utils';
import { Book } from '../../typings/Book';

const router = express.Router();
router.get('/me', getToken);
export default router;

async function getToken(req: Request, res: Response, next: NextFunction) {
  res.json((<any>req).jwt_token);
};