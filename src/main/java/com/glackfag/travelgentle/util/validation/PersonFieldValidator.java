package com.glackfag.travelgentle.util.validation;

import com.glackfag.travelgentle.util.telegram.Commands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonFieldValidator {
    private final Environment env;

    @Autowired
    public PersonFieldValidator(Environment env) {
        this.env = env;
    }

    public Optional<String> validateName(String name) {
        if (name == null || name.length() < 2)
            return Optional.of(env.getRequiredProperty("name.tooShort"));

        if (name.length() > 25)
            return Optional.of(env.getRequiredProperty("name.tooLong"));

        if (!name.matches("[A-Za-z]+"))
            return Optional.of(env.getRequiredProperty("name.chars"));
        return Optional.empty();
    }

    public Optional<String> validateAge(String age) {
        try {
            return validateAge(Integer.parseInt(age));
        } catch (NumberFormatException e) {
            return Commands.SKIP.equalsIgnoreCase(age) ? Optional.empty() :
                    Optional.of(env.getRequiredProperty("age.NAN"));
        }
    }

    public Optional<String> validateAge(int age) {
        if (age <= 0)
            return Optional.of(env.getRequiredProperty("age.notPositive"));
        if (age > 120)
            return Optional.of(env.getRequiredProperty("age.tooBig"));
        return Optional.empty();
    }

    public Optional<String> validateBio(String bio) {
        if (bio.length() > 200)
            return Optional.of(env.getRequiredProperty("bio.tooLong"));
        return Optional.empty();
    }
}
