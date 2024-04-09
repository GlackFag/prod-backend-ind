package com.glackfag.travelgentle.maps;

import com.glackfag.travelgentle.util.exceptions.InvalidCoordinateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestExecutor {
    private final RestTemplate restTemplate;
    @Autowired
    public RequestExecutor(@Qualifier("defualtRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public  <T> ResponseEntity<T> getForUrl(String url, Class<T> clazz){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept-language", "en");

        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), clazz);


        if (response.getStatusCode().is4xxClientError())
            throw new InvalidCoordinateException();

        if (!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException(String.format("Error occurred while requesting address:%s,%d", url, response.getStatusCode().value()));

        return response;
    }
}
