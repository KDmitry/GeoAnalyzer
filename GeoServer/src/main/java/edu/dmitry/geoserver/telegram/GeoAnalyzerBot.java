package edu.dmitry.geoserver.telegram;

import edu.dmitry.geoserver.Manager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.updateshandlers.SentCallback;

import java.util.HashSet;
import java.util.Set;

public class GeoAnalyzerBot extends TelegramLongPollingBot {
    private Logger logger = LogManager.getRootLogger();
    private Manager manager;
    private Set<Long> chatsId = new HashSet<>();

    public GeoAnalyzerBot(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            logger.info("Get message from chatId = " + message.getChatId().toString() + " message = " + message.getText());
            if (message.getText().equals("/start")) {
                sendMessage(message.getChatId().toString(), "Привет, я частный бот");
            } else if (message.getText().equals("/help")) {
                sendMessage(message.getChatId().toString(), "Я частный бот");
            } else if (message.getText().startsWith("/stat")) {
                String[] args = message.getText().split(" ");
                if (args.length == 2 && args[1].equals("яизмиэм")) {
                    chatsId.add(message.getChatId());
                }
                if (chatsId.contains(message.getChatId())) {
                    sendMessage(message.getChatId().toString(), manager.getStatistic());
                } else {
                    sendMessage(message.getChatId().toString(), "Я вас не знаю");
                }
            } else if (message.getText().equals("/bye")) {
                chatsId.remove(message.getChatId());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "GeoAnalyzerBot";
    }

    @Override
    public String getBotToken() {
        return "208551717:AAEYZHlMhgY-skduW-R4eP_S9wk9b1kCst0";
    }

    private void sendMessage(String chatId, String text) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(chatId);
        sendMessageRequest.setText(text);
        try {
            sendMessageAsync(sendMessageRequest, new SentCallback<Message>() {
                @Override
                public void onResult(BotApiMethod<Message> botApiMethod, JSONObject jsonObject) {
                }

                @Override
                public void onError(BotApiMethod<Message> botApiMethod, JSONObject jsonObject) {
                    logger.error("onError");
                }

                @Override
                public void onException(BotApiMethod<Message> botApiMethod, Exception e) {
                    logger.error("onException");
                }
            });
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendMessage(String text) {
        for (Long chatId: chatsId) {
            logger.info("Send to " + chatId.toString());
            sendMessage(chatId.toString(), text);
        }
    }
}
