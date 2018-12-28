package kroonprins.mocker.model;

import java.util.List;

public interface TemplatableResponse {
    FixedLatency getFixedLatency();

    RandomLatency getRandomLatency();

    String getContentType();

    String getStatusCode();

    List<Header> getHeaders();

    List<Cookie> getCookies();

    String getBody();
}
