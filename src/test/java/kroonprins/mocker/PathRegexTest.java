package kroonprins.mocker;


import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class PathRegexTest extends AbstractTest {

    @Test
    public void testRegexMatch() {
        client.get()
                .uri("/regex/abe")
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .expectBody(String.class).isEqualTo("this rule's response");
    }

    @Test
    public void testAnotherRegexMatch1() {
        client.get()
                .uri("/regex/abcde")
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .expectBody(String.class).isEqualTo("this rule's response");
    }

}

