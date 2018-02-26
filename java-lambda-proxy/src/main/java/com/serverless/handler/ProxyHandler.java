package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.gateway.ApiGatewayResponse;
import com.serverless.gateway.Resource;
import com.serverless.gateway.UrlItem;
import io.vavr.collection.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import static com.serverless.gateway.Resource.HttpMethod.GET;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

public class ProxyHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger LOGGER = LogManager.getLogger(ProxyHandler.class);

    private static List<Resource> resources = new ArrayList<>();

    static {
        resources.add(new Resource(GET, "/books", BooksGetHandler.class));
        resources.add(new Resource(GET, "/books/{bookId}", BookGetHandler.class));
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        String method = ((String) input.get("httpMethod")).toUpperCase();
        String requestUri = (String) input.get("path");
        LOGGER.info("method=" + method + "  path=" + requestUri);

        String[] uriParts = requestUri.split("/");
        List<Resource> resources = findResourcesMatch(method, uriParts);

        if (resources.isEmpty()) {
            String message = "Could not find any lambda function associated to the path: " + requestUri;
            LOGGER.error(message);
            return ApiGatewayResponse.builder()
                    .setStatusCode(404)
                    .setObjectBody(message)
                    .setHeaders(singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                    .build();
        } else if (resources.size() > 1) {
            String message = "find multiple lambda function associated to the same path: " + requestUri;
            LOGGER.error(message);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(message)
                    .setHeaders(singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                    .build();
        } else {
            Resource resource = resources.get(0);

            try {
                RequestHandler requestHandler = resource.getHandler().newInstance();
                return (ApiGatewayResponse) requestHandler.handleRequest(input, context);
            } catch (Exception e) {
                String message = "error occurred when call resource : " + requestUri;
                LOGGER.error(message, e);
                return ApiGatewayResponse.builder()
                        .setStatusCode(500)
                        .setObjectBody(message)
                        .setHeaders(singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                        .build();
            }
        }
    }

    private List<Resource> findResourcesMatch(String method, String[] uriParts) {
        return resources.stream()
                .filter(endPoint -> endPoint.getHttpMethod().name().equalsIgnoreCase(method))
                .filter(endPoint -> endPoint.getUrlItems().size() == uriParts.length)
                .filter(endPoint ->
                        Stream.ofAll(endPoint.getUrlItems())
                                .corresponds(Arrays.asList(uriParts), isUrlMatch()))
                .collect(toList());
    }

    private BiPredicate<UrlItem, String> isUrlMatch() {
        return (val, nextUri) -> val.getType().equals(UrlItem.UrlItemType.PathParam) || val.getName().equals(nextUri);
    }
}
