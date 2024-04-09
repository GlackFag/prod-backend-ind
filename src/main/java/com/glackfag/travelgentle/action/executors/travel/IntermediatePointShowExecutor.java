package com.glackfag.travelgentle.action.executors.travel;

import com.glackfag.travelgentle.Bot;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.models.IntermediatePoint;
import com.glackfag.travelgentle.services.IntermediatePointService;
import com.glackfag.travelgentle.services.TravelService;
import com.glackfag.travelgentle.util.ParamRetriever;
import com.glackfag.travelgentle.util.telegram.MessageFormatter;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import com.glackfag.travelgentle.util.telegram.markup.MarkupBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Component
public class IntermediatePointShowExecutor {
    private final IntermediatePointService intermediatePointService;
    private final TravelService travelService;
    private final Environment env;
    private final Bot bot;
    private final MessageFormatter messageFormatter;

    @Autowired
    public IntermediatePointShowExecutor(IntermediatePointService intermediatePointService, TravelService travelService, Environment env, Bot bot, MessageFormatter messageFormatter) {
        this.intermediatePointService = intermediatePointService;
        this.travelService = travelService;
        this.env = env;
        this.bot = bot;
        this.messageFormatter = messageFormatter;
    }

    public void execute(Action action, Update update) throws TelegramApiException {
        Long chatId = UpdateUtils.extractUserId(update);
        String callbackData = UpdateUtils.extractCallbackDataText(update);
        int messageId = UpdateUtils.extractMessage(update).getMessageId();

        Map<String, String> params = ParamRetriever.getParamsFromUrl(callbackData);

        switch (action) {
            case INDEX_POINTS_BY_TRAVEL_ID -> {
                int travelId = Integer.parseInt(params.get("travelId"));
                indexPointsByTravelId(chatId, travelId, messageId);
            }
            case SWITCH_POINT_INDEX_PAGE -> {
                int travelId = Integer.parseInt(params.get("travelId"));
                int page = Integer.parseInt(params.get("page"));

                indexPointsByTravelIdAndPage(chatId, travelId, messageId, page);
            }
            case INDEX_SINGLE_POINT -> {
                int pointId = Integer.parseInt(params.get("id"));
                int pageFromNumber = Integer.parseInt(params.get("pageFromNumber"));
                indexSinglePoint(chatId, messageId, pointId, pageFromNumber);
            }
        }
    }

    private void indexPointsByTravelId(Long chatId, int travelId, int messageId) throws TelegramApiException {
        indexPointsByTravelIdAndPage(chatId, travelId, messageId, 0);
    }

    private void indexPointsByTravelIdAndPage(Long chatId, int travelId, int messageId, int page) throws TelegramApiException {
        boolean isAccessed = travelService.isAccessed(travelId, chatId);
        EditMessageText editMessage;

        if (isAccessed) {
            Page<IntermediatePoint> points = intermediatePointService.findByTravelId(travelId, page);

            editMessage = Bot.editTextOf(
                    chatId, messageId,
                    "points of travel",
                    MarkupBuilder.buildPointsOfTravelMarkup(points),
                    false);
        } else
            editMessage = Bot.editTextOf(
                    chatId, messageId,
                    env.getRequiredProperty("travel.index.single.notFound"),
                    MarkupBuilder.emptyInlineKeyboard(), false);


        bot.execute(editMessage);
    }

    private void indexSinglePoint(Long chatId, int messageId, int pointId, int fromPageNumber) throws TelegramApiException {
        boolean isAccessed = intermediatePointService.isAccessed(chatId, pointId);
        EditMessageText editMessage;

        if (isAccessed) {
            IntermediatePoint point = intermediatePointService.findById(pointId).orElseThrow();

            editMessage = Bot.editTextOf(
                    chatId, messageId,
                    messageFormatter.indexSinglePoint(point),
                    MarkupBuilder.buildIndexSinglePointOptions(point, fromPageNumber),
                    true);
        } else
            editMessage = Bot.editTextOf(
                    chatId, messageId,
                    env.getRequiredProperty("travel.index.single.notFound"),
                    MarkupBuilder.emptyInlineKeyboard(), false);

        bot.execute(editMessage);
    }
}
