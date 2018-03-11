import * as dynamodbLocal from 'dynamodb-localhost';
import logger from '../app/utils/logger.utils';
import AWS, { DynamoDB, DocumentClient } from '../app/utils/awssdk'
import * as glob from 'glob'
import { pick } from 'lodash'

export {
  beforeAllHook,
  afterAllHook,
  afterEachHook,
  populateDatabase
}


const DYNAMODB_PORT = 8888;

async function beforeAllHook() {

  logger.debug('Start dynamolocal');

  await dynamodbLocal.start({
    port: DYNAMODB_PORT,
    inMemory: true,
  });

  logger.debug('Create tables');
  await createTables();
};


async function createTables() {
  const promises = (await getDynamoConfig()).map(conf => DynamoDB.createTable(conf).promise());
  return await Promise.all(promises);
}

async function afterAllHook() {
  logger.debug('Stop dynamolocal');
  await dynamodbLocal.stop(DYNAMODB_PORT);
};

async function afterEachHook() {
  logger.debug('Purges tables');

  const promises = (await getDynamoConfig()).map(conf => deleteTableItems(conf));
  await Promise.all(promises);
};

/**
 * All configuration for database is stored in json file
 * @returns {Promise<AWS.CreateTableInput[]>}
 */
async function getDynamoConfig(): Promise<AWS.DynamoDB.CreateTableInput[]> {
  return new Promise<AWS.DynamoDB.CreateTableInput[]>((resolve, reject) => {
    glob(`${__dirname}/tables/*.table.json`, [], (err, files) => {
      resolve(files.map(require));
    });
  })
}

async function deleteTableItems(tableConfig: AWS.DynamoDB.CreateTableInput): Promise<any> {
  const tablename = tableConfig.TableName;
  const tablekeys = tableConfig.KeySchema.map(elt => elt.AttributeName);

  const result: AWS.DynamoDB.DocumentClient.ScanOutput = await DocumentClient.scan({ TableName: tablename }).promise();
  const promises = (result.Items || []).map(item => {
    return DocumentClient.delete({
      TableName: tablename,
      Key: pick(item, tablekeys)
    }).promise();

  });

  await Promise.all(promises);
}

async function populateDatabase(filePath: string) {
  const data = require(filePath);

  for (let tableName of Object.keys(data)) { // For each root key (ie table name)
    if (data[tableName]) {
      logger.debug(`Load table ${tableName} with file ${filePath}`);
      for (let item of data[tableName]) {
        await DocumentClient.put({
          TableName: tableName,
          Item: item
        }).promise()
      }
    }
  }
}
