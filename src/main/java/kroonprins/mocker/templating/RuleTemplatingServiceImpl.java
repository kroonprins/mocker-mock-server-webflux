package kroonprins.mocker.templating;

import kroonprins.mocker.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("with-threading")
public class RuleTemplatingServiceImpl implements RuleTemplatingService {
    private TemplatingService templatingService;

    public RuleTemplatingServiceImpl(TemplatingService templatingService) {
        this.templatingService = templatingService;
    }

    @Override
    public Mono<TemplatedRule> template(Rule rule, TemplatingContext context) {
        TemplatingEngines templatingEngine = rule.getResponse().getTemplatingEngine();
        log.debug("Templating with engine {}", templatingEngine);

        Mono<TemplatedResponse> response = templateResponse(rule.getResponse(), templatingEngine, context);

        return Mono.zip(templatedValues -> {
            log.debug("Templating done: {}", templatedValues);
            return createTemplatedRule(rule, templatedValues);
        }, response);
    }

    private Mono<Optional<String>> template(String toTemplate, TemplatingEngines templatingEngine, TemplatingContext context) {
        if (TemplatingEngines.NONE.equals(templatingEngine) || StringUtils.isBlank(toTemplate)) {
            return Mono.just(Optional.ofNullable(toTemplate));
        } else {
            return Mono.fromCallable(() -> {
                log.debug("Templating with engine {}: {}", templatingEngine, toTemplate);
                return Optional.of(templatingService.render(templatingEngine, toTemplate, context));
            }).subscribeOn(Schedulers.parallel());
        }
    }

    private Mono<TemplatedResponse> templateResponse(Response response, TemplatingEngines templatingEngine, TemplatingContext context) {
        // TODO if null => Optional
        Mono<Optional<FixedLatency>> fixedLatency = templateFixedLatency(response.getFixedLatency(), templatingEngine, context);
        Mono<Optional<RandomLatency>> randomLatency = templateRandomLatency(response.getRandomLatency(), templatingEngine, context);
        Mono<Optional<String>> statusCode = template(response.getStatusCode(), templatingEngine, context);
        Mono<Optional<String>> contentType = template(response.getContentType(), templatingEngine, context);
        Mono<List<Header>> headers = templateHeaders(response.getHeaders(), templatingEngine, context);
        Mono<List<Cookie>> cookies = templateCookies(response.getCookies(), templatingEngine, context);
        Mono<Optional<String>> body = template(response.getBody(), templatingEngine, context);

        return Mono.zip(templatedValues -> {
            log.debug("Templating done: {}", templatedValues);
            return createTemplatedResponse(templatedValues);
        }, fixedLatency, randomLatency, statusCode, contentType, headers, cookies, body);
    }

    private Mono<Optional<FixedLatency>> templateFixedLatency(FixedLatency fixedLatency, TemplatingEngines templatingEngine, TemplatingContext context) {
        if (fixedLatency == null) {
            return Mono.just(Optional.ofNullable(fixedLatency));
        }

        return template(fixedLatency.getValue(), templatingEngine, context)
                .map(value -> Optional.of(
                        FixedLatency.builder()
                                .value(value.get())
                                .build()
                        )
                );
    }

    private Mono<Optional<RandomLatency>> templateRandomLatency(RandomLatency randomLatency, TemplatingEngines templatingEngine, TemplatingContext context) {
        if (randomLatency == null) {
            return Mono.just(Optional.ofNullable(randomLatency));
        }

        Mono<Optional<String>> min = template(randomLatency.getMin(), templatingEngine, context);
        Mono<Optional<String>> max = template(randomLatency.getMax(), templatingEngine, context);

        return Mono.zip(min, max, (templatedMin, templatedMax) ->
                Optional.of(
                        RandomLatency.builder()
                                .min(templatedMin.orElse(null))
                                .max(templatedMax.get())
                                .build()
                )
        );
    }

    private Mono<List<Header>> templateHeaders(List<Header> headers, TemplatingEngines templatingEngine, TemplatingContext context) {
        if (headers == null) {
            return Mono.just(new ArrayList<>());
        }
        Flux<Optional<String>> names = Flux.fromStream(headers.stream())
                .map(Header::getName)
                .flatMap(name -> template(name, templatingEngine, context));
        Flux<Optional<String>> values = Flux.fromStream(headers.stream())
                .map(Header::getValue)
                .flatMap(value -> template(value, templatingEngine, context));
        return names.zipWith(values)
                .map(zipped -> Header.builder()
                        .name(zipped.getT1().get())
                        .value(zipped.getT2().orElse(null))
                        .build())
                .collectList();
    }

