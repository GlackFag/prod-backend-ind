package com.glackfag.travelgentle.action.executors.travel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glackfag.travelgentle.Bot;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.action.executors.ActionExecutor;
import com.glackfag.travelgentle.maps.LocationFinder;
import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.models.creatring.CreatingPoint;
import com.glackfag.travelgentle.models.creatring.CreatingTravel;
import com.glackfag.travelgentle.services.AddressService;
import com.glackfag.travelgentle.services.IntermediatePointService;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.services.TravelService;
import com.glackfag.travelgentle.services.creating.CreatingPointService;
import com.glackfag.travelgentle.services.creating.CreatingTravelService;
import com.glackfag.travelgentle.util.telegram.markup.MarkupBuilder;
import com.glackfag.travelgentle.util.TimeUtils;
import com.glackfag.travelgentle.util.telegram.Commands;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import com.glackfag.travelgentle.util.validation.AddressFieldValidator;
import com.glackfag.travelgentle.util.validation.TravelFieldValidator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Date;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class TravelCreationExecutor {
    private final ZoneId zoneId;
    private final PersonService personService;
    private final AddressService addressService;
    private final TravelService travelService;
    private final LocationFinder locationFinder;
    private final TravelFieldValidator travelFieldValidator;
    private final ActionExecutor actionExecutor;
    private final Environment env;
    private final Bot bot;
    private final CreatingTravelService creatingTravelService;
    private final CreatingPointService creatingPointService;
    private final IntermediatePointService intermediatePointService;

    @Autowired
    public TravelCreationExecutor(ZoneId zoneId, PersonService personService, TravelService travelService, LocationFinder locationFinder, TravelFieldValidator travelFieldValidator, AddressFieldValidator addressFieldValidator, AddressService addressService, ActionExecutor actionExecutor, Environment env, @Lazy Bot bot, CreatingTravelService creatingTravelService, CreatingPointService creatingPointService, IntermediatePointService intermediatePointService) {
        this.zoneId = zoneId;
        this.personService = personService;
        this.travelService = travelService;
        this.locationFinder = locationFinder;
        this.travelFieldValidator = travelFieldValidator;
        this.addressService = addressService;
        this.actionExecutor = actionExecutor;
        this.env = env;
        this.bot = bot;
        this.creatingTravelService = creatingTravelService;
        this.creatingPointService = creatingPointService;
        this.intermediatePointService = intermediatePointService;
    }

    public void execute(Action action, Update update) throws TelegramApiException {
        Long chatId = UpdateUtils.extractChatId(update);

        switch (action) {
            case REQUEST_TITLE -> requestTitle(chatId);
            case VALIDATE_TITLE_AND_SEND_RESULT -> validateTitleAndSendResult(chatId, update);
            case REQUEST_DESCRIPTION -> bot.sendMessage(chatId, env.getRequiredProperty("travel.require.description"));
            case VALIDATE_DESCRIPTION_AND_SEND_RESULT -> validateDescriptionAndSendResult(chatId, update);
            case REQUEST_INTERMEDIATE_POINTS -> requestIntermediatePoints(update);
            case REQUEST_POINT_CITY -> bot.sendMessage(chatId, env.getRequiredProperty("travel.require.points.city"));
            case VALIDATE_CITY_AND_SEND_RESULTS -> validateCityAndSendResults(chatId, update);
            case REQUEST_POINT_START_DATE ->
                    bot.sendMessage(chatId, env.getRequiredProperty("travel.require.points.start"));
            case VALIDATE_START_DATE_AND_SEND_RESULTS -> validateStartDateAndSendResults(chatId, update);
            case REQUEST_POINT_END_DATE ->
                    bot.sendMessage(chatId, env.getRequiredProperty("travel.require.points.end"));
            case VALIDATE_END_DATE_AND_SEND_RESULTS -> validateEndDateAndSendResults(chatId, update);
            case REQUEST_MORE_INTERMEDIATE_POINTS -> requestMoreIntermediatePoints(chatId);
            case SAVE_TRAVEL -> saveTravel(chatId, UpdateUtils.extractMessage(update));
        }
    }

    private void requestTitle(Long chatId) throws TelegramApiException {
        creatingTravelService.deleteAllByOrganizerId(chatId);
        bot.sendMessage(chatId, env.getRequiredProperty("travel.require.title"));
    }

    @SneakyThrows(JsonProcessingException.class)
    private void validateTitleAndSendResult(Long chatId, Update update) throws TelegramApiException {
        String title = UpdateUtils.extractUserInput(update);

        Optional<String> errMessage = travelFieldValidator.validateTitle(title);

        if (errMessage.isPresent()) {
            bot.sendMessage(chatId, errMessage.get());
            return;
        }

        CreatingTravel travel = new CreatingTravel();
        travel.setTitle(title);
        travel.setOrganizerId(chatId);

        creatingTravelService.save(travel);
        actionExecutor.execute(Action.REQUEST_DESCRIPTION, update);
    }

    @SneakyThrows(JsonProcessingException.class)
    private void validateDescriptionAndSendResult(Long chatId, Update update) throws TelegramApiException {
        String description = UpdateUtils.extractUserInput(update);
        Long userId = UpdateUtils.extractUserId(update);

        Optional<String> errMessage = travelFieldValidator.validateDescription(description);

        if (errMessage.isPresent()) {
            bot.sendMessage(chatId, errMessage.get());
            personService.updateLastActionById(chatId, Action.REQUEST_DESCRIPTION);
            return;
        }

        CreatingTravel travel = creatingTravelService.findByOrganizerId(userId);

        if (Commands.SKIP.equalsIgnoreCase(description))
            travel.setDescription("");
        else
            travel.setDescription(description != null ? description : "");

        creatingTravelService.save(travel);

        actionExecutor.execute(Action.REQUEST_INTERMEDIATE_POINTS, update);
    }

    @SneakyThrows(JsonProcessingException.class)
    private void requestIntermediatePoints(Update update) throws TelegramApiException {
        actionExecutor.execute(Action.REQUEST_POINT_CITY, update);
    }

    @SneakyThrows(JsonProcessingException.class)
    protected void validateCityAndSendResults(Long chatId, Update update) throws TelegramApiException {
        String[] cityCountry = UpdateUtils.extractUserInput(update).split(",\\s*");

        if (!actionExecutor.validateAddress(chatId, cityCountry)) {
            personService.updateLastActionById(chatId, Action.REQUEST_POINT_CITY);
            return;
        }

        Address address = new Address();

        if (cityCountry.length == 2) {
            address.setCity(cityCountry[0]);
            address.setCountry(cityCountry[1]);
        } else
            address = locationFinder.addressByCity(cityCountry[0]);


        int addressId = addressService.save(address);

        CreatingPoint point = new CreatingPoint();
        point.setAddressId(addressId);
        point.setTravelId(creatingTravelService.findByOrganizerId(chatId).getId());

        creatingPointService.save(point);

        actionExecutor.execute(Action.REQUEST_POINT_START_DATE, update);
    }

    @SneakyThrows(JsonProcessingException.class)
    private void validateStartDateAndSendResults(Long chatId, Update update) throws TelegramApiException {
        String input = UpdateUtils.extractUserInput(update);

        Optional<String> errMessage = travelFieldValidator.validateStartDate(input);

        if (errMessage.isPresent()) {
            bot.sendMessage(chatId, errMessage.get());
            personService.updateLastActionById(chatId, Action.REQUEST_POINT_START_DATE);
            return;
        }

        Date startDate = TimeUtils.dateFromLocalDate(
                TravelFieldValidator.parseLocalDate(input), zoneId);

        CreatingPoint point = getCreatingPointByOrganizerId(chatId);
        point.setStartDate(startDate);

        creatingPointService.save(point);

        actionExecutor.execute(Action.REQUEST_POINT_END_DATE, update);
    }

    private CreatingPoint getCreatingPointByOrganizerId(Long orgId) {
        CreatingTravel creatingTravel = creatingTravelService.findByOrganizerId(orgId);
        return creatingPointService.findByTravelId(creatingTravel.getId());
    }

    @SneakyThrows(JsonProcessingException.class)
    private void validateEndDateAndSendResults(Long chatId, Update update) throws TelegramApiException {
        String input = UpdateUtils.extractUserInput(update);
        CreatingPoint point = getCreatingPointByOrganizerId(chatId);

        Optional<String> errMessage = travelFieldValidator.validateEndDate(input, point.getStartDate());

        if (errMessage.isPresent()) {
            bot.sendMessage(chatId, errMessage.get());
            personService.updateLastActionById(chatId, Action.REQUEST_POINT_END_DATE);
            return;
        }

        Date endDate = TimeUtils.dateFromLocalDate(
                TravelFieldValidator.parseLocalDate(input), zoneId);
        point.setEndDate(endDate);

        creatingPointService.save(point);

        travelService.saveFromCreating(creatingTravelService.findByOrganizerId(chatId));
        intermediatePointService.saveFromCreating(point);

        creatingPointService.deleteAllByTravelId(point.getTravelId());

        actionExecutor.execute(Action.REQUEST_MORE_INTERMEDIATE_POINTS, update);
    }

    private void requestMoreIntermediatePoints(Long chatId) throws TelegramApiException {
        bot.sendMessage(chatId, env.getRequiredProperty("travel.doRequestMorePoints"), MarkupBuilder.doRequestMorePointsMarkup());
    }

    private void saveTravel(Long chatId, Message message) throws TelegramApiException {
        travelService.saveFromCreating(creatingTravelService.findByOrganizerId(chatId));

        EditMessageText editMessageText = new EditMessageText(env.getRequiredProperty("travel.saved"));
        editMessageText.setChatId(chatId);
        editMessageText.setReplyMarkup(MarkupBuilder.emptyInlineKeyboard());
        editMessageText.setMessageId(message.getMessageId());

        bot.execute(editMessageText);
    }
}
