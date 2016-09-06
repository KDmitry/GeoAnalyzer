package edu.dmitry.geoserver.telegram;

import edu.dmitry.geoserver.Manager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;

public class TelegramBotApi {
    private Logger logger = LogManager.getRootLogger();
    private TelegramBotsApi telegramBotsApi;
    private GeoAnalyzerBot geoAnalyzerBot;

    public TelegramBotApi(Manager manager) {
        telegramBotsApi = new TelegramBotsApi();
        geoAnalyzerBot = new GeoAnalyzerBot(manager);
        try {
            telegramBotsApi.registerBot(geoAnalyzerBot);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendMessage(String text) {
        geoAnalyzerBot.sendMessage(text);
    }
}
