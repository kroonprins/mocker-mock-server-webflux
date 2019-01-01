package kroonprins.mocker;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class LatencyTest extends AbstractTest {

    @Nested
    public class FixedLatency {

        @ParameterizedTest(name = "Test {index}")
        @ValueSource(ints = {1, 2, 3})
        public void fixed() {
            long start = System.currentTimeMillis();
            client.get()
                    .uri("/fixed-latency")
                    .exchange()
                    .expectStatus().isEqualTo(200);
            long elapsed = System.currentTimeMillis() - start;
            assertThat(elapsed).isBetween(1000L, 1200L);
        }

        @ParameterizedTest(name = "Test {index}")
        @ValueSource(longs = {1000, 2000, 3000})
        public void templated(long duration) {
            long start = System.currentTimeMillis();
            client.get()
                    .uri("/fixed-latency-jinjava-templating?value=" + duration)
                    .exchange()
                    .expectStatus().isEqualTo(200);
            long elapsed = System.currentTimeMillis() - start;
            assertThat(elapsed).isBetween(duration, duration + 200);
        }
    }

    @Nested
    public class RandomLatency {

        @ParameterizedTest(name = "Test {index}")
        @ValueSource(ints = {1, 2, 3})
        public void random() {
            long start = System.currentTimeMillis();
            client.get()
                    .uri("/random-latency")
                    .exchange()
                    .expectStatus().isEqualTo(200);
            long elapsed = System.currentTimeMillis() - start;
            assertThat(elapsed).isBetween(500L, 2200L);
        }

        @ParameterizedTest(name = "Test {index}")
        @ValueSource(ints = {1, 2, 3})
        public void randomNoMin() {
            long start = System.currentTimeMillis();
            client.get()
                    .uri("/random-latency-no-min")
                    .exchange()
                    .expectStatus().isEqualTo(200);
            long elapsed = System.currentTimeMillis() - start;
            assertThat(elapsed).isBetween(0L, 2200L);
        }


        @ParameterizedTest(name = "Test {index}")
        @ValueSource(longs = {1000, 2000, 3000})
        public void templated(long duration) {
            long start = System.currentTimeMillis();
            client.get()
                    .uri("/random-latency-jinjava-templating?min=" + (duration - 1000) + "&max=" + duration)
                    .exchange()
                    .expectStatus().isEqualTo(200);
            long elapsed = System.currentTimeMillis() - start;
            assertThat(elapsed).isBetween(duration - 1000, duration + 200);
        }
    }
}

