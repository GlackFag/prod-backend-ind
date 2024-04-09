package com.glackfag.travelgentle.action.executors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glackfag.travelgentle.Bot;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.action.executors.person.PersonEditExecutor;
import com.glackfag.travelgentle.action.executors.person.PersonShowExecutor;
import com.glackfag.travelgentle.action.executors.travel.IntermediatePointShowExecutor;
import com.glackfag.travelgentle.action.executors.travel.TravelCreationExecutor;
import com.glackfag.travelgentle.action.executors.travel.TravelEditExecutor;
import com.glackfag.travelgentle.action.executors.travel.TravelShowExecutor;
import com.glackfag.travelgentle.maps.CoordinatesParser;
import com.glackfag.travelgentle.maps.SightSuggester;
import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.util.ParamRetriever;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import com.glackfag.travelgentle.util.telegram.markup.MarkupBuilder;
import com.glackfag.travelgentle.util.validation.AddressFieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class ActionExecutor {
    private final PersonEditExecutor personEditExecutor;
    private final RegistrationExecutor registrationExecutor;
    private final TravelCreationExecutor travelCreationExecutor;
    private final TravelShowExecutor travelShowExecutor;
    private final TravelEditExecutor travelEditExecutor;
    private final PersonShowExecutor personShowExecutor;
    private final AddressFieldValidator addressFieldValidator;
    private final IntermediatePointShowExecutor intermediatePointShowExecutor;
    private final SightSuggester sightSuggester;
    private final CoordinatesParser coordinatesParser;
    private final Environment env;
    private final Bot bot;
    private final PersonService personService;

    @Autowired
    public ActionExecutor(PersonEditExecutor personEditExecutor, @Lazy RegistrationExecutor registrationExecutor, @Lazy TravelCreationExecutor travelCreationExecutor, TravelShowExecutor travelShowExecutor, TravelEditExecutor travelEditExecutor, PersonShowExecutor personShowExecutor, AddressFieldValidator addressFieldValidator, IntermediatePointShowExecutor intermediatePointShowExecutor, SightSuggester sightSuggester, CoordinatesParser coordinatesParser, Environment env, @Lazy Bot bot, PersonService personService) {
        this.personEditExecutor = personEditExecutor;
        this.registrationExecutor = registrationExecutor;
        this.travelCreationExecutor = travelCreationExecutor;
        this.travelShowExecutor = travelShowExecutor;
        this.travelEditExecutor = travelEditExecutor;
        this.personShowExecutor = personShowExecutor;
        this.addressFieldValidator = addressFieldValidator;
        this.intermediatePointShowExecutor = intermediatePointShowExecutor;
        this.sightSuggester = sightSuggester;
        this.coordinatesParser = coordinatesParser;
        this.env = env;
        this.bot = bot;
        this.personService = personService;
    }

    public void execute(Action action, Update update) throws TelegramApiException, JsonProcessingException {
        Long chatId = UpdateUtils.extractChatId(update);
        String callback = UpdateUtils.extractCallbackDataText(update);

        try {
            switch (action) {
                case ASK_TO_REGISTER -> {
                    bot.sendMessage(chatId, env.getRequiredProperty("askToRegister"));
                    return;
                }
                case SEND_MENU -> {
                    bot.sendMessage(chatId, env.getRequiredProperty("user.menu"));
                    personService.updateLastActionById(chatId, action);
                    return;
                }
                case IGNORE -> {
                    return;
                }
                case REQUEST_LOCATION_FOR_SUGGEST_SIGHT -> requestLocationForSuggestSight(chatId);
                case SUGGEST_SIGHT -> suggestSight(chatId, update);
                case SUGGEST_NEXT_SIGHT -> suggestNextSight(chatId, callback, UpdateUtils.extractMessage(update));
            }

            registrationExecutor.execute(action, update);

            personService.updateLastActionById(chatId, action);
            travelShowExecutor.execute(action, update);
            intermediatePointShowExecutor.execute(action, update);
            travelEditExecutor.execute(action, update);
            personShowExecutor.execute(action, update);
            personEditExecutor.execute(action, update);
            travelCreationExecutor.execute(action, update);
        } catch (NoSuchElementException e) {
            bot.sendMessage(chatId, env.getRequiredProperty("entityNotFound"));
        } catch (Exception e) {
            bot.sendMessage(chatId, env.getRequiredProperty("error"));
            e.printStackTrace();
        }
    }

    public boolean validateAddress(Long chatId, String[] cityCountry) throws TelegramApiException {
        Optional<String> errMessage = addressFieldValidator.validateCity(cityCountry[0]);

        if (errMessage.isPresent()) {
            bot.sendMessage(chatId, errMessage.get());
            return false;
        }
        if (cityCountry.length == 2) {
            errMessage = addressFieldValidator.validateCountry(cityCountry[1]);

            if (errMessage.isPresent()) {
                bot.sendMessage(chatId, errMessage.get());
                return false;
            }
        }
        return true;
    }

    private void requestLocationForSuggestSight(Long chatId) throws TelegramApiException {
        bot.sendMessage(chatId, env.getRequiredProperty("require.location.telegram"), MarkupBuilder.shareLocationMarkup());
    }

    private void suggestSight(Long chatId, Update update) throws TelegramApiException {
        Location location = UpdateUtils.extractMessage(update).getLocation();

        Address address = coordinatesParser.getAddressFromCoordinates(String.valueOf(location.getLongitude()),
                String.valueOf(location.getLatitude()));

        bot.sendMessage(chatId, sightSuggester.suggest(address.getCity(), new String[0]),
                MarkupBuilder.buildNextSuggestionMarkup(address.getCity(), new String[0]));
    }

    private void suggestNextSight(Long chatId, String callback, Message message) throws TelegramApiException {
        Map<String, String> params = ParamRetriever.getParamsFromUrl(callback);
        String city = params.get("city");
        String[] ignoredSights = Optional.ofNullable(params.get("ignore"))
                .orElse("").split(";");

        ignoredSights = Arrays.copyOf(ignoredSights, ignoredSights.length + 1);

        ignoredSights[ignoredSights.length -1] = sightSuggester.getSuggestedSight(message.getText());

        String suggestion = sightSuggester.suggest(city, ignoredSights);

        bot.sendMessage(chatId, suggestion, MarkupBuilder.removeMarkup());
        Bot.editTextOf(chatId, message.getMessageId(), suggestion, MarkupBuilder.buildNextSuggestionMarkup(city, ignoredSights), false);
    }
}
