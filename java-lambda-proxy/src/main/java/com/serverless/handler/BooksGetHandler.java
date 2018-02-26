package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.gateway.ApiGatewayResponse;
import com.serverless.repository.BookDao;
import com.serverless.repository.BookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;

public class BooksGetHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(BooksGetHandler.class);

    private static volatile BookRepository bookRepository = BookRepository.instance();

    private static volatile BookDao bookDao = BookDao.instance();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(bookDao.findBooks())
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }
}
