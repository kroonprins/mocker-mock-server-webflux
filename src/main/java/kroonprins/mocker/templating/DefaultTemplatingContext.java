package kroonprins.mocker.templating;

import kroonprins.mocker.templating.model.Request;
import kroonprins.mocker.templating.model.Response;
import lombok.Builder;
import lombok.Value;
import org.springframework.web.reactive.function.server.ServerRequest;

@Builder
@Value
public class DefaultTemplatingContext implements TemplatingContext {
    private final Request req;
    private final Response res;

    public static DefaultTemplatingContext fromServerRequest(ServerRequest serverRequest) {
        // TODO add more
        return DefaultTemplatingContext.builder()
                .req(
                        Request.builder()
                                .path(serverRequest.path())
                                .method(serverRequest.method())
                                .query(serverRequest.queryParams().toSingleValueMap()) // TODO toSingleValueMap means possibly losing data
                                .build()
                )
                .res(
                        Response.builder()
                                .build()
                )
                .build();
    }
}
