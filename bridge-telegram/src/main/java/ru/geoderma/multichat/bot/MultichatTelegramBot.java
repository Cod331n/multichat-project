package ru.geoderma.multichat.bot;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.geoderma.multichat.config.BotConfig;

public abstract class MultichatTelegramBot extends TelegramLongPollingBot {

    public MultichatTelegramBot() {
        super(BotConfig.getBotToken());
    }

    public abstract void onStart();

    public abstract void onStop();

    @Override
    @NotNull
    public String getBotUsername() {
        return BotConfig.getBotUsername();
    }

}
