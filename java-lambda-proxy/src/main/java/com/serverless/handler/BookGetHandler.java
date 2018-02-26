package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.gateway.ApiGatewayResponse;
import com.serverless.model.Book;
import com.serverless.repository.BookDao;
import com.serverless.repository.BookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.serverless.utils.ParamsHelper.getPathParameter;

public class BookGetHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(BookGetHandler.class);

    private static volatile BookRepository bookRepository = BookRepository.instance();

    private static volatile BookDao bookDao = BookDao.instance();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        Optional<Book> bookOptional = getPathParameter("bookId", input)
                .flatMap(bookDao::findBookById);

        if (bookOptional.isPresent()) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(bookOptional.get())
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                    .build();
        } else {
            return ApiGatewayResponse.builder()
                    .setStatusCode(404)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                    .build();
        }
    }
}
