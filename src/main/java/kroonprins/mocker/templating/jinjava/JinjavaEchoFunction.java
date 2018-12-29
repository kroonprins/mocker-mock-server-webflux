package kroonprins.mocker.templating.jinjava;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JinjavaEchoFunction extends AbstractJinjavaFunction {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected String getName() {
        return "echo";
    }

    @Override
    protected String getMethod() {
        return "echo";
    }

    @Override
    protected Class[] getParameterTypes() {
        return new Class[]{Map.class};
    }

    @SneakyThrows
    public static String echo(Map<String, Object> request) {
        return mapper.writeValueAsString(
                EchoResponse.builder()
                        .method((HttpMethod) request.get("method")) // TODO meh
                        .path((String) request.get("path"))
                        .build()
        );
    }

    @Builder
    @Value
    public static class EchoResponse {
        private final HttpMethod method;
        private final String path;
    }
}
