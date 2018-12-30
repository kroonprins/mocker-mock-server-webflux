package kroonprins.mocker.events;

import reactor.core.publisher.Flux;

public interface EventEmitter {

    void emit(RequestReceivedEvent event);

    Flux<RequestReceivedEvent> getFlux();
}
