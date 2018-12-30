package kroonprins.mocker.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import kroonprins.mocker.events.EventEmitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class EventListener {
    private final EventEmitter eventEmitter;
    private final MeterRegistry meterRegistry;

    public EventListener(EventEmitter eventEmitter, MeterRegistry meterRegistry) {
        this.eventEmitter = eventEmitter;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void listen() {
        this.eventEmitter.getFlux()
                .publishOn(Schedulers.parallel())
                .doOnNext(event -> {
                    log.debug("event {}", event);
                    this.meterRegistry.counter("mocker.metrics.requests", Tags.of("rule", event.getRule().getName())).increment();
                })
                .subscribe();
    }

}