    private Mono<List<Cookie>> templateCookies(List<Cookie> cookies, TemplatingEngines templatingEngine, TemplatingContext context) {
        if (cookies == null) {
            return Mono.just(new ArrayList<>());
        }
        Flux<Optional<String>> names = Flux.fromStream(cookies.stream())
                .map(Cookie::getName)
                .flatMap(name -> template(name, templatingEngine, context));
        Flux<Optional<String>> values = Flux.fromStream(cookies.stream())
                .map(Cookie::getValue)
                .flatMap(value -> template(value, templatingEngine, context));
        Flux<Optional<CookieProperties>> cookiePropertiesFlux = Flux.fromStream(cookies.stream())
                .map(Cookie::getProperties)
                .flatMap(cookieProperties -> templateCookieProperties(cookieProperties, templatingEngine, context));
        return Flux.zip(names, values, cookiePropertiesFlux)
                .map(zipped -> Cookie.builder()
                        .name(zipped.getT1().get())
                        .value(zipped.getT2().orElse(null))
                        .properties(zipped.getT3().orElse(null))
                        .build())
                .collectList();
    }

    private Mono<Optional<CookieProperties>> templateCookieProperties(CookieProperties cookieProperties, TemplatingEngines templatingEngine, TemplatingContext context) {
        if (cookieProperties == null) {
            return Mono.just(Optional.ofNullable(cookieProperties));
        }

        Mono<Optional<String>> domain = template(cookieProperties.getDomain(), templatingEngine, context);
        Mono<Optional<String>> httpOnly = template(cookieProperties.getHttpOnly(), templatingEngine, context);
        Mono<Optional<String>> maxAge = template(cookieProperties.getMaxAge(), templatingEngine, context);
        Mono<Optional<String>> path = template(cookieProperties.getPath(), templatingEngine, context);
        Mono<Optional<String>> secure = template(cookieProperties.getSecure(), templatingEngine, context);
        Mono<Optional<String>> sameSite = template(cookieProperties.getSameSite(), templatingEngine, context);

        return Mono.zip(templatedValues ->
                        Optional.of(
                                CookieProperties.builder()
                                        .domain(((Optional<String>) templatedValues[0]).orElse(null))
                                        .httpOnly(((Optional<String>) templatedValues[1]).orElse(null))
                                        .maxAge(((Optional<String>) templatedValues[2]).orElse(null))
                                        .path(((Optional<String>) templatedValues[3]).orElse(null))
                                        .secure(((Optional<String>) templatedValues[4]).orElse(null))
                                        .sameSite(((Optional<String>) templatedValues[5]).orElse(null))
                                        .build()
                        )
                , domain, httpOnly, maxAge, path, secure, sameSite);
    }

    private TemplatedRule createTemplatedRule(Rule rule, Object[] templatedValues) {
        TemplatedResponse templatedResponse = (TemplatedResponse) templatedValues[0];

        return TemplatedRule.builder()
                .name(rule.getName())
                .request(TemplatedRequest.from(rule.getRequest()))
                .response(templatedResponse)
                .build();
    }

    private TemplatedResponse createTemplatedResponse(Object[] templatedValues) {
        FixedLatency fixedLatency = ((Optional<FixedLatency>) templatedValues[0]).orElse(null);
        RandomLatency randomLatency = ((Optional<RandomLatency>) templatedValues[1]).orElse(null);
        HttpStatus statusCode = HttpStatus.valueOf(Integer.parseInt((((Optional<String>) templatedValues[2]).get())));
        MediaType contentType = ((Optional<String>) templatedValues[3]).map(MediaType::valueOf).orElse(null);
        List<Header> headers = (List<Header>) templatedValues[4];
        List<Cookie> cookies = (List<Cookie>) templatedValues[5];
        String body = ((Optional<String>) templatedValues[6]).orElse(null);

        return TemplatedResponse.builder()
                .fixedLatency(TemplatedFixedLatency.from(fixedLatency))
                .randomLatency(TemplatedRandomLatency.from(randomLatency))
                .statusCode(statusCode)
                .contentType(contentType)
                .headers(headers.stream().map(TemplatedHeader::from).collect(Collectors.toList()))
                .cookies(cookies.stream().map(TemplatedCookie::from).collect(Collectors.toList()))
                .body(body)
                .build();
    }
}
