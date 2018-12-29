package kroonprins.mocker.templating.jinjava;

import kroonprins.mocker.templating.TemplatingContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

public class JinjavaTemplatingContext extends HashMap<String, Object> implements TemplatingContext {

    public static JinjavaTemplatingContext fromServerRequest(ServerRequest serverRequest) {
        JinjavaTemplatingContext context = new JinjavaTemplatingContext();
        context.put("req", createRequestContext(serverRequest));
        context.put("res", createResponseContext(serverRequest));

        return context;
    }

    private static Map<String, Object> createRequestContext(ServerRequest serverRequest) {
        Map<String, Object> request = new HashMap<>();
        request.put("path", serverRequest.path());
        request.put("method", serverRequest.method());
        request.put("query", serverRequest.queryParams().toSingleValueMap()); // TODO toSingleValueMap means possibly losing data
        return request;
    }

    private static Object createResponseContext(ServerRequest serverRequest) {
        return null;
    }
}
