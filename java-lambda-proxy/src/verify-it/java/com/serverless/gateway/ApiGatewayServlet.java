package com.serverless.gateway;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ApiGatewayServlet extends HttpServlet {

    private final Logger logger = LogManager.getLogger(ApiGatewayServlet.class);

    private final List<Resource> endpoints;

    public ApiGatewayServlet() {
        endpoints = ResourceHandler.loadEndPoints();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod().toUpperCase();
        String requestUri = req.getRequestURI();

        logger.info("endpoint : " + requestUri);

        String[] uriParts = requestUri.split("/");

        List<Resource> resources = findResourcesMatch(method, uriParts);

        if (resources.isEmpty()) {
            String message = "Could not find any lambda function associated to the path: " + requestUri;
            logger.error(message);
            resp.setContentType("application/json; charset=UTF-8");
            resp.setStatus(403);
            resp.getWriter().print(message);
        } else if (resources.size() > 1) {
            String message = "find multiple lambda function associated to the same path: " + requestUri;
            logger.error(message);
            resp.setContentType("application/json; charset=UTF-8");
            resp.setStatus(500);
            resp.getWriter().print(message);
        } else {
            Resource endPoint = resources.get(0);
            Map<String, Object> callParameters = new HashMap<>();
            callParameters.put("resource", endPoint.getUrl());
            callParameters.put("path", requestUri);
            callParameters.put("httpMethod", method);

            String body = req.getReader().lines().collect(Collectors.joining());
            if (isNotBlank(body)) {
                callParameters.put("body", body);
            }

            callParameters.put("pathParameters", buildPathParameters(endPoint.getUrlItems(), requestUri));

            callParameters.put("queryStringParameters", buildQueryParameters(req));

            callParameters.put("headers", emulateApiGatewayHeaders(req));

            try {
                RequestHandler requestHandler = endPoint.getHandler().newInstance();
                ApiGatewayResponse handlerResponse = (ApiGatewayResponse) requestHandler.handleRequest(callParameters, null);
                buildResponse(resp, handlerResponse);
            } catch (Exception e) {
                String message = "add error occurred when call resource : " + requestUri;
                logger.error(message, e);
                resp.setContentType("application/json; charset=UTF-8");
                resp.setStatus(500);
                resp.getWriter().print(message);
            }
        }
    }

    private void buildResponse(HttpServletResponse resp, ApiGatewayResponse handlerResponse) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        // emulate the gateway API response headers
        resp.addHeader("date", "Wed, 31 May 2017 15:11:39 GMT");
        resp.addHeader("x-amzn-requestid", "726a95f3-4613-11e7-ae8f-f9f788cc0a8f");
        resp.addHeader("x-amzn-trace-id", "sampled=0;root=1-592edd2b-a20c24f6eeb62f63a4977849");
        resp.addHeader("x-cache", "Error from cloudfront");
        resp.addHeader("via", "1.1 54073dd9095b9ef12d7cdaefb0bcc12c.cloudfront.net (CloudFront)");
        resp.addHeader("x-amz-cf-id", "FA8PcHW-HpB3mziDqB49c4lzX8xrG9b6eVZfAWPAVAdY-5KhDzUC1g==");
        handlerResponse.getHeaders().forEach(resp::addHeader);

        resp.setStatus(handlerResponse.getStatusCode());
        if (handlerResponse.getBody() != null) {
            resp.getWriter().print(handlerResponse.getBody());
        }
    }

    private Map<String, String> buildQueryParameters(HttpServletRequest req) {
        return Optional.ofNullable(req.getParameterMap())
                .orElseGet(Collections::emptyMap)
                .entrySet()
                .stream()
                // API gateway actually gives only the last value
                // for a given parameter name, so let's emulate this behavior
                .collect(toMap(Map.Entry::getKey, param -> param.getValue()[param.getValue().length - 1]));
    }

    private List<Resource> findResourcesMatch(String method, String[] uriParts) {
        return endpoints.stream()
                .filter(endPoint -> endPoint.getHttpMethod().name().equalsIgnoreCase(method))
                .filter(endPoint -> endPoint.getUrlItems().size() == uriParts.length)
                .filter(endPoint ->
                        Stream.ofAll(endPoint.getUrlItems())
                                .corresponds(Arrays.asList(uriParts), isUrlMatch()))
                .collect(Collectors.toList());
    }

    private BiPredicate<UrlItem, String> isUrlMatch() {
        return (val, nextUri) -> val.getType().equals(UrlItem.UrlItemType.PathParam) || val.getName().equals(nextUri);
    }

    private Map<String, String> buildPathParameters(List<UrlItem> templateItems, String uri) {
        String[] uriParts = uri.split("/");

        return Stream.ofAll(templateItems)
                .zipWithIndex()
                .filter(part -> part._1().getType() == UrlItem.UrlItemType.PathParam)
                .toJavaMap(part -> new Tuple2<>(part._1.getName(), uriParts[part._2]));
    }

    private Map<String, String> emulateApiGatewayHeaders(HttpServletRequest req) {
        Map<String, String> gatewayHeaders = new HashMap<>();
        gatewayHeaders.put("Accept", "*/*");
        gatewayHeaders.put("Accept-Encoding", "gzip,deflate");
        gatewayHeaders.put("CloudFront-Forwarded-Proto", "https");
        gatewayHeaders.put("CloudFront-Is-Desktop-Viewer", "true");
        gatewayHeaders.put("CloudFront-Is-Mobile-Viewer", "false");
        gatewayHeaders.put("CloudFront-Is-SmartTV-Viewer", "false");
        gatewayHeaders.put("CloudFront-Is-Tablet-Viewer", "false");
        gatewayHeaders.put("CloudFront-Viewer-Country", "FR");
        gatewayHeaders.put("Content-Type", "application/json; charset=UTF-8");
        gatewayHeaders.put("Host", req.getServerName());
        gatewayHeaders.put("User-Agent", "Apache-HttpClient/4.5.1 (Java/1.8.0_131)");
        gatewayHeaders.put("Via", "1.1 16291083b92e5aa4f2f272f1da69c5e4.cloudfront.net (CloudFront)");
        gatewayHeaders.put("X-Amz-Cf-Id", "TIHPZoMaJ8s2UYvCheicpxS8VUDKl46i9aGIDOloj6OMLWlstQ8sUw==");
        gatewayHeaders.put("X-Amzn-Trace-Id", "Root=1-592ed349-1a24a0237901e0571b0dfd16");
        gatewayHeaders.put("X-Forwarded-For", "93.154.70.227, 55.240.147.67");
        gatewayHeaders.put("X-Forwarded-Port", Integer.toString(req.getServerPort()));
        gatewayHeaders.put("X-Forwarded-Proto", req.getScheme());
        return gatewayHeaders;
    }
}
