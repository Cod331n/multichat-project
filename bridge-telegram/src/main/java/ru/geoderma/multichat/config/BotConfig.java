package ru.geoderma.multichat.config;

import org.jetbrains.annotations.NotNull;

public interface BotConfig {

    @NotNull
    static String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @NotNull
    static String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    @NotNull
    static String getChatId() {
        return System.getenv("BOT_CHAT_ID");
    }

    @NotNull
    static String getThreadFor1_12_2() {
        return System.getenv("BOT_CHAT_THREAD_1_12_2");
    }

    @NotNull
    static String getThreadFor1_20_5() {
        return System.getenv("BOT_CHAT_THREAD_1_20_5");
    }

    @NotNull
    static String getKafkaBootstrapServers() {
        return System.getenv("KAFKA_BOOTSTRAP_SERVERS");
    }

    @NotNull
    static String getEnv(@NotNull String env) {
        return System.getenv(env);
    }

}