import app from './index'
import logger from '../../nodejs-express/app/utils/logger.utils';
import * as http from "http";

const port = 3000;
const host = 'localhost';

const server = http.createServer(app.callback());

server.listen(port, '', () => {
    logger.info(`server started on http://${host}:${port}`)
});