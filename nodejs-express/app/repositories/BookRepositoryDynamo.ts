import AWS from '../utils/awssdk'
import config from '../utils/config'
import logger, { errorToString} from '../utils/logger.utils';
import { Book } from '../../typings/Book';
import { DocumentClient } from 'aws-sdk/lib/dynamodb/document_client';

const documentClient = new AWS.DynamoDB.DocumentClient();


export async function get(bookId: string): Promise<Book> {
  try {
    const result: DocumentClient.GetItemOutput = await documentClient.get({
      TableName: config.dynamodb.book,
      Key: {id: bookId}
    }).promise();

    return <Book> result.Item;

  } catch(error) {
    logger.error(`BookRepository.get - Error reading book ${bookId}`, {error: errorToString(error)});
    throw error;
  }
}

export async function list(): Promise<Book[]> {
  try {
    const result = await documentClient.scan({
      TableName: config.dynamodb.book
    }).promise();

    return <Book[]> result.Items;
  } catch(error) {
    logger.error(`BookRepository.get - Error listing books`, {error: errorToString(error)});
    throw error;
  }
}

