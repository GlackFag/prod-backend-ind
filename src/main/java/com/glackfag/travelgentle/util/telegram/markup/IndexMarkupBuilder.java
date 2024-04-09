package com.glackfag.travelgentle.util.telegram.markup;

import com.glackfag.travelgentle.models.IntermediatePoint;
import com.glackfag.travelgentle.models.Person;
import com.glackfag.travelgentle.models.Travel;
import com.glackfag.travelgentle.util.telegram.Commands;
import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class IndexMarkupBuilder {
    public static InlineKeyboardMarkup buildTravelIndexMarkup(Page<Travel> page) {
        List<Travel> travels = page.toList();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int size = travels.size();

        for (int i = 0; i < travels.size() - (size % 2 == 0 ? 1 : 2); i += 2) {
            Travel travel1 = travels.get(i);
            Travel travel2 = travels.get(i + 1);

            InlineKeyboardButton button1 = new InlineKeyboardButton(travel1.getTitle());
            button1.setCallbackData(Commands.Travel.INDEX_ID + travel1.getId());

            InlineKeyboardButton button2 = new InlineKeyboardButton(travel2.getTitle());
            button2.setCallbackData(Commands.Travel.INDEX_ID + travel2.getId());

            rows.add(List.of(button1, button2));
        }
        if (travels.size() % 2 != 0) {
            Travel last = travels.get(size - 1);
            InlineKeyboardButton button = new InlineKeyboardButton(last.getTitle());
            button.setCallbackData(Commands.Travel.INDEX_ID + last.getId());

            rows.add(List.of(button));
        }
        List<InlineKeyboardButton> navigation;

        navigation = buildNavigationRow(page, Commands.Travel.INDEX_PAGE);

        rows.add(navigation);

        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup buildPointsOfTravelMarkup(Page<IntermediatePoint> page) {
        List<IntermediatePoint> points = page.toList();
        points = points.stream().sorted().toList();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int size = points.size();

        for (int i = 0; i < points.size() - (size % 2 == 0 ? 1 : 2); i += 2) {
            IntermediatePoint point1 = points.get(i);
            IntermediatePoint point2 = points.get(i + 1);

            InlineKeyboardButton button1 = new InlineKeyboardButton(point1.getAddress().getCity());
            button1.setCallbackData(Commands.IntermediatePoint.INDEX_ID + point1.getId());
            button1.setCallbackData(String.format(Commands.IntermediatePoint.INDEX_ID,
                    point1.getId(), page.getNumber()));

            InlineKeyboardButton button2 = new InlineKeyboardButton(point2.getAddress().getCity());
            button2.setCallbackData(String.format(Commands.IntermediatePoint.INDEX_ID,
                    point2.getId(), page.getNumber()));

            rows.add(List.of(button1, button2));
        }
        if (points.size() % 2 != 0) {
            IntermediatePoint last = points.get(size - 1);
            InlineKeyboardButton button = new InlineKeyboardButton(last.getAddress().getCity());
            button.setCallbackData(String.format(Commands.IntermediatePoint.INDEX_ID,
                    last.getId(), page.getNumber()));

            rows.add(List.of(button));
        }

        if (points.isEmpty())
            rows.add(buildNavigationRow(page, Commands.IntermediatePoint.INDEX_PAGE));
        else {
            int travelId = points.get(0).getTravel().getId();
            rows.add(buildNavigationRow(page, Commands.IntermediatePoint.INDEX_PAGE, travelId));
        }

        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup buildChooseTravelToInviteMarkup(Page<Travel> page) {
        List<Travel> travels = page.toList();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int size = travels.size();

        for (int i = 0; i < travels.size() - (size % 2 == 0 ? 1 : 2); i += 2) {
            Travel travel1 = travels.get(i);
            Travel travel2 = travels.get(i + 1);

            InlineKeyboardButton button1 = new InlineKeyboardButton(travel1.getTitle());
            button1.setCallbackData(Commands.Travel.INVITE_TRAVEL_ID + travel1.getId());

            InlineKeyboardButton button2 = new InlineKeyboardButton(travel2.getTitle());
            button2.setCallbackData(Commands.Travel.INVITE_TRAVEL_ID + travel2.getId());

            rows.add(List.of(button1, button2));
        }
        if (travels.size() % 2 != 0) {
            Travel last = travels.get(size - 1);
            InlineKeyboardButton button = new InlineKeyboardButton(last.getTitle());
            button.setCallbackData(Commands.Travel.INVITE_TRAVEL_ID + last.getId());

            rows.add(List.of(button));
        }
        List<InlineKeyboardButton> navigation;

        navigation = buildNavigationRow(page, Commands.Travel.INDEX_PAGE);

        rows.add(navigation);

        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup buildParticipantsOfTravelMarkup(Page<Person> page, int travelId) {
        List<Person> people = page.toList();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int size = people.size();

        for (int i = 0; i < people.size() - (size % 2 == 0 ? 1 : 2); i += 2) {
            Person person1 = people.get(i);
            Person person2 = people.get(i + 1);

            InlineKeyboardButton button1 = new InlineKeyboardButton(person1.getName());
            button1.setCallbackData(String.format(Commands.Person.INDEX_ID, person1.getId(), page.getNumber(), travelId));

            InlineKeyboardButton button2 = new InlineKeyboardButton(person2.getName());
            button2.setCallbackData(String.format(Commands.Person.INDEX_ID, person2.getId(), page.getNumber(), travelId));

            rows.add(List.of(button1, button2));
        }
        if (people.size() % 2 != 0) {
            Person last = people.get(size - 1);
            InlineKeyboardButton button = new InlineKeyboardButton(last.getName());
            button.setCallbackData(String.format(Commands.Person.INDEX_ID, last.getId(), page.getNumber(), travelId));

            rows.add(List.of(button));
        }
        List<InlineKeyboardButton> navigation;

        navigation = buildNavigationRow(page, Commands.Person.INDEX_PARTICIPANTS, travelId);

        rows.add(navigation);

        return new InlineKeyboardMarkup(rows);

    }

    private static <T> List<InlineKeyboardButton> buildNavigationRow(Page<T> page, String callbackCommand, Object... commandParams) {
        List<InlineKeyboardButton> navigation = new ArrayList<>();


        if (!page.isFirst()) {
            List<Object> args = new ArrayList<>(Arrays.asList(commandParams));
            args.add(0, page.getNumber() - 1);

            InlineKeyboardButton last = new InlineKeyboardButton("Last page");
            last.setCallbackData(String.format(callbackCommand, args.toArray()));

            navigation.add(last);
        }

        if (!page.isLast()) {
            List<Object> args = new ArrayList<>(Arrays.asList(commandParams));
            args.add(0, page.getNumber() + 1);

            InlineKeyboardButton next = new InlineKeyboardButton("Next page");
            next.setCallbackData(String.format(callbackCommand, args.toArray()));

            navigation.add(next);
        }

        return navigation;
    }

    public static InlineKeyboardMarkup buildTravelPointsAndParticipants(int travelId) {
        InlineKeyboardButton pointIndexButton = new InlineKeyboardButton("Intermediate points");
        pointIndexButton.setCallbackData(Commands.IntermediatePoint.INDEX_BY_TRAVEL_ID + travelId);

        InlineKeyboardButton participantIndexButton = new InlineKeyboardButton("Participants");
        participantIndexButton.setCallbackData(Commands.Person.INDEX_BY_TRAVEL_ID + travelId);

        InlineKeyboardButton delete = new InlineKeyboardButton("Delete travel");
        delete.setCallbackData(Commands.Travel.CONFIRM_DELETE_TRAVEL_ID + travelId);

        return new InlineKeyboardMarkup(List.of(List.of(pointIndexButton, participantIndexButton), List.of(delete)));
    }

    public static InlineKeyboardMarkup buildIndexSinglePointOptions(IntermediatePoint point, int fromPageNumber) {
        String fromPageCommand = String.format(Commands.IntermediatePoint.INDEX_PAGE,
                fromPageNumber, point.getTravel().getId());

        InlineKeyboardButton back = new InlineKeyboardButton("Back");
        back.setCallbackData(fromPageCommand);

        return new InlineKeyboardMarkup(List.of(List.of(back)));
    }
}
