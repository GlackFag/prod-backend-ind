package com.glackfag.travelgentle.action.recognizers;

import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.action.recognizers.travel.IntermediatePointRecognizer;
import com.glackfag.travelgentle.action.recognizers.travel.TravelRecognizer;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.util.telegram.Commands;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class ActionRecognizer {
    private final RegistrationActionsRecognizer registrationActionsRecognizer;
    private final TravelRecognizer travelRecognizer;
    private final IntermediatePointRecognizer pointRecognizer;
    private final PersonRecognizer personRecognizer;
    private final PersonService personService;

    @Autowired
    public ActionRecognizer(RegistrationActionsRecognizer registrationActionsRecognizer, TravelRecognizer travelRecognizer, IntermediatePointRecognizer pointRecognizer, PersonRecognizer personRecognizer, PersonService personService) {
        this.registrationActionsRecognizer = registrationActionsRecognizer;
        this.travelRecognizer = travelRecognizer;
        this.pointRecognizer = pointRecognizer;
        this.personRecognizer = personRecognizer;
        this.personService = personService;
    }

    public Action recognize(Update update) {
        String input = UpdateUtils.extractUserInput(update);
        Long userId = UpdateUtils.extractUserId(update);

        if (isAskToRegister(update))
            return Action.ASK_TO_REGISTER;
        if (isSendMenu(input))
            return Action.SEND_MENU;
        if (isRequestLocationForSuggestSight(input))
            return Action.REQUEST_LOCATION_FOR_SUGGEST_SIGHT;
        if (isSuggestSight(personService.findLastActionById(userId), UpdateUtils.extractMessage(update)))
            return Action.SUGGEST_SIGHT;
        if (isSuggestNextSight(UpdateUtils.extractCallbackDataText(update)))
            return Action.SUGGEST_NEXT_SIGHT;


        Optional<Action> action = registrationActionsRecognizer.recognize(update);

        if (action.isPresent())
            return action.get();

        action = travelRecognizer.recognize(update);

        if (action.isPresent())
            return action.get();

        action = pointRecognizer.recognize(update);

        if (action.isPresent())
            return action.get();

        action = personRecognizer.recognize(update);

        return action.orElse(Action.IGNORE);
    }

    private static boolean isSendMenu(String input) {
        return Commands.CANCEL.equalsIgnoreCase(input);
    }

    private boolean isAskToRegister(Update update) {
        Long userId = UpdateUtils.extractUserId(update);
        String input = UpdateUtils.extractUserInput(update);

        return input.startsWith("/") && !Commands.START.equalsIgnoreCase(input) &&
                !personService.existsById(userId);
    }

    private boolean isRequestLocationForSuggestSight(String input) {
        return Commands.SUGGEST_SIGHT.equalsIgnoreCase(input);
    }

    private boolean isSuggestSight(Action last, Message message) {
        return message.hasLocation() && last == Action.REQUEST_LOCATION_FOR_SUGGEST_SIGHT;
    }

    private boolean isSuggestNextSight(String callbackData) {
       String[] url = Commands.NEXT_SUGGESTION.split("%s");
       for(String e : url)
           if(!callbackData.contains(e))
               return false;
       return true;
    }
}
