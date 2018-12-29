package kroonprins.mocker.templating.jinjava;

import com.hubspot.jinjava.Jinjava;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JinjavaConfig {

    @Bean
    public Jinjava jinjava() {
        return new Jinjava();
    }
}
