package com.glackfag.travelgentle.action.recognizers;


import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.services.creating.RegisteringPersonService;
import com.glackfag.travelgentle.util.telegram.Commands;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class RegistrationActionsRecognizer {
    private final RegisteringPersonService registeringService;
    private final PersonService personService;

    @Autowired
    public RegistrationActionsRecognizer(RegisteringPersonService registeringService, PersonService personService) {
        this.registeringService = registeringService;
        this.personService = personService;
    }

    public Optional<Action> recognize(Update update) {
        Long userId = UpdateUtils.extractUserId(update);
        String input = UpdateUtils.extractUserInput(update);

        Message message = UpdateUtils.extractMessage(update);
        Action lastAction = registeringService.findLastActionById(userId);

        if (isSendGreetingsAndRequestName(input, userId))
            return Optional.of(Action.SEND_GREETINGS_AND_REQUEST_NAME);
        if (isValidateNameAndSendResult(lastAction))
            return Optional.of(Action.VALIDATE_NAME_AND_SEND_RESULT);
        if (isValidateAgeAndSendResult(lastAction))
            return Optional.of(Action.VALIDATE_AGE_AND_SEND_RESULT);
        if (isSaveAddress(lastAction, message))
            return Optional.of(Action.PARSE_AND_SAVE_ADDRESS);
        if (isCancelRegistration(input))
            return Optional.of(Action.CANCEL_REGISTRATION);
        if(isValidateAndSaveAddress(lastAction))
            return Optional.of(Action.VALIDATE_AND_SAVE_ADDRESS);

        return Optional.empty();
    }

    private boolean isSendGreetingsAndRequestName(String input, Long userId) {
        return Commands.START.equalsIgnoreCase(input) &&
                !personService.existsById(userId);
    }

    private boolean isValidateNameAndSendResult(Action lastAction) {
        return lastAction == Action.SEND_GREETINGS_AND_REQUEST_NAME;
    }

    private boolean isValidateAgeAndSendResult(Action lastAction) {
        return lastAction == Action.REQUEST_AGE;
    }


    private boolean isSaveAddress(Action lastAction, Message message) {
        return message.hasLocation() && lastAction == Action.REQUEST_ADDRESS;
    }

    private boolean isCancelRegistration(String input) {
        return input.equalsIgnoreCase(Commands.Registration.CANCEL);
    }

    private boolean isValidateAndSaveAddress(Action lastAction){
        return lastAction == Action.REQUEST_ADDRESS_MANUALLY;
    }
}
