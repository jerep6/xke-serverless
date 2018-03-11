import { APIVersions, ConfigurationOptions } from 'aws-sdk/lib/config';

import config from './config';
import logger from './logger.utils';


let AWS;
// If xray is enabled capture call from aws-sdk
if (config.xray) {
  const AWSXRay = require('aws-xray-sdk');
  AWSXRay.setLogger(logger);
  AWS = AWSXRay.captureAWS(require('aws-sdk'));
  AWSXRay.captureHTTPsGlobal(require('http'));
} else {
  AWS = require('aws-sdk');
}

const awsconf: ConfigurationOptions & APIVersions = {
  region: "eu-west-1",
  apiVersions: {
    apigateway: '2015-07-09',
    cloudwatch: '2010-08-01',
    dynamodb: '2012-08-10',
    lambda: '2015-03-31',
    s3: '2006-03-01'
  }
};
AWS.config.update(awsconf);


let DocumentClient, DynamoDB;
if (process.env.NODE_ENV === 'test') {
  const configDynamo = {
    region: "eu-west-1",
    endpoint: "http://localhost:8888",
    accessKeyId: 'dummy',
    secretAccessKey: 'dummy'
  };

  DynamoDB = new AWS.DynamoDB(configDynamo);
  DocumentClient = new AWS.DynamoDB.DocumentClient(configDynamo);
}
else {
  DynamoDB = new AWS.DynamoDB();
  DocumentClient = new AWS.DynamoDB.DocumentClient();
}

export { DynamoDB };
export { DocumentClient };
export default AWS;
