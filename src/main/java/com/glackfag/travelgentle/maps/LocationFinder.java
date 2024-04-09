package com.glackfag.travelgentle.maps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.util.JsonParser;
import com.glackfag.travelgentle.util.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class LocationFinder {
    private final String apiUrl;
    private final RequestExecutor requestExecutor;

    @Autowired
    public LocationFinder(@Value("${apis.existChecker}") String apiUrl, RequestExecutor requestExecutor) {
        this.apiUrl = apiUrl;
        this.requestExecutor = requestExecutor;
    }

    public boolean isCityFound(String providedCity) {
        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("city", providedCity)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .toUriString();

            ResponseEntity<String> response = requestExecutor.getForUrl(url, String.class);

            String cityName = JsonParser.retrieveField(JsonParser.retrieveField(response.getBody(), 0), "name")
                    .replaceAll("\"", "");

            return providedCity.equalsIgnoreCase(cityName);
        } catch (JsonProcessingException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean isCountryFound(String providedCountry) {
        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("country", providedCountry)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .toUriString();

            ResponseEntity<String> response = requestExecutor.getForUrl(url, String.class);

            String countryName = JsonParser.retrieveField(JsonParser.retrieveField(response.getBody(), 0), "name")
                    .replaceAll("\"", "");

            return providedCountry.equalsIgnoreCase(countryName);
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public Address addressByCity(String providedCity) {
        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("city", providedCity)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .toUriString();

            ResponseEntity<String> response = requestExecutor.getForUrl(url, String.class);

            String res = JsonParser.retrieveField(JsonParser.retrieveField(response.getBody(), 0), "display_name");
            String[] cityCountry = res.substring(1, res.length() - 1).split(",\\s*");

            return new Address(cityCountry[cityCountry.length - 1], cityCountry[0], null, null);
        } catch (JsonProcessingException e) {
            throw new ApiException(e.getMessage());
        }
    }

    @ExceptionHandler
    private void handle(ApiException apiException) {
        log.atError().setCause(apiException).log();
    }

    @ExceptionHandler
    private void handle(Exception exception) {
        log.atDebug().setCause(exception).log();
    }
}
