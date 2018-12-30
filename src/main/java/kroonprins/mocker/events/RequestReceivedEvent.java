package kroonprins.mocker.events;

import kroonprins.mocker.model.Rule;
import lombok.Builder;
import lombok.Value;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Date;

@Builder
@Value
public class RequestReceivedEvent {
    private Date timestamp;
    private Rule rule;
    private ServerRequest req;
}
