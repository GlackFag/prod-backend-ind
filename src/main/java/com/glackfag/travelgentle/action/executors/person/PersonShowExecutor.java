package com.glackfag.travelgentle.action.executors.person;

import com.glackfag.travelgentle.Bot;
import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.models.Person;
import com.glackfag.travelgentle.services.AddressService;
import com.glackfag.travelgentle.services.PersonService;
import com.glackfag.travelgentle.util.ParamRetriever;
import com.glackfag.travelgentle.util.telegram.MessageFormatter;
import com.glackfag.travelgentle.util.telegram.UpdateUtils;
import com.glackfag.travelgentle.util.telegram.markup.MarkupBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Optional;

@Component
public class PersonShowExecutor {
    private final PersonService personService;
    private final AddressService addressService;
    private final Bot bot;
    private final MessageFormatter messageFormatter;
    private final Environment env;

    @Autowired
    public PersonShowExecutor( PersonService personService, AddressService addressService,
                              Bot bot, MessageFormatter messageFormatter, Environment env) {
        this.personService = personService;
        this.addressService = addressService;
        this.bot = bot;
        this.messageFormatter = messageFormatter;
        this.env = env;
    }

    public void execute(Action action, Update update) throws TelegramApiException {
        Long chatId = UpdateUtils.extractChatId(update);
        Message message = UpdateUtils.extractMessage(update);
        Map<String, String> params = ParamRetriever.getParamsFromUrl(UpdateUtils.extractCallbackDataText(update));
        System.out.println(UpdateUtils.extractCallbackDataText(update));
        System.out.println(action);
        switch (action) {
            case INDEX_REQUESTER_PROFILE -> indexRequesterProfile(chatId);
            case INDEX_PARTICIPANTS, SWITCH_INDEXING_PARTICIPANTS_PAGE -> {
                int travelId = Integer.parseInt(params.get("travelId"));
                int page = Integer.parseInt(
                        Optional.ofNullable(params.get("page")).orElse("0"));

                indexParticipants(chatId, message.getMessageId(), travelId, page);
            }
            case INDEX_SINGLE_PARTICIPANT -> {
                Long personId = Long.parseLong(params.get("id"));
                int pageFrom = Integer.parseInt(params.get("pageFromNumber"));
                int travelId = Integer.parseInt(params.get("travelId"));

                indexSingleProfile(chatId, message.getMessageId(), personId, pageFrom, travelId);
            }
        }
    }

    private void indexRequesterProfile(Long chatId) throws TelegramApiException {
        Person person = personService.findById(chatId).orElseThrow();
        Address address = addressService.findById(person.getAddressId()).orElse(new Address("Unknown", "Unknown"));

        String text = messageFormatter.indexSingleProfile(person, address);

        SendMessage sendMessage = new SendMessage(chatId.toString(), text);
        sendMessage.setReplyMarkup(MarkupBuilder.editProfileFieldsMarkup());
        sendMessage.enableHtml(true);

        bot.execute(sendMessage);
    }

    private void indexParticipants(Long chatId, int messageId, int travelId, int pageNumber) throws TelegramApiException {
        Page<Person> page = personService.findByTravelId(travelId, pageNumber);
        System.out.println(page);
        InlineKeyboardMarkup markup = MarkupBuilder.buildParticipantsOfTravelMarkup(page, travelId);
        System.out.println(page.toList());
        bot.execute(Bot.editTextOf(
                chatId, messageId,
                env.getRequiredProperty("travel.index.participants"),
                markup, false)
        );
    }

    private void indexSingleProfile(Long chatId, int messageId, Long personId, int pageFrom, int travelId) throws TelegramApiException {
        Person person = personService.findById(personId).orElseThrow();
        Address address = addressService.findById(person.getAddressId()).orElse(new Address("Unknown", "Unknown"));

        InlineKeyboardMarkup markup = MarkupBuilder.backToParticipantIndex(travelId, pageFrom);
        String text = messageFormatter.indexSingleProfile(person, address);


        bot.execute(Bot.editTextOf(
                chatId, messageId,
                text, markup,
                true));
    }
}
