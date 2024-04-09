package com.glackfag.travelgentle.util.telegram.markup;

import com.glackfag.travelgentle.models.IntermediatePoint;
import com.glackfag.travelgentle.models.Person;
import com.glackfag.travelgentle.models.Travel;
import com.glackfag.travelgentle.util.telegram.Commands;
import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collections;
import java.util.List;

public class MarkupBuilder {
    private static final InlineKeyboardMarkup emptyInlineKeyboard = new InlineKeyboardMarkup(List.of(Collections.emptyList()));
    private static final InlineKeyboardMarkup doRequestMorePointsMarkup;
    private static final ReplyKeyboardMarkup registrationLocationMarkup;
    private static final ReplyKeyboardMarkup shareLocationMarkup;
    private static final InlineKeyboardMarkup editProfileFieldsMarkup;
    private static final ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);

    static {
        {
            InlineKeyboardButton saveTravel = new InlineKeyboardButton("Save travel");
            saveTravel.setCallbackData(Commands.Travel.SAVE_TRAVEL);

            InlineKeyboardButton anotherPoint = new InlineKeyboardButton("Another point");
            anotherPoint.setCallbackData(Commands.Travel.ANOTHER_POINT);

            doRequestMorePointsMarkup = new InlineKeyboardMarkup(List.of(List.of(saveTravel), List.of(anotherPoint)));
        }
        {
            KeyboardButton viaTelegram = new KeyboardButton("Share my location");
            viaTelegram.setRequestLocation(true);

            KeyboardButton cancel = new KeyboardButton(Commands.Registration.CANCEL);

            KeyboardRow row = new KeyboardRow(List.of(cancel, viaTelegram));

            registrationLocationMarkup = new ReplyKeyboardMarkup(List.of(row));
            shareLocationMarkup = new ReplyKeyboardMarkup(List.of(new KeyboardRow(List.of(viaTelegram))));
        }
        {
            InlineKeyboardButton editName = new InlineKeyboardButton("Edit name");
            editName.setCallbackData(Commands.Person.EDIT_NAME);

            InlineKeyboardButton editAge = new InlineKeyboardButton("Edit age");
            editAge.setCallbackData(Commands.Person.EDIT_AGE);

            InlineKeyboardButton editBio = new InlineKeyboardButton("Edit bio");
            editBio.setCallbackData(Commands.Person.EDIT_BIO);

            InlineKeyboardButton editHomeCity = new InlineKeyboardButton("Edit home city");
            editHomeCity.setCallbackData(Commands.Person.EDIT_HOME_CITY);

            editProfileFieldsMarkup = new InlineKeyboardMarkup(List.of(List.of(editName),
                    List.of(editAge), List.of(editBio), List.of(editHomeCity)));
        }
    }

    public static ReplyKeyboardRemove removeMarkup() {
        return removeMarkup;
    }

    public static InlineKeyboardMarkup emptyInlineKeyboard() {
        return emptyInlineKeyboard;
    }

    public static ReplyKeyboardMarkup registrationLocationMarkup() {
        return registrationLocationMarkup;
    }

    public static ReplyKeyboardMarkup shareLocationMarkup() {
        return shareLocationMarkup;
    }

    public static InlineKeyboardMarkup doRequestMorePointsMarkup() {
        return doRequestMorePointsMarkup;
    }

    public static InlineKeyboardMarkup buildTravelIndexMarkup(Page<Travel> page) {
        return IndexMarkupBuilder.buildTravelIndexMarkup(page);
    }

    public static InlineKeyboardMarkup buildPointsOfTravelMarkup(Page<IntermediatePoint> page) {
        return IndexMarkupBuilder.buildPointsOfTravelMarkup(page);
    }

    public static InlineKeyboardMarkup buildParticipantsOfTravelMarkup(Page<Person> page, int travelId){
        return IndexMarkupBuilder.buildParticipantsOfTravelMarkup(page ,travelId);
    }

    public static InlineKeyboardMarkup buildTravelPointsAndParticipants(int travelId) {
        return IndexMarkupBuilder.buildTravelPointsAndParticipants(travelId);
    }

    public static InlineKeyboardMarkup buildIndexSinglePointOptions(IntermediatePoint point, int fromPageNumber) {
        return IndexMarkupBuilder.buildIndexSinglePointOptions(point, fromPageNumber);
    }

    public static InlineKeyboardMarkup buildChooseTravelToInviteMarkup(Page<Travel> page) {
        return IndexMarkupBuilder.buildChooseTravelToInviteMarkup(page);
    }

    public static InlineKeyboardMarkup editProfileFieldsMarkup() {
        return editProfileFieldsMarkup;
    }

    public static InlineKeyboardMarkup buildNextSuggestionMarkup(String city, String[] ignore) {
        InlineKeyboardButton next = new InlineKeyboardButton("Next suggestion");
        next.setCallbackData(String.format(Commands.NEXT_SUGGESTION, city, String.join(";", ignore)));

        return new InlineKeyboardMarkup(List.of(List.of(next)));
    }

    public static InlineKeyboardMarkup buildTravelDeletionConfirmation(int travelId) {
        InlineKeyboardButton sure = new InlineKeyboardButton("I'm Sure");
        sure.setCallbackData(Commands.Travel.DELETE_TRAVEL_ID + travelId);

        InlineKeyboardButton cancel = new InlineKeyboardButton("Cancel");
        cancel.setCallbackData(Commands.Travel.CANCEL_DELETION);

        return new InlineKeyboardMarkup(List.of(List.of(sure), List.of(cancel)));
    }

    public static InlineKeyboardMarkup backToParticipantIndex(int travelId, int page){
        InlineKeyboardButton button = new InlineKeyboardButton("Back");
        button.setCallbackData(String.format(Commands.Person.INDEX_PARTICIPANTS, page, travelId));

        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }
}
