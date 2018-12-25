package kroonprins.mocker.templating;

import kroonprins.mocker.templating.model.Request;
import kroonprins.mocker.templating.model.Response;
import lombok.Builder;
import lombok.Value;
import org.springframework.web.reactive.function.server.ServerRequest;

@Builder
@Value
public class TemplatingContext {
    private final Request req;
    private final Response res;

    public static TemplatingContext fromServerRequest(ServerRequest serverRequest) {
        return TemplatingContext.builder()
                .req(
                        Request.builder()
                                .path(serverRequest.path())
                                .method(serverRequest.method())
                                .query(serverRequest.queryParams().toSingleValueMap()) // TODO toSingleValueMap means possibly using data
                                .build()
                )
                .res(
                        Response.builder()
                                .build()
                )
                .build();
    }
}
