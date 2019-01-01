package kroonprins.mocker;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public abstract class AbstractTest {

    @Autowired
    WebTestClient client;

    protected String readFileFromClasspath(String file) {
        try (InputStream in = new ClassPathResource(file, getClass()).getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Unable to read file %s from classpath", file), e);
        }
    }
}
