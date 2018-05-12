import * as http from 'http';
import app from './index'
import logger from './utils/logger.utils';

const port = 4000;
const host = 'localhost';

const server = http.createServer(app);

server.listen(port, '', () => {
  logger.info(`server started on http://${host}:${port}`)
});
