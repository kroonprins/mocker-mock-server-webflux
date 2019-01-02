package kroonprins.mocker;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class LatencyTest extends AbstractTest {

    @Nested
    public class FixedLatency {

        @ParameterizedTest(name = "Test {index}")
        @ValueSource(ints = {1, 2, 3})
        public void fixed() {
            Instant start = Instant.now();
            client.get()
                    .uri("/fixed-latency")
                    .exchange()
                    .expectStatus().isEqualTo(200);
            assertThat(Duration.between(start, Instant.now())).isBetween(Duration.ofMillis(1000L), Duration.ofMillis(1200L));
        }

        @ParameterizedTest(name = "Test {index}")
        @ValueSource(longs = {1000, 2000, 3000})
        public void templated(long duration) {
            Instant start = Instant.now();
            client.get()
                    .uri("/fixed-latency-jinjava-templating?value=" + duration)
                    .exchange()
                    .expectStatus().isEqualTo(200);
            assertThat(Duration.between(start, Instant.now())).isBetween(Duration.ofMillis(duration), Duration.ofMillis(duration).plusMillis(200));
        }
    }

    @Nested
    public class RandomLatency {

        @ParameterizedTest(name = "Test {index}")
        @ValueSource(ints = {1, 2, 3})
        public void random() {
            Instant start = Instant.now();
            client.get()
                    .uri("/random-latency")
                    .exchange()
                    .expectStatus().isEqualTo(200);
            assertThat(Duration.between(start, Instant.now())).isBetween(Duration.ofMillis(500L), Duration.ofMillis(2200L));
        }

        @ParameterizedTest(name = "Test {index}")
        @ValueSource(ints = {1, 2, 3})
        public void randomNoMin() {
            Instant start = Instant.now();
            client.get()
                    .uri("/random-latency-no-min")
                    .exchange()
                    .expectStatus().isEqualTo(200);
            assertThat(Duration.between(start, Instant.now())).isBetween(Duration.ofMillis(0L), Duration.ofMillis(2200L));
        }


        @ParameterizedTest(name = "Test {index}")
        @ValueSource(longs = {1000, 2000, 3000})
        public void templated(long duration) {
            Instant start = Instant.now();
            client.get()
                    .uri("/random-latency-jinjava-templating?min=" + (duration - 1000) + "&max=" + duration)
                    .exchange()
                    .expectStatus().isEqualTo(200);
            assertThat(Duration.between(start, Instant.now())).isBetween(Duration.ofMillis(duration).minusMillis(1000L), Duration.ofMillis(duration).plusMillis(200));
        }
    }
}

