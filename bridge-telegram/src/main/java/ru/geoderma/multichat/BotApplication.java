package ru.geoderma.multichat;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.geoderma.multichat.bot.MultichatTelegramBot;
import ru.geoderma.multichat.bot.TelegramBot;

public class BotApplication {

    public static void main(String... arguments) throws TelegramApiException {
        System.out.println("Telegram bot started turning on");

        try {
            MultichatTelegramBot bot = new TelegramBot();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            bot.onStart();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down bot...");
                bot.onStop();
            }));
        } catch (Exception e) {
            System.out.println("Telegram bot could not be activated due to a sudden error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        System.out.println("Telegram bot has loaded successfully");
    }

}