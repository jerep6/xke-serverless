import { Request, Response, NextFunction } from 'express';
import logger from '../utils/logger.utils';

export default function (req: Request, res: Response, next: NextFunction) {
  logger.error(`Route ${req.path} not found`,);
  res.status(404).json();
}
