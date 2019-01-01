package kroonprins.mocker.templating.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Writer;

@Configuration
public class MustacheConfig {

    public static class NoEscapintMustacheFactory extends DefaultMustacheFactory {
        @SneakyThrows
        @Override
        public void encode(String value, Writer writer) {
            writer.write(value);
        }
    }

    @Bean
    public MustacheFactory mustacheFactory() {
        return new NoEscapintMustacheFactory();
    }
}
