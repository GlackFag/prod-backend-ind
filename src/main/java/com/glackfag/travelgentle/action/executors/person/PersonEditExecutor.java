package com.glackfag.travelgentle.action.executors.person;

import com.glackfag.travelgentle.Bot;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.action.executors.ActionExecutor;
import com.glackfag.travelgentle.maps.LocationFinder;
import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.services.AddressService;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import com.glackfag.travelgentle.util.validation.PersonFieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
public class PersonEditExecutor {
    private final PersonService personService;
    private final ActionExecutor actionExecutor;
    private final AddressService addressService;
    private final LocationFinder locationFinder;
    private final Bot bot;
    private final Environment env;
    private final PersonFieldValidator personFieldValidator;

    @Autowired
    public PersonEditExecutor(PersonService personService,
                              @Lazy ActionExecutor actionExecutor, AddressService addressService,
                              LocationFinder locationFinder, Bot bot, Environment env,
                              PersonFieldValidator personFieldValidator) {
        this.personService = personService;
        this.actionExecutor = actionExecutor;
        this.addressService = addressService;
        this.locationFinder = locationFinder;
        this.bot = bot;
        this.env = env;
        this.personFieldValidator = personFieldValidator;
    }

    public void execute(Action action, Update update) throws TelegramApiException {
        Long chatId = UpdateUtils.extractChatId(update);
        String input = UpdateUtils.extractUserInput(update);

        if (action.name().startsWith("REQUEST_EDITING"))
            bot.removeInlineMarkup(chatId, UpdateUtils.extractMessage(update));


        switch (action) {
            case REQUEST_EDITING_NAME -> bot.sendMessage(chatId, env.getRequiredProperty("user.edit.request.name"));
            case VALIDATE_EDITING_NAME_AND_SEND_RESULTS -> validateEditingNameAndSend(chatId, input);

            case REQUEST_EDITING_AGE -> bot.sendMessage(chatId, env.getRequiredProperty("user.edit.request.age"));
            case VALIDATE_EDITING_AGE_AND_SEND_RESULTS -> validateEditingAgeAndSend(chatId, input);

            case REQUEST_EDITING_BIO -> bot.sendMessage(chatId, env.getRequiredProperty("user.edit.request.bio"));
            case VALIDATE_EDITING_BIO_AND_SEND_RESULTS -> validateEditingBioAndSend(chatId, input);

            case REQUEST_EDITING_HOME_CITY ->
                    bot.sendMessage(chatId, env.getRequiredProperty("user.edit.request.homeCity"));
            case VALIDATE_EDITING_HOME_CITY_AND_SEND_RESULTS -> validateEditingHomeCityAndSend(chatId, input);
        }
    }

    private void validateEditingNameAndSend(Long chatId, String name) throws TelegramApiException {
        Optional<String> errMessage = personFieldValidator.validateName(name);

        String message = errMessage.orElse(env.getRequiredProperty("user.edit.saved.name"));

        if (errMessage.isEmpty()) personService.updateNameById(chatId, name);
        else personService.updateLastActionById(chatId, Action.REQUEST_EDITING_NAME);

        bot.sendMessage(chatId, message);
    }

    private void validateEditingAgeAndSend(Long chatId, String age) throws TelegramApiException {
        Optional<String> errMessage = personFieldValidator.validateAge(age);

        String message = errMessage.orElse(env.getRequiredProperty("user.edit.saved.age"));

        if (errMessage.isEmpty()) personService.updateAgeById(chatId, Integer.parseInt(age));
        else personService.updateLastActionById(chatId, Action.REQUEST_EDITING_AGE);

        bot.sendMessage(chatId, message);
    }

    private void validateEditingBioAndSend(Long chatId, String bio) throws TelegramApiException {
        Optional<String> errMessage = personFieldValidator.validateBio(bio);

        String message = errMessage.orElse(env.getRequiredProperty("user.edit.saved.bio"));

        if (errMessage.isEmpty()) personService.updateBioById(chatId, bio);
        else personService.updateLastActionById(chatId, Action.REQUEST_EDITING_BIO);

        bot.sendMessage(chatId, message);
    }

    private void validateEditingHomeCityAndSend(Long chatId, String input) throws TelegramApiException {
        String[] cityCountry = input.split(",\\s*");

        if (!actionExecutor.validateAddress(chatId, cityCountry))
            return;

        Address address = new Address();

        if (cityCountry.length == 2) {
            address.setCity(cityCountry[0]);
            address.setCountry(cityCountry[1]);
        } else
            address = locationFinder.addressByCity(cityCountry[0]);


        int addressId = addressService.save(address);

        personService.updateAddressIdById(chatId, addressId);

        bot.sendMessage(chatId, env.getRequiredProperty("user.edit.saved.homeCity"));
    }
}
