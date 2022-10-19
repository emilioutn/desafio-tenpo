package com.exampletenpo.calculate.httpapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class HttpOtherServiceImpl implements HttpOtherService {

    private RestTemplate restTemplate;

    private String apiUrl;

    public HttpOtherServiceImpl(
            RestTemplate restTemplate,
            @Value("${otherApp.url.api}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    public HttpOtherServiceImpl() {
    }

    @Override
    public BigDecimal calculateFromApi(String jsonWithNumbers) {
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(jsonWithNumbers, headers);
            HttpEntity<BigDecimal> response = restTemplate.postForEntity(apiUrl, entity, BigDecimal.class);
            return response.getBody();
        } catch (Exception e) {
            throw e;
        }
    }
}
