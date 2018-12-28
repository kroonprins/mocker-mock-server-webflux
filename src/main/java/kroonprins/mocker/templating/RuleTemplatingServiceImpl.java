package kroonprins.mocker.templating;

import kroonprins.mocker.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        Mono<Optional<Response>> response = templateResponse(rule.getResponse(), context);
        Mono<Optional<ConditionalResponseValue>> conditionalResponseValue = templateConditionalResponse(rule.getConditionalResponse(), context);

        return Mono.zip(templatedValues -> {
            log.debug("Templating done: {}", templatedValues);
            return createTemplatedRule(rule, templatedValues);
        }, response, conditionalResponseValue);
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

    private Mono<Optional<Response>> templateResponse(Response response, TemplatingContext context) {
        if (response == null) {
            return Mono.just(Optional.empty());
        }
        TemplatingEngines templatingEngine = response.getTemplatingEngine();
        Mono<Optional<FixedLatency>> fixedLatency = templateFixedLatency(response.getFixedLatency(), templatingEngine, context);
        Mono<Optional<RandomLatency>> randomLatency = templateRandomLatency(response.getRandomLatency(), templatingEngine, context);
        Mono<Optional<String>> statusCode = template(response.getStatusCode(), templatingEngine, context);
        Mono<Optional<String>> contentType = template(response.getContentType(), templatingEngine, context);
        Mono<List<Header>> headers = templateHeaders(response.getHeaders(), templatingEngine, context);
        Mono<List<Cookie>> cookies = templateCookies(response.getCookies(), templatingEngine, context);
        Mono<Optional<String>> body = template(response.getBody(), templatingEngine, context);

        return Mono.zip(templatedValues ->
                        Optional.of(
                                Response.builder()
                                        .fixedLatency(((Optional<FixedLatency>) templatedValues[0]).orElse(null))
                                        .randomLatency(((Optional<RandomLatency>) templatedValues[1]).orElse(null))
                                        .statusCode(((Optional<String>) templatedValues[2]).get())
                                        .contentType(((Optional<String>) templatedValues[3]).orElse(null))
                                        .headers((List<Header>) templatedValues[4])
                                        .cookies((List<Cookie>) templatedValues[5])
                                        .body(((Optional<String>) templatedValues[6]).orElse(null))
                                        .build()
                        )
                , fixedLatency, randomLatency, statusCode, contentType, headers, cookies, body);
    }

    private Mono<Optional<ConditionalResponseValue>> templateConditionalResponse(ConditionalResponse conditionalResponse, TemplatingContext context) {
        if (conditionalResponse == null) {
            return Mono.just(Optional.empty());
        }
        List<Tuple2<Integer, ConditionalResponseValue>> conditionalResponseValuesWithOrder = new ArrayList<>();
        for (int i = 0; i < conditionalResponse.getResponse().size(); i++) {
            conditionalResponseValuesWithOrder.add(Tuples.of(i, conditionalResponse.getResponse().get(i)));
        }
        TemplatingEngines templatingEngine = conditionalResponse.getTemplatingEngine();
        return Flux.fromIterable(conditionalResponseValuesWithOrder)
                .flatMap(conditionalResponseValueWithOrder ->
                        template(conditionalResponseValueWithOrder.getT2().getCondition(), templatingEngine, context)
                                .map(condition ->
                                        Tuples.of(Boolean.valueOf(condition.get()), conditionalResponseValueWithOrder.getT2(), conditionalResponseValueWithOrder.getT1())
                                )
                )
                .filter(Tuple3::getT1)
                .collectList()
                .map(list -> {
                    log.error("list: {}", list);
                    if (list.size() > 1) {
                        list.sort(Comparator.comparing(Tuple3::getT3));
                    }
                    return list.get(0);
                })
                .flatMap(tuple -> templateConditionalResponseValue(tuple.getT2(), templatingEngine, context))
                .map(Optional::of);
    }

    private Mono<ConditionalResponseValue> templateConditionalResponseValue(ConditionalResponseValue conditionalResponseValue, TemplatingEngines templatingEngine, TemplatingContext context) {
        Mono<Optional<FixedLatency>> fixedLatency = templateFixedLatency(conditionalResponseValue.getFixedLatency(), templatingEngine, context);
        Mono<Optional<RandomLatency>> randomLatency = templateRandomLatency(conditionalResponseValue.getRandomLatency(), templatingEngine, context);
        Mono<Optional<String>> statusCode = template(conditionalResponseValue.getStatusCode(), templatingEngine, context);
        Mono<Optional<String>> contentType = template(conditionalResponseValue.getContentType(), templatingEngine, context);
        Mono<List<Header>> headers = templateHeaders(conditionalResponseValue.getHeaders(), templatingEngine, context);
        Mono<List<Cookie>> cookies = templateCookies(conditionalResponseValue.getCookies(), templatingEngine, context);
        Mono<Optional<String>> body = template(conditionalResponseValue.getBody(), templatingEngine, context);

        return Mono.zip(templatedValues ->
                        ConditionalResponseValue.builder()
                                .fixedLatency(((Optional<FixedLatency>) templatedValues[0]).orElse(null))
                                .randomLatency(((Optional<RandomLatency>) templatedValues[1]).orElse(null))
                                .statusCode(((Optional<String>) templatedValues[2]).get())
                                .contentType(((Optional<String>) templatedValues[3]).orElse(null))
                                .headers((List<Header>) templatedValues[4])
                                .cookies((List<Cookie>) templatedValues[5])
                                .body(((Optional<String>) templatedValues[6]).orElse(null))
                                .build()
                , fixedLatency, randomLatency, statusCode, contentType, headers, cookies, body);

    }

    private Mono<Optional<FixedLatency>> templateFixedLatency(FixedLatency fixedLatency, TemplatingEngines templatingEngine, TemplatingContext context) {
        if (fixedLatency == null) {
            return Mono.just(Optional.empty());
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
            return Mono.just(Optional.empty());
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
        Flux<Optional<String>> names = Flux.fromIterable(headers)
                .map(Header::getName)
                .flatMap(name -> template(name, templatingEngine, context));
        Flux<Optional<String>> values = Flux.fromIterable(headers)
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
        Flux<Optional<String>> names = Flux.fromIterable(cookies)
                .map(Cookie::getName)
                .flatMap(name -> template(name, templatingEngine, context));
        Flux<Optional<String>> values = Flux.fromIterable(cookies)
                .map(Cookie::getValue)
                .flatMap(value -> template(value, templatingEngine, context));
        Flux<Optional<CookieProperties>> cookiePropertiesFlux = Flux.fromIterable(cookies)
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
            return Mono.just(Optional.empty());
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
        TemplatedResponse templatedResponse = ((Optional<Response>) templatedValues[0])
                .map(TemplatedResponse::from)
                .orElseGet(() -> TemplatedResponse.from(((Optional<ConditionalResponseValue>) templatedValues[1]).get()));

        return TemplatedRule.builder()
                .name(rule.getName())
                .request(TemplatedRequest.from(rule.getRequest()))
                .response(templatedResponse)
                .build();
    }
}
