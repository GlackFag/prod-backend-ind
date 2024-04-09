package com.glackfag.travelgentle.maps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.util.JsonParser;
import com.glackfag.travelgentle.util.exceptions.CityNotRecognizedException;
import com.glackfag.travelgentle.util.exceptions.InvalidCoordinateException;
import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Component
@Slf4j
public class CoordinatesParser {
    private final String coordsToCityURL;
    private final RequestExecutor requestExecutor;

    @Autowired
    public CoordinatesParser(@Value("${apis.coords.url}") String coordsToCityURL, RequestExecutor requestExecutor) {
        this.coordsToCityURL = coordsToCityURL;
        this.requestExecutor = requestExecutor;
    }

    public Address getAddressFromCoordinates(@NotNull String longitude, @NotNull String latitude) throws InvalidCoordinateException {
        try {
            if (longitude == null || longitude.isEmpty())
                throw new NullPointerException("Longitude is null or empty");
            if (latitude == null || latitude.isEmpty())
                throw new NullPointerException("Latitude is null or empty");

            String url = UriComponentsBuilder.fromUriString(coordsToCityURL)
                    .queryParam("format", "json")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .toUriString();

            ResponseEntity<String> response = requestExecutor.getForUrl(url, String.class);

            Address address = JsonParser.retrieveAddress(response.getBody());

            if (Objects.equals(address.getCity(), "null"))
                throw new CityNotRecognizedException();

            return address;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
