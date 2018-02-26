package com.serverless.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public class ParamsHelper {

    private static final Logger LOGGER = LogManager.getLogger(ParamsHelper.class);

    public static Optional<String> getPathParameter(String name, Map<String, Object> input) {
        Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
        if (pathParameters != null) {
            return Optional.ofNullable(pathParameters.get(name));
        }
        return Optional.empty();
    }


    public static Optional<String> getQueryParameter(String name, Map<String, Object> input) {
        Map<String, String> queryParams = (Map<String, String>) input.get("queryStringParameters");
        if (queryParams != null) {
            return Optional.ofNullable(queryParams.get(name));
        }
        return Optional.empty();
    }


    /**
     * extract the origin uri
     * from the api gateway parameters. Useful when paginating, as we need to send
     * back the next url to retrieve data.
     */
    public static String extractOriginUrl(Map<String, Object> input) {

        String resource = (String) input.get("resource");
        String path = (String) input.get("path");

        Map<String, Object> requestHeaders = (Map<String, Object>) input.get("headers");
        String port = (String) requestHeaders.get("X-Forwarded-Port");
        String proto = (String) requestHeaders.get("X-Forwarded-Proto");
        String host = (String) requestHeaders.get("Host");

        // the path has one more component than the resource when the service
        // is accessed using a prefix - like the user name. This is the way multiple deployements
        // of the API are segregated. However in local testing, there's no such prefix
        String[] splitResource = resource.split("/");
        String[] splitPath = path.split("/");
        String prefix = "";
        if (splitPath.length > splitResource.length) {
            prefix = "/" + splitPath[1];
        }
        return proto + "://" + host + ":" + port + prefix;
    }
}
