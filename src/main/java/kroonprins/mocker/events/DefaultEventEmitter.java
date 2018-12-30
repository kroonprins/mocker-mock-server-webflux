package kroonprins.mocker.events;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.UnicastProcessor;

public class DefaultEventEmitter implements EventEmitter {
    // unicast because currently only one listener foreseen
    private final UnicastProcessor<RequestReceivedEvent> receivedRequest;
    private final FluxSink<RequestReceivedEvent> sink;

    public DefaultEventEmitter() {
        receivedRequest = UnicastProcessor.create();
        sink = receivedRequest.sink();
    }

    public void emit(RequestReceivedEvent event) {
        sink.next(event);
    }

    public Flux<RequestReceivedEvent> getFlux() {
        return receivedRequest;
    }
}
