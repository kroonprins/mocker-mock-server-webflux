package kroonprins.mocker;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class JinjavaTemplatingTest extends AbstractTest {

    @Test
    public void basicTemplating() {
        client.post()
                .uri(builder -> builder
                        .path("/templating-jinjava/parameter1/parameter2")
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
                    .uri("/templating-jinjava-json-input")
                    .contentType(MediaType.APPLICATION_JSON)
                    .syncBody("{ \"a\": \"x\", \"b\": { \"c\": [ \"d\", \"e\" ] } }")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBody(String.class).isEqualTo("received json body with as value for property 'a': x, and second element in nested array: e");
        }

        @Test
        public void templatingUsingNonJsonBody() {
            client.post()
                    .uri("/templating-jinjava-non-json-input")
                    .contentType(MediaType.APPLICATION_XML)
                    .syncBody("<document><a>x</a><b><c>d</c><c>e</c></be></document>")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBody(String.class).isEqualTo("received non-json body: <document><a>x</a><b><c>d</c><c>e</c></be></document>");
        }
    }

    @Nested
    public class EchoServerTests {
        @Test
        public void echoServerNoInputBody() {
            client.post()
                    .uri(builder -> builder
                            .path("/templating-jinjava-helper-echo")
                            .queryParam("q1", "query1") // TODO update test with multivalue query parameter once implemented
                            .queryParam("q2", "query2")
                            .build())
                    .header("x-h1", "header1")
                    .header("x-h2", "header2")
                    .cookie("c1", "cookie1")
                    .cookie("c2", "cookie2")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .consumeWith(response -> {
                        String responseBody = new String(response.getResponseBodyContent());
                        assertThatJson(responseBody).isEqualTo(readFileFromClasspath("echoServerNoInputBody.json"));
                    });
        }


        @Test
        public void echoServerJsonInputBody() {
            client.post()
                    .uri("/templating-jinjava-helper-echo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .syncBody("{ \"a\": \"x\", \"b\": { \"c\": [ \"d\", \"e\" ] } }")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .consumeWith(response -> {
                        String responseBody = new String(response.getResponseBodyContent());
                        assertThatJson(responseBody).isEqualTo(readFileFromClasspath("echoServerJsonInputBody.json"));
                    });
        }

        @Test
        public void echoServerNonJsonInputBody() {
            client.post()
                    .uri("/templating-jinjava-helper-echo")
                    .contentType(MediaType.APPLICATION_XML)
                    .syncBody("<document><a>x</a><b><c>d</c><c>e</c></be></document>")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .consumeWith(response -> {
                        String responseBody = new String(response.getResponseBodyContent());
                        assertThatJson(responseBody).isEqualTo(readFileFromClasspath("echoServerNonJsonInputBody.json"));
                    });
        }
    }

    @Nested
    public class ConditionalResponseTests {
        @Test
        public void testCondition1() {
            client.get()
                    .uri("/conditional-jinjava?id=id1")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                    .expectHeader().valueEquals("X-id", "id1")
                    .expectBody().json("{ \"data\": \"id1's data\" }");
        }

        @Test
        public void testCondition2() {
            client.get()
                    .uri("/conditional-jinjava?id=id2")
                    .exchange()
                    .expectStatus().isEqualTo(206)
                    .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                    .expectHeader().doesNotExist("X-id")
                    .expectBody().json("{ \"data\": \"id2's data\" }");
        }

        @Test
        public void testFallbackCondition() {
            client.get()
                    .uri("/conditional-jinjava?id=idx")
                    .exchange()
                    .expectStatus().isEqualTo(404)
                    .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                    .expectHeader().doesNotExist("X-id")
                    .expectBody(String.class).isEqualTo("The item with id 'idx' does not exist");
        }
    }
}

