import AWS from '../utils/awssdk'
import config from '../utils/config'
import logger, { errorToString} from '../utils/logger.utils';

const documentClient = new AWS.DynamoDB.DocumentClient();


export async function get(bookId: string) {
  try {
    const result = await documentClient.get({
      TableName: config.dynamodb.book,
      Key: {id: bookId}
    }).promise();

    return result.Item;

  } catch(error) {
    logger.error(`BookRepository.get - Error reading book ${bookId}`, {error: errorToString(error)});
    throw error;
  }
}

export async function list() {
  try {
    const result = await documentClient.scan({
      TableName: config.dynamodb.book
    }).promise();

    return result.Items;
  } catch(error) {
    logger.error(`BookRepository.get - Error listing books`, {error: errorToString(error)});
    throw error;
  }
}

