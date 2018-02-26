package com.serverless.gateway;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.serverless.gateway.Resource.HttpMethod;
import static java.util.stream.Collectors.toList;

public class ResourceHandler {

    private static final Logger LOGGER = LogManager.getLogger(ResourceHandler.class);

    public static List<Resource> loadEndPoints() {
        try {
            File file = new File(new File(".").getCanonicalPath() + "/serverless.yml");
            Yaml yaml = new Yaml();
            Map<String, LinkedHashMap<String, LinkedHashMap>> load = yaml.load(new FileInputStream(file));
            return load.get("functions")
                    .values()
                    .stream()
                    .flatMap(extractHandlers())
                    .collect(toList());
        } catch (Exception e) {
            LOGGER.error(e);
            return Collections.emptyList();
        }
    }

    private static Function<LinkedHashMap, Stream<? extends Resource>> extractHandlers() {
        return function -> {
            Class<RequestHandler> clazz = (Class<RequestHandler>) Try.of(() -> Class.forName((String) function.get("handler")))
                    .get();
            List<LinkedHashMap> events = (List<LinkedHashMap>) function.get("events");
            return events.stream()
                    .map(event -> {
                        LinkedHashMap http = (LinkedHashMap) event.get("http");
                        String path = (String) http.get("path");
                        String method = (String) http.get("method");
                        if (method.equalsIgnoreCase("ANY")) {
                            return Arrays.stream(HttpMethod.values())
                                    .map(httpMethod -> new Resource(httpMethod, path, clazz))
                                    .collect(toList());
                        } else {
                            return Collections.singletonList(new Resource(HttpMethod.valueOf(method.toUpperCase()), path, clazz));
                        }
                    })
                    .flatMap(List::stream);
        };
    }

}
