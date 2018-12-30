package kroonprins.mocker.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventEmitterConfig {

    @Bean
    @ConditionalOnProperty(prefix = "mocker", name = "metrics.enabled", havingValue = "true", matchIfMissing = true)
    public EventEmitter defaultEventEmitter() {
        return new DefaultEventEmitter();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventEmitter noopEventEmitter() {
        return new NoopEventEmitter();
    }
}
