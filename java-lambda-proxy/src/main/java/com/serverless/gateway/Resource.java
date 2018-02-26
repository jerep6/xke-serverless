package com.serverless.gateway;

import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Resource {
    private final HttpMethod httpMethod;
    private final String url;
    private final Class<? extends RequestHandler> handler;
    private List<UrlItem> urlItems;

    public Resource(HttpMethod httpMethod, String url, Class<? extends RequestHandler> handler) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.handler = handler;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public Class<? extends RequestHandler> getHandler() {
        return handler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource endPoint = (Resource) o;

        if (httpMethod != endPoint.httpMethod) return false;
        return url != null ? url.equals(endPoint.url) : endPoint.url == null;
    }

    @Override
    public int hashCode() {
        int result = httpMethod != null ? httpMethod.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    public List<UrlItem> getUrlItems() {
        if (urlItems == null) {
            urlItems = Arrays.stream(url.split("/"))
                    .map(part -> {
                        UrlItem urlItem = new UrlItem();
                        if (part.startsWith("{") && part.endsWith("}")) {
                            urlItem.setType(UrlItem.UrlItemType.PathParam);
                            urlItem.setName(part.substring(1, part.length() - 1));
                        } else {
                            urlItem.setType(UrlItem.UrlItemType.PathElement);
                            urlItem.setName(part);
                        }
                        return urlItem;
                    })
                    .collect(toList());
        }
        return urlItems;
    }

    @Override
    public String toString() {
        return "EndPoint{" +
                "httpMethod=" + httpMethod +
                ", url='" + url + '\'' +
                ", handler=" + handler +
                '}';
    }

    public enum HttpMethod {
        GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;
    }
}
