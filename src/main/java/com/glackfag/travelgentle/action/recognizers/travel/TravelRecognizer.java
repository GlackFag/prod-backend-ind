package com.glackfag.travelgentle.action.recognizers.travel;

import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.util.telegram.Commands;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class TravelRecognizer {
    private final PersonService personService;

    public TravelRecognizer(PersonService personService) {
        this.personService = personService;
    }

    public Optional<Action> recognize(Update update) {
        Long userId = UpdateUtils.extractUserId(update);
        String input = UpdateUtils.extractUserInput(update);
        String callbackData = UpdateUtils.extractCallbackDataText(update);

        Action lastAction = personService.findLastActionById(userId);

        if (isRequestTitle(input))
            return Optional.of(Action.REQUEST_TITLE);
        if (isValidateTitleAndSendResult(lastAction))
            return Optional.of(Action.VALIDATE_TITLE_AND_SEND_RESULT);
        if (isValidateDescriptionAndSendResult(lastAction))
            return Optional.of(Action.VALIDATE_DESCRIPTION_AND_SEND_RESULT);
        if (isRequestIntermediatePoints(lastAction, callbackData))
            return Optional.of(Action.REQUEST_INTERMEDIATE_POINTS);
        if (isValidateCityAndSendResults(lastAction))
            return Optional.of(Action.VALIDATE_CITY_AND_SEND_RESULTS);
        if (isValidateStartDateAndSendResults(lastAction))
            return Optional.of(Action.VALIDATE_START_DATE_AND_SEND_RESULTS);
        if (isValidateEndDateAndSendResults(lastAction))
            return Optional.of(Action.VALIDATE_END_DATE_AND_SEND_RESULTS);
        if (isSaveTravel(lastAction, callbackData))
            return Optional.of(Action.SAVE_TRAVEL);
        if (isIndexTravels(input))
            return Optional.of(Action.INDEX_TRAVELS);
        if (isSwitchTravelIndexPage(callbackData, lastAction))
            return Optional.of(Action.SWITCH_TRAVEL_INDEX_PAGE);
        if (isIndexSingleTravel(callbackData))
            return Optional.of(Action.INDEX_SINGLE_TRAVEL);
        if (isChooseTravelToInvite(input))
            return Optional.of(Action.CHOOSE_TRAVEL_TO_INVITE);
        if (isSwitchChoosingTravelPage(callbackData, lastAction))
            return Optional.of(Action.SWITCH_CHOOSING_TRAVEL_PAGE);
        if (isSendInvitationCode(callbackData))
            return Optional.of(Action.SEND_INVITATION_CODE);
        if (isRequestInvitationCode(input))
            return Optional.of(Action.REQUEST_INVITATION_CODE);
        if (isValidateInvitationCodeAndSendRes(lastAction))
            return Optional.of(Action.VALIDATE_INVITATION_CODE_AND_SEND_RESULT);
        if (isSubmitTravelDeletion(callbackData))
            return Optional.of(Action.SUBMIT_TRAVEL_DELETION);
        if (isDeleteTravel(callbackData))
            return Optional.of(Action.DELETE_TRAVEL);
        if (cancelDelete(callbackData))
            return Optional.of(Action.CANCEL_DELETE_TRAVEL);

        return Optional.empty();
    }

    private boolean isRequestTitle(String input) {
        return Commands.Travel.NEW_TRAVEL.equalsIgnoreCase(input);
    }

    private boolean isValidateTitleAndSendResult(Action lastAction) {
        return lastAction == Action.REQUEST_TITLE;
    }

    private boolean isValidateDescriptionAndSendResult(Action lastAction) {
        return lastAction == Action.REQUEST_DESCRIPTION;
    }

    private boolean isRequestIntermediatePoints(Action lastAction, String callbackData) {
        return lastAction == Action.VALIDATE_DESCRIPTION_AND_SEND_RESULT ||
                (lastAction == Action.REQUEST_MORE_INTERMEDIATE_POINTS && Commands.Travel.ANOTHER_POINT.equals(callbackData));
    }

    private boolean isValidateCityAndSendResults(Action lastAction) {
        return lastAction == Action.REQUEST_POINT_CITY;
    }

    private boolean isValidateStartDateAndSendResults(Action lastAction) {
        return lastAction == Action.REQUEST_POINT_START_DATE;
    }

    private boolean isValidateEndDateAndSendResults(Action lastAction) {
        return lastAction == Action.REQUEST_POINT_END_DATE;
    }

    private boolean isSaveTravel(Action lastAction, String callbackData) {
        return lastAction == Action.REQUEST_MORE_INTERMEDIATE_POINTS && Commands.Travel.SAVE_TRAVEL.equals(callbackData);
    }

    private boolean isIndexTravels(String input) {
        return Commands.Travel.MY_TRAVELS.equalsIgnoreCase(input);
    }

    private boolean isSwitchTravelIndexPage(String callbackData, Action action) {
        return action == Action.INDEX_TRAVELS &&
                Commands.Travel.INDEX_PAGE.equals(callbackData.replaceAll("\\d+", "%d"));
    }

    private boolean isIndexSingleTravel(String callbackData) {
        return callbackData.startsWith(Commands.Travel.INDEX_ID);
    }

    private boolean isChooseTravelToInvite(String input) {
        return Commands.Travel.INVITE.equalsIgnoreCase(input);
    }

    private boolean isSwitchChoosingTravelPage(String callbackData, Action last) {
        return (last == Action.SWITCH_CHOOSING_TRAVEL_PAGE || last == Action.CHOOSE_TRAVEL_TO_INVITE) &&
                Commands.Travel.INDEX_PAGE.equals(callbackData.replaceAll("\\d+", "%d"));
    }

    private boolean isSendInvitationCode(String callback) {
        return callback.startsWith(Commands.Travel.INVITE_TRAVEL_ID);
    }

    private boolean isRequestInvitationCode(String input) {
        return Commands.Travel.JOIN_TRAVEL.equalsIgnoreCase(input);
    }

    private boolean isValidateInvitationCodeAndSendRes(Action last) {
        return last == Action.REQUEST_INVITATION_CODE;
    }

    private boolean isSubmitTravelDeletion(String callback) {
        return Commands.Travel.CONFIRM_DELETE_TRAVEL_ID.equals(callback.replaceAll("\\d+", ""));
    }

    private boolean isDeleteTravel(String callback) {
        return Commands.Travel.DELETE_TRAVEL_ID.equals(callback.replaceAll("\\d+", ""));
    }

    private boolean cancelDelete(String callback) {
        return Commands.Travel.CANCEL_DELETION.equals(callback);
    }
}
