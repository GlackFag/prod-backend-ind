package com.glackfag.travelgentle.util.validation;

import com.glackfag.travelgentle.services.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Component
public class TravelFieldValidator {
    private final Environment env;
    private final TravelService travelService;

    @Autowired
    public TravelFieldValidator(Environment env, TravelService travelService) {
        this.env = env;
        this.travelService = travelService;
    }

    public Optional<String> validateTitle(String title) {
        if (title == null || title.isEmpty())
            return Optional.of(env.getRequiredProperty("travel.title.empty"));
        if (title.length() > 30)
            return Optional.of(env.getRequiredProperty("travel.title.tooLong"));
        if (travelService.existsByTitle(title))
            return Optional.of(env.getRequiredProperty("travel.title.alreadyExists"));

        return Optional.empty();
    }

    public Optional<String> validateDescription(String description) {
        if (description == null || description.length() <= 100)
            return Optional.empty();

        return Optional.of(env.getRequiredProperty("travel.description.tooLong"));
    }

    public Optional<String> validateStartDate(String date) {
        try{
            LocalDate localDate = parseLocalDate(date);

            if (localDate.isBefore(LocalDate.now()))
                return Optional.of(env.getRequiredProperty("travel.date.start.tooEarly"));

        }catch (DateTimeParseException e){
            return Optional.of(env.getRequiredProperty("travel.date.unparsable"));
        }

        return Optional.empty();
    }

    public Optional<String> validateEndDate(String date, Date startDate) {
        try{
            LocalDate localDate = parseLocalDate(date);

            if (localDate.isBefore(LocalDate.now()))
                return Optional.of(env.getRequiredProperty("travel.date.start.tooEarly"));
            if(localDate.isBefore(startDate.toLocalDate()))
                return Optional.of(env.getRequiredProperty("travel.date.end.tooEarly"));

        }catch (DateTimeParseException e){
            return Optional.of(env.getRequiredProperty("travel.date.unparsable"));
        }

        return Optional.empty();
    }

    public static LocalDate parseLocalDate(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        return LocalDate.parse(date, formatter);
    }
}

