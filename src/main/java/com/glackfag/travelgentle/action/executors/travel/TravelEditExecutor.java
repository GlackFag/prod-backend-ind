package com.glackfag.travelgentle.action.executors.travel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glackfag.travelgentle.Bot;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.action.executors.ActionExecutor;
import com.glackfag.travelgentle.models.Travel;
import com.glackfag.travelgentle.services.InvitationService;
import com.glackfag.travelgentle.services.TravelService;
import com.glackfag.travelgentle.util.ParamRetriever;
import com.glackfag.travelgentle.util.telegram.MessageFormatter;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import com.glackfag.travelgentle.util.telegram.markup.MarkupBuilder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Optional;

@Component
public class TravelEditExecutor {
    private final TravelService travelService;
    private final Environment env;
    private final Bot bot;
    private final InvitationService invitationService;
    private final MessageFormatter messageFormatter;
    private final ActionExecutor actionExecutor;

    @Autowired
    public TravelEditExecutor(TravelService travelService, Environment env, Bot bot, InvitationService invitationService, MessageFormatter messageFormatter, @Lazy ActionExecutor actionExecutor) {
        this.travelService = travelService;
        this.env = env;
        this.bot = bot;
        this.invitationService = invitationService;
        this.messageFormatter = messageFormatter;
        this.actionExecutor = actionExecutor;
    }

    @SneakyThrows(JsonProcessingException.class)
    public void execute(Action action, Update update) throws TelegramApiException {
        Long chatId = UpdateUtils.extractChatId(update);
        String callback = UpdateUtils.extractCallbackDataText(update);
        int messageId = UpdateUtils.extractMessage(update).getMessageId();

        Map<String, String> params = ParamRetriever.getParamsFromUrl(callback);

        switch (action) {
            case CHOOSE_TRAVEL_TO_INVITE -> startChooseTravelToInvite(chatId);
            case SWITCH_CHOOSING_TRAVEL_PAGE -> {
                int page = Integer.parseInt(params.get("page"));
                startChooseTravelToInvite(chatId, page, messageId);
            }
            case SEND_INVITATION_CODE -> {
                int travelId = Integer.parseInt(params.get("id"));
                sendInvitationCode(chatId, travelId, messageId);
            }
            case REQUEST_INVITATION_CODE -> bot.sendMessage(chatId, env.getRequiredProperty("user.invite.requireCode"));
            case VALIDATE_INVITATION_CODE_AND_SEND_RESULT ->
                    validateInvitationCode(chatId, UpdateUtils.extractUserInput(update));
            case SUBMIT_TRAVEL_DELETION -> {
                int travelId = Integer.parseInt(params.get("id"));
                sendDeleteConfirmation(chatId, travelId, messageId);
            }
            case DELETE_TRAVEL -> {
                int travelId = Integer.parseInt(params.get("id"));
                deleteTravel(chatId, travelId, messageId);
            }
            case CANCEL_DELETE_TRAVEL ->{
                bot.execute(new DeleteMessage(chatId.toString(), messageId));
                actionExecutor.execute(Action.SEND_MENU, update);
            }
        }
    }

    private void startChooseTravelToInvite(Long chatId) throws TelegramApiException {
        Page<Travel> page = travelService.findAllByPersonId(chatId, 0);

        bot.sendMessage(chatId, env.getRequiredProperty("user.invite.menu"),
                MarkupBuilder.buildChooseTravelToInviteMarkup(page));
    }

    private void startChooseTravelToInvite(Long chatId, int pageNumber, int messageId) throws TelegramApiException {
        Page<Travel> page = travelService.findAllByPersonId(chatId, pageNumber);

        EditMessageText editMessageText = Bot.editTextOf(
                chatId, messageId,
                env.getRequiredProperty("user.invite.menu"),
                MarkupBuilder.buildChooseTravelToInviteMarkup(page),
                false);

        bot.execute(editMessageText);
    }

    private void sendInvitationCode(Long chatId, int travelId, int messageId) throws TelegramApiException {
        boolean isAccessed = travelService.isAccessed(travelId, chatId);
        Optional<Travel> optional = travelService.findById(travelId);

        String text;

        if (!isAccessed || optional.isEmpty())
            text = env.getRequiredProperty("travel.index.single.notFound");
        else {
            String code = invitationService.generate(optional.get());
            text = messageFormatter.invitationCode(code);
        }

        EditMessageText editMessageText = Bot.editTextOf(
                chatId, messageId,
                text,
                MarkupBuilder.emptyInlineKeyboard(),
                false);
        editMessageText.enableMarkdown(true);

        bot.execute(editMessageText);
    }

    private void validateInvitationCode(Long chatId, String code) throws TelegramApiException {
        Optional<Travel> optional = invitationService.useInvitation(code);
        String text;

        if (optional.isEmpty())
            text = env.getRequiredProperty("user.invite.invalid");
        else {
            Travel travel = optional.get();
            travelService.addParticipant(travel, chatId);

            text = env.getRequiredProperty("user.invite.access.success");
        }

        bot.sendMessage(chatId, text);
    }

    private void sendDeleteConfirmation(Long chatId, int travelId, int messageId) throws TelegramApiException {
        boolean isAccessed = travelService.isAccessed(travelId, chatId);
        Optional<Travel> optional = travelService.findById(travelId);

        String text;
        InlineKeyboardMarkup markup = null;

        if (!isAccessed || optional.isEmpty())
            text = env.getRequiredProperty("travel.index.single.notFound");
        else {
            text = String.format(env.getRequiredProperty("travel.delete.confirmation"), optional.get().getTitle());
            markup = MarkupBuilder.buildTravelDeletionConfirmation(travelId);
        }

        bot.execute(Bot.editTextOf(chatId, messageId, text,
                markup == null ? MarkupBuilder.emptyInlineKeyboard() : markup,
                false));
    }

    private void deleteTravel(Long chatId, int travelId, int messageId) throws TelegramApiException {
        travelService.deleteById(travelId);

        bot.execute(new DeleteMessage(chatId.toString(), messageId));
        bot.sendMessage(chatId, env.getRequiredProperty("travel.deleted"));
    }
}
