package kroonprins.mocker;


import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class NoTemplatingTest extends AbstractTest {

    @Test
    public void aVeryBasicRule() {
        client.get()
                .uri("/get")
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .expectBody(String.class).isEqualTo("the response body");
    }

    @Test
    public void aVeryBasicRuleValidatingThatTemplatingSyntaxIsNotReplaced() {
        client.get()
                .uri("/get-without-templating")
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .expectBody(String.class).isEqualTo("the response body {{req.path}}");
    }

}

