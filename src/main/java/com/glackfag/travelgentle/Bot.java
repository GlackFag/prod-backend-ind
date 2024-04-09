package com.glackfag.travelgentle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.action.executors.ActionExecutor;
import com.glackfag.travelgentle.action.recognizers.ActionRecognizer;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import com.glackfag.travelgentle.util.telegram.markup.MarkupBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {
    @Getter
    private final String botUsername;
    private final ActionExecutor actionExecutor;
    private final ActionRecognizer actionRecognizer;

    @Autowired
    public Bot(@Value("${bot.token}") String botToken,
               @Value(("${bot.username}")) String botUsername,
               TelegramBotsApi api, @Lazy ActionExecutor actionExecutor, ActionRecognizer actionRecognizer) {
        super(botToken);
        this.botUsername = botUsername;
        this.actionExecutor = actionExecutor;
        this.actionRecognizer = actionRecognizer;

        try {
            api.registerBot(this);
        } catch (TelegramApiException e) {
            log.atError().log(e.getMessage());
        }
    }

    @SneakyThrows(TelegramApiException.class)
    @Override
    public void onUpdateReceived(Update update) {
        try {
            Action action = actionRecognizer.recognize(update);
            log.info("Action:{}; UserId:{}; Input:{}; CallbackData Text:{}", action.toString(), UpdateUtils.extractUserId(update), UpdateUtils.extractUserInput(update), UpdateUtils.extractCallbackDataText(update));

            actionExecutor.execute(action, update);
        } catch (TelegramApiException | JsonProcessingException e) {
            log.atError().log(e.getMessage());
            execute(new SendMessage(UpdateUtils.extractChatId(update).toString(), "Something went wrong"));
        }

    }

    public void sendMessage(Long chatId, String content) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId.toString(), content);
        execute(sendMessage);
    }

    public void sendMessage(Long chatId, String content, ReplyKeyboard markup) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId.toString(), content);
        sendMessage.setReplyMarkup(markup);
        execute(sendMessage);
    }

    public static EditMessageText editTextOf(Long chatId, int messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        return editMessageText;
    }

    public static EditMessageText editTextOf(Long chatId, int messageId, String text, InlineKeyboardMarkup markup, boolean enableHtml) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        editMessageText.setReplyMarkup(markup);
        editMessageText.enableHtml(enableHtml);

        return editMessageText;
    }

    public void removeInlineMarkup(Long chatId, Message message) throws TelegramApiException {
        EditMessageText editMessageText =
                editTextOf(chatId, message.getMessageId(),
                        message.getText(),
                        MarkupBuilder.emptyInlineKeyboard(), false);

        execute(editMessageText);
    }
}
