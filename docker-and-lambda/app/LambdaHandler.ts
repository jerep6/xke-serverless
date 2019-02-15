import { APIGatewayEvent, Context as LambdaContext, ProxyCallback } from 'aws-lambda';
import * as serverlessHttp from 'serverless-http';
import app from '.';

export async function handler(event: APIGatewayEvent, context: LambdaContext, callback: ProxyCallback) {
  return serverlessHttp(app)(event, context);
}
