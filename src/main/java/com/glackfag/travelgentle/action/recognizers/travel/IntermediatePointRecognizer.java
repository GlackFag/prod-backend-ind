package com.glackfag.travelgentle.action.recognizers.travel;

import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.util.telegram.Commands;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class IntermediatePointRecognizer {
    public Optional<Action> recognize(Update update) {
        String callbackData = UpdateUtils.extractCallbackDataText(update);

        if (isIndexPointsByTravelId(callbackData))
            return Optional.of(Action.INDEX_POINTS_BY_TRAVEL_ID);
        if (isSwitchPointIndexPage(callbackData))
            return Optional.of(Action.SWITCH_POINT_INDEX_PAGE);
        if (isIndexSinglePoint(callbackData))
            return Optional.of(Action.INDEX_SINGLE_POINT);

        return Optional.empty();
    }

    private boolean isIndexPointsByTravelId(String callbackData) {
        return callbackData.startsWith(Commands.IntermediatePoint.INDEX_BY_TRAVEL_ID);
    }

    private boolean isSwitchPointIndexPage(String callbackData) {
        return Commands.IntermediatePoint.INDEX_PAGE.equals(callbackData.replaceAll("\\d+", "%d"));
    }

    private boolean isIndexSinglePoint(String callbackData){
        return Commands.IntermediatePoint.INDEX_ID.equals(callbackData.replaceAll("\\d+", "%d"));
    }
}
