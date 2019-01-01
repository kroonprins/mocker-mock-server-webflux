package kroonprins.mocker;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class FreeMarkerTemplatingTest extends AbstractTest {

    @Test
    public void basicTemplating() {
        client.post()
                .uri(builder -> builder
                        .path("/templating-freemarker/parameter1/parameter2")
                        .queryParam("contentType", "text/plain")
                        .queryParam("statusCode", 206)
                        .build())
                .header("x-secure", "true")
                .cookie("c1", "cookie1")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("{ \"a\": \"x\", \"b\": { \"c\": [ \"d\", \"e\" ] } }")
                .exchange()
                .expectStatus().isEqualTo(206)
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .expectHeader().valueEquals("X-header1", "header1")
                .expectHeader().valueEquals("X-header2", "cookie1")
                // Unable to check cookie response value for more cookies because they don't always come back in the same order.
                // Other options using a matcher or consumer also don't work because they only get access to one of the values.
                //.expectHeader().valueEquals("Set-Cookie", "cookie2=value2; HttpOnly", "cookie1=value1; Secure")
                .expectBody(String.class).isEqualTo("received json body with as value for property 'a': x, and second element in nested array: e. Path parameter p1: parameter1, parameter p2: parameter2.");
    }

    @Nested
    public class InputBodyTests {
        @Test
        public void templatingUsingJsonBody() {
            client.post()
                    .uri("/templating-freemarker-json-input")
                    .contentType(MediaType.APPLICATION_JSON)
                    .syncBody("{ \"a\": \"x\", \"b\": { \"c\": [ \"d\", \"e\" ] } }")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBody(String.class).isEqualTo("received json body with as value for property 'a': x, and second element in nested array: e");
        }

        @Test
        public void templatingUsingNonJsonBody() {
            client.post()
                    .uri("/templating-freemarker-non-json-input")
                    .contentType(MediaType.APPLICATION_XML)
                    .syncBody("<document><a>x</a><b><c>d</c><c>e</c></be></document>")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBody(String.class).isEqualTo("received non-json body: <document><a>x</a><b><c>d</c><c>e</c></be></document>");
        }
    }

    @Nested
    public class ConditionalResponseTests {
        @Test
        public void testCondition1() {
            client.get()
                    .uri("/conditional-freemarker?id=id1")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                    .expectHeader().valueEquals("X-id", "id1")
                    .expectBody().json("{ \"data\": \"id1's data\" }");
        }

        @Test
        public void testCondition2() {
            client.get()
                    .uri("/conditional-freemarker?id=id2")
                    .exchange()
                    .expectStatus().isEqualTo(206)
                    .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                    .expectHeader().doesNotExist("X-id")
                    .expectBody().json("{ \"data\": \"id2's data\" }");
        }

        @Test
        public void testFallbackCondition() {
            client.get()
                    .uri("/conditional-freemarker?id=idx")
                    .exchange()
                    .expectStatus().isEqualTo(404)
                    .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                    .expectHeader().doesNotExist("X-id")
                    .expectBody(String.class).isEqualTo("The item with id 'idx' does not exist");
        }
    }
}

