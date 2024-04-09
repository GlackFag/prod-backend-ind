package com.glackfag.travelgentle.action.recognizers;

import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.util.telegram.Commands;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class PersonRecognizer {
    private final PersonService personService;

    @Autowired
    public PersonRecognizer(PersonService personService) {
        this.personService = personService;
    }

    public Optional<Action> recognize(Update update) {
        Action last = personService.findLastActionById(UpdateUtils.extractUserId(update));
        String input = UpdateUtils.extractUserInput(update);
        String callback = UpdateUtils.extractCallbackDataText(update);

        if (isIndexRequesterProfile(input))
            return Optional.of(Action.INDEX_REQUESTER_PROFILE);

        if (isRequestEditingName(callback))
            return Optional.of(Action.REQUEST_EDITING_NAME);
        if (isValidateEditingName(last))
            return Optional.of(Action.VALIDATE_EDITING_NAME_AND_SEND_RESULTS);

        if (isRequestEditingAge(callback))
            return Optional.of(Action.REQUEST_EDITING_AGE);
        if (isValidateEditingAge(last))
            return Optional.of(Action.VALIDATE_EDITING_AGE_AND_SEND_RESULTS);

        if (isRequestEditingBio(callback))
            return Optional.of(Action.REQUEST_EDITING_BIO);
        if (isValidateEditingBio(last))
            return Optional.of(Action.VALIDATE_EDITING_BIO_AND_SEND_RESULTS);

        if (isRequestEditingHomeCity(callback))
            return Optional.of(Action.REQUEST_EDITING_HOME_CITY);
        if (isValidateEditingHomeCity(last))
            return Optional.of(Action.VALIDATE_EDITING_HOME_CITY_AND_SEND_RESULTS);
        if (isIndexParticipants(callback))
            return Optional.of(Action.INDEX_PARTICIPANTS);
        if (isSwitchParticipantIndexPage(callback))
            return Optional.of(Action.SWITCH_INDEXING_PARTICIPANTS_PAGE);
        if (isIndexSingleParticipant(callback))
            return Optional.of(Action.INDEX_SINGLE_PARTICIPANT);

        return Optional.empty();
    }

    private boolean isIndexRequesterProfile(String input) {
        return Commands.ME.equalsIgnoreCase(input);
    }

    private boolean isRequestEditingName(String callback) {
        return Commands.Person.EDIT_NAME.equals(callback);
    }

    private boolean isValidateEditingName(Action last) {
        return last == Action.REQUEST_EDITING_NAME;
    }

    private boolean isRequestEditingAge(String callback) {
        return Commands.Person.EDIT_AGE.equals(callback);
    }

    private boolean isValidateEditingAge(Action last) {
        return last == Action.REQUEST_EDITING_AGE;
    }

    private boolean isRequestEditingBio(String callback) {
        return Commands.Person.EDIT_BIO.equals(callback);
    }

    private boolean isValidateEditingBio(Action last) {
        return last == Action.VALIDATE_EDITING_BIO_AND_SEND_RESULTS || last == Action.REQUEST_EDITING_BIO;
    }

    private boolean isRequestEditingHomeCity(String callback) {
        return Commands.Person.EDIT_HOME_CITY.equals(callback);
    }

    private boolean isValidateEditingHomeCity(Action last) {
        return last == Action.REQUEST_EDITING_HOME_CITY || last == Action.VALIDATE_EDITING_HOME_CITY_AND_SEND_RESULTS;
    }

    private boolean isIndexParticipants(String callback) {
        return callback.startsWith(Commands.Person.INDEX_BY_TRAVEL_ID);
    }

    private boolean isSwitchParticipantIndexPage(String callback) {
        return Commands.Person.INDEX_PARTICIPANTS.equals(callback.replaceAll("\\d+", "%d"));
    }

    private boolean isIndexSingleParticipant(String callback) {
        return Commands.Person.INDEX_ID.equals(callback.replaceAll("\\d+", "%d"));
    }
}
