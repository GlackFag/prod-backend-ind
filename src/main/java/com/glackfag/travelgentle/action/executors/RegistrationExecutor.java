package com.glackfag.travelgentle.action.executors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glackfag.travelgentle.Bot;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.maps.CoordinatesParser;
import com.glackfag.travelgentle.maps.LocationFinder;
import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.models.creatring.RegisteringPerson;
import com.glackfag.travelgentle.services.AddressService;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.services.creating.RegisteringPersonService;
import com.glackfag.travelgentle.util.telegram.markup.MarkupBuilder;
import com.glackfag.travelgentle.util.exceptions.CityNotRecognizedException;
import com.glackfag.travelgentle.util.telegram.Commands;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import com.glackfag.travelgentle.util.validation.PersonFieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class RegistrationExecutor {
    private final Bot bot;
    private final RegisteringPersonService registeringService;
    private final PersonService personService;
    private final AddressService addressService;
    private final PersonFieldValidator personFieldValidator;
    private final Environment env;
    private final LocationFinder locationFinder;
    private final CoordinatesParser coordinatesParser;
    private final ActionExecutor actionExecutor;

    @Autowired
    public RegistrationExecutor(@Lazy Bot bot, RegisteringPersonService registeringService, PersonService personService,
                                AddressService addressService, PersonFieldValidator personFieldValidator, Environment env,
                                LocationFinder locationFinder, CoordinatesParser coordinatesParser,
                                @Lazy ActionExecutor actionExecutor) {
        this.bot = bot;
        this.registeringService = registeringService;
        this.personService = personService;
        this.addressService = addressService;
        this.personFieldValidator = personFieldValidator;
        this.env = env;
        this.locationFinder = locationFinder;
        this.coordinatesParser = coordinatesParser;
        this.actionExecutor = actionExecutor;
    }

    @ExceptionHandler
    private void handle(NoSuchElementException ignored) throws TelegramApiException {
        throw new TelegramApiException();
    }

    public void execute(Action action, Update update) throws TelegramApiException, JsonProcessingException {
        Long chatId = UpdateUtils.extractChatId(update);
        String input = UpdateUtils.extractUserInput(update);

        switch (action) {
            case SEND_GREETINGS_AND_REQUEST_NAME -> {
                bot.sendMessage(chatId, env.getRequiredProperty("start"));
                sendRequestForName(chatId);
            }
            case VALIDATE_NAME_AND_SEND_RESULT -> validateName(update, chatId, input);
            case REQUEST_AGE -> sendRequestForAge(chatId);
            case VALIDATE_AGE_AND_SEND_RESULT -> validateAge(update, chatId, input);
            case REQUEST_ADDRESS -> requestLocation(chatId);
            case PARSE_AND_SAVE_ADDRESS -> parseAndSaveLocation(chatId, update);
            case CANCEL_REGISTRATION -> cancelRegistration(chatId);
            case REQUEST_ADDRESS_MANUALLY -> requestAddressManually(chatId);
            case VALIDATE_AND_SAVE_ADDRESS -> validateAddressAndSave(chatId, input, update);
            case COMPLETE_REGISTRATION -> completeRegistration(chatId);

        }
    }

    private void completeRegistration(Long chatId) throws TelegramApiException {
        RegisteringPerson registering = registeringService.findById(chatId).orElseThrow();
        personService.saveFromRegistrationPerson(registering);

        bot.sendMessage(chatId, env.getRequiredProperty("registration.complete"));
    }

    private void cancelRegistration(Long chatId) throws TelegramApiException {
        registeringService.deleteById(chatId);
        bot.sendMessage(chatId, env.getRequiredProperty("registration.cancel"), MarkupBuilder.removeMarkup());
    }

    private void validateAddressAndSave(Long chatId, String input, Update update) throws TelegramApiException, JsonProcessingException {
        String[] cityCountry = input.split(",\\s*");

        if (!actionExecutor.validateAddress(chatId, cityCountry))
            return;

        Address address = new Address();

        if (cityCountry.length == 2) {
            address.setCity(cityCountry[0]);
            address.setCountry(cityCountry[1]);
        } else
            address = locationFinder.addressByCity(cityCountry[0]);


        saveAddress(chatId, address);
        execute(Action.COMPLETE_REGISTRATION, update);
    }

    private void requestAddressManually(Long chatId) throws TelegramApiException {
        bot.sendMessage(chatId, env.getRequiredProperty("location.exception"), MarkupBuilder.removeMarkup());
        registeringService.updateLastActionById(chatId, Action.REQUEST_ADDRESS_MANUALLY);
    }

    private void parseAndSaveLocation(Long chatId, Update update) throws JsonProcessingException, TelegramApiException {
        Location location = UpdateUtils.extractMessage(update).getLocation();

        try {
            Address address = coordinatesParser.getAddressFromCoordinates(String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()));
            saveAddress(chatId, address);

            execute(Action.COMPLETE_REGISTRATION, update);
        } catch (CityNotRecognizedException e) {
            execute(Action.REQUEST_ADDRESS_MANUALLY, update);
        }
    }

    private void saveAddress(long chatId, Address address) throws TelegramApiException {
        registeringService.updateAddressById(chatId, addressService.save(address));
        registeringService.updateLastActionById(chatId, Action.PARSE_AND_SAVE_ADDRESS);
        bot.sendMessage(chatId, env.getRequiredProperty("info.locationSaved"), MarkupBuilder.removeMarkup());
    }

    private void requestLocation(Long chatId) throws TelegramApiException {
        bot.sendMessage(chatId, env.getRequiredProperty("require.location.way"), MarkupBuilder.registrationLocationMarkup());
        registeringService.updateLastActionById(chatId, Action.REQUEST_ADDRESS);
    }

    private void validateAge(Update update, Long chatId, String age) throws TelegramApiException, JsonProcessingException {
        Optional<String> errMessage = personFieldValidator.validateAge(age);

        if (errMessage.isPresent()) {
            bot.sendMessage(chatId, errMessage.get());
            return;
        }

        RegisteringPerson registeringPerson = registeringService.findById(chatId).orElseThrow();
        registeringPerson.setLastAction(Action.VALIDATE_AGE_AND_SEND_RESULT);

        if (!Commands.SKIP.equalsIgnoreCase(age))
            registeringPerson.setAge(Integer.parseInt(age));

        registeringService.save(registeringPerson);
        execute(Action.REQUEST_ADDRESS, update);
    }

    private void sendRequestForAge(Long chatId) throws TelegramApiException {
        bot.sendMessage(chatId, env.getRequiredProperty("registration.require.age"));
        registeringService.updateLastActionById(chatId, Action.REQUEST_AGE);
    }

    private void validateName(Update update, Long chatId, String name) throws TelegramApiException, JsonProcessingException {
        Optional<String> errMessage = personFieldValidator.validateName(name);

        if (errMessage.isPresent()) {
            bot.sendMessage(chatId, errMessage.get());
            return;
        }

        RegisteringPerson registeringPerson = registeringService.findById(chatId).orElseThrow();
        registeringPerson.setName(name);
        registeringPerson.setLastAction(Action.VALIDATE_NAME_AND_SEND_RESULT);

        registeringService.save(registeringPerson);
        execute(Action.REQUEST_AGE, update);
    }

    private void sendRequestForName(Long chatId) throws TelegramApiException {
        bot.sendMessage(chatId, env.getRequiredProperty("registration.require.name"));
        registeringService.save(new RegisteringPerson().setId(chatId).setLastAction(Action.SEND_GREETINGS_AND_REQUEST_NAME));
    }

}
