package kroonprins.mocker.templating.jinjava;

import com.hubspot.jinjava.Jinjava;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class JinjavaConfig {

    @Bean
    public Jinjava jinjava(List<JinjavaFunction> jinjavaFunctions) {
        Jinjava jinjava = new Jinjava();

        for(JinjavaFunction jinjavaFunction : jinjavaFunctions) {
            jinjava.getGlobalContext().registerFunction(jinjavaFunction.create());
        }

        return jinjava;
    }
}
