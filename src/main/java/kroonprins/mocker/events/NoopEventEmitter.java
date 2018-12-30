package kroonprins.mocker.events;

import reactor.core.publisher.Flux;

public class NoopEventEmitter implements EventEmitter {
    @Override
    public void emit(RequestReceivedEvent event) {
    }

    @Override
    public Flux<RequestReceivedEvent> getFlux() {
        return Flux.empty();
    }
}
