package com.glackfag.travelgentle.util.validation;

import com.glackfag.travelgentle.maps.LocationFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AddressFieldValidator {
    private final LocationFinder locationFinder;
    private final Environment env;

    @Autowired
    public AddressFieldValidator(LocationFinder locationFinder, Environment env) {
        this.locationFinder = locationFinder;
        this.env = env;
    }

    public Optional<String> validateCity(String city) {
        if (locationFinder.isCityFound(city))
            return Optional.empty();
        return Optional.of(env.getRequiredProperty("location.city.notFound"));
    }

    public Optional<String> validateCountry(String country) {
        if (locationFinder.isCountryFound(country))
            return Optional.empty();
        return Optional.of(env.getRequiredProperty("location.country.notFound"));
    }
}
