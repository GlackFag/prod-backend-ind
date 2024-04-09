package com.glackfag.travelgentle.action.executors.travel;

import com.glackfag.travelgentle.Bot;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.models.Travel;
import com.glackfag.travelgentle.services.TravelService;
import com.glackfag.travelgentle.util.telegram.markup.MarkupBuilder;
import com.glackfag.travelgentle.util.ParamRetriever;
import com.glackfag.travelgentle.util.telegram.Commands;
import com.glackfag.travelgentle.util.telegram.MessageFormatter;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TravelShowExecutor {
    private final TravelService travelService;
    private final Environment env;
    private final Bot bot;
    private final MessageFormatter messageFormatter;

    @Autowired
    public TravelShowExecutor(TravelService travelService, Environment env, Bot bot, MessageFormatter messageFormatter) {
        this.travelService = travelService;
        this.env = env;
        this.bot = bot;
        this.messageFormatter = messageFormatter;
    }

    public void execute(Action action, Update update) {
        Long chatId = UpdateUtils.extractChatId(update);
        String callbackData = UpdateUtils.extractCallbackDataText(update);
        int messageId = UpdateUtils.extractMessage(update).getMessageId();

        Map<String, String> params = ParamRetriever.getParamsFromUrl(callbackData);

        try {
            switch (action) {
                case INDEX_TRAVELS -> index(chatId);
                case SWITCH_TRAVEL_INDEX_PAGE -> {
                    int pageNumber = Integer.parseInt(params.get("page"));

                    index(chatId, pageNumber, messageId);
                }
                case INDEX_SINGLE_TRAVEL -> indexById(chatId, extractTravelId(callbackData), messageId);
            }
        } catch (TelegramApiException | RuntimeException e) {
            log.atDebug().setCause(e).setMessage(e.getMessage()).log();
            throw new RuntimeException(e);
        }
    }


    private int extractTravelId(String callbackData) {
        callbackData = callbackData.replaceFirst(Pattern.quote(Commands.Travel.INDEX_ID), "");

        return Integer.parseInt(callbackData);
    }

    private void index(Long chatId) throws TelegramApiException {
        Page<Travel> page = travelService.findAllByPersonId(chatId, 0);

        if (page.isEmpty()) {
            bot.sendMessage(chatId, env.getRequiredProperty("travel.index.empty"));
            return;
        }

        bot.sendMessage(chatId, env.getRequiredProperty("travel.index.all"),
                MarkupBuilder.buildTravelIndexMarkup(page));
    }

    private void index(Long chatId, int pageNumber, int messageId) throws TelegramApiException {
        Page<Travel> page = travelService.findAllByPersonId(chatId, pageNumber);
        EditMessageText editMessage = Bot.editTextOf(chatId, messageId);

        editMessage.setText(env.getRequiredProperty("travel.index.all"));
        editMessage.setReplyMarkup(MarkupBuilder.buildTravelIndexMarkup(page));

        bot.execute(editMessage);
    }

    private void indexById(Long chatId, int travelId, int messageId) throws TelegramApiException {
        Optional<Travel> optional = travelService.findById(travelId);
        InlineKeyboardMarkup markup;

        String text;
        boolean isAccessed = false;

        if (optional.isPresent()) {
            Travel travel = optional.get();

            isAccessed = travel.getOrganizer().getId() == chatId;
            markup = MarkupBuilder.buildTravelPointsAndParticipants(travel.getId());
        } else
            markup = MarkupBuilder.emptyInlineKeyboard();

        if (!isAccessed)
            text = env.getRequiredProperty("travel.index.single.notFound");

        else
            text = messageFormatter.indexSingleTravel(optional.get());


        EditMessageText editMessage = Bot.editTextOf(chatId, messageId);

        editMessage.setText(text);
        editMessage.enableHtml(true);
        editMessage.setReplyMarkup(markup);


        bot.execute(editMessage);
    }
}
