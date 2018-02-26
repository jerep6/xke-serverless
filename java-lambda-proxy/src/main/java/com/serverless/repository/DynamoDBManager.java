package com.serverless.repository;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DynamoDBManager {

    private static volatile DynamoDBManager instance;

    private DynamoDBMapper mapper;

    // this is only defined if running integration tests with local dynamodb
    // will always be null when running as an aws lambda
    private static final String PORT = System.getProperty("dynamodb.port");
    private static final String END_TO_END = System.getProperty("endtoend");


    private DynamoDBManager() {
        AmazonDynamoDBClient client;
        AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();

        if (isNotBlank(PORT) && isNotBlank(END_TO_END)) {

            AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                    new AwsClientBuilder.EndpointConfiguration(String.format("http://localhost:%s", PORT), "");

            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials("", "");
            AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(basicAWSCredentials);

            client = (AmazonDynamoDBClient) clientBuilder
                    .withCredentials(credentialsProvider)
                    .withEndpointConfiguration(endpointConfiguration).build();

        } else {
            // in this case, we are in aws lambda : credentials are inherited
            // from environment variables
            client = (AmazonDynamoDBClient) clientBuilder.withRegion(Regions.EU_WEST_1).build();
        }
        mapper = new DynamoDBMapper(client);
    }

    public static DynamoDBManager instance() {

        if (instance == null) {
            synchronized (DynamoDBManager.class) {
                if (instance == null) {
                    instance = new DynamoDBManager();
                }
            }
        }
        return instance;
    }

    public static DynamoDBMapper mapper() {
        return instance().mapper;
    }
}
