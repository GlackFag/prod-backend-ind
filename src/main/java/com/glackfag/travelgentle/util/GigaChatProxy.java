package com.glackfag.travelgentle.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glackfag.travelgentle.util.exceptions.ApiException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class GigaChatProxy {
    private Pair<String, Long> tokenExpiresAt = Pair.of("", 0L);
    private final RestTemplate restTemplate;
    @Value("${gigachat.auth}")
    private String authToken;

    @Value("${apis.gigachat.revoke.auth}")
    private String revokeAuthUrl;

    @Value("${apis.gigachat.query}")
    private String queryUrl;

    @Autowired
    public GigaChatProxy(@Qualifier("certificatelessRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    public static String retrieveMessageContent(String json) {
        String content = JsonParser.retrieveField(JsonParser.retrieveField(JsonParser.retrieveField(JsonParser.retrieveField(json, "choices"), 0), "message"), "content");
        return content.substring(1, content.length() - 1);
    }

    @SneakyThrows
    public String executePrompt(String prompt, String systemPrompt) {
        updateVerificationToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenExpiresAt.getFirst());

        String body = "{\n" +
                "  \"model\": \"GigaChat:latest\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"" + systemPrompt + "\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + prompt + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"temperature\": 1.2,\n" +
                "  \"top_p\": 0.1,\n" +
                "  \"n\": 1,\n" +
                "  \"stream\": false,\n" +
                "  \"max_tokens\": 512,\n" +
                "  \"repetition_penalty\": 1.2\n" +
                "}";

        System.out.println(body);
        return exec(new HttpEntity<>(body, httpHeaders));
    }

    @SneakyThrows
    public String executePrompt(String prompt) {
        updateVerificationToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenExpiresAt.getFirst());

        String body = "{\n" +
                "  \"model\": \"GigaChat:latest\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + prompt + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"temperature\": 1.0,\n" +
                "  \"top_p\": 0.1,\n" +
                "  \"n\": 1,\n" +
                "  \"stream\": false,\n" +
                "  \"max_tokens\": 512,\n" +
                "  \"repetition_penalty\": 1\n" +
                "}";

        return restTemplate.exchange(queryUrl, HttpMethod.POST, new HttpEntity<>(body, httpHeaders), String.class).getBody();
    }

    private String exec(HttpEntity<String> httpEntity) {
        return restTemplate.exchange(queryUrl, HttpMethod.POST, httpEntity, String.class).getBody();
    }

    private void updateVerificationToken() throws JsonProcessingException {
        if (isTokenValid())
            return;

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> body = new HttpEntity<>("scope=GIGACHAT_API_PERS", httpHeaders);

        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + authToken);
        httpHeaders.add("RqUID", UUID.randomUUID().toString());
        httpHeaders.add("Accept", "application/json");

        ResponseEntity<String> response = restTemplate.exchange(revokeAuthUrl, HttpMethod.POST, body, String.class);

        if (!response.getStatusCode().is2xxSuccessful())
            throw new ApiException("GigaChat is not authorized");

        String accessToken = JsonParser.retrieveField(response.getBody(), "access_token");
        Long expires = Long.parseLong(JsonParser.retrieveField(response.getBody(), "expires_at"));

        tokenExpiresAt = Pair.of(accessToken.substring(1, accessToken.length() - 1), expires);
        System.out.println(tokenExpiresAt);
    }

    private boolean isTokenValid() {
        return tokenExpiresAt.getSecond() > Timestamp.valueOf(LocalDateTime.now()).getTime();
    }
}
