package com.kotah;

import com.kotah.botlogic.GameBot;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        GameBot gameBot = new GameBot(new DefaultBotOptions());
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(gameBot);
        } catch (TelegramApiException ex) {
            ex.getStackTrace();
        }
        try {
            gameBot.execute(SendMessage.builder().chatId("489777841").text("I'm awaken!").build());
        } catch (TelegramApiException ex) {
            System.out.println(ex.getMessage());
        }
    }
}