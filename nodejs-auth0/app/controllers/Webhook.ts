import * as express from 'express';
import {Request, Response, NextFunction} from 'express';

import logger from '../utils/logger.utils';

const router = express.Router();
router.get('/', webhook);
export default router;

async function webhook(req: Request, res: Response, next: NextFunction) {
  logger.info('------ WEBHOOH QUERY', req.query);
  logger.info('------ WEBHOOH BODY', req.body);

  res.status(200);
};