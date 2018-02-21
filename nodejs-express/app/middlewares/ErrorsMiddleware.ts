import { Request, Response } from 'express';

import logger from '../utils/logger.utils'

export default function (err: Error, req: Request, res: Response) {
  logger.error(JSON.stringify(err, null, 2))
  res.status(500).send(withError(err))
}

function withError(err: any) {
  if (err instanceof Error) {
    return { "message": err.message };
  }
  return err;
}
