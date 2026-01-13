package ru.geoderma.multichat.bot;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.geoderma.multichat.config.BotConfig;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageConsumer;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageConsumerFactory;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducerFactory;
import ru.geoderma.multichat.core.model.BridgeMessage;
import ru.geoderma.multichat.core.model.Source;

import java.util.*;
import java.util.function.Predicate;

public class TelegramBot extends MultichatTelegramBot {

    private final KafkaMessageProducer kafkaProducer;

    private final KafkaMessageConsumer kafkaConsumer;

    private final Predicate<Update> isText = update -> update.hasMessage() && update.getMessage().hasText();

    private final Predicate<String> isGlobalMessage = msg -> !msg.startsWith("!");

    private final List<Integer> telegramThreads = List.of(
            Integer.parseInt(BotConfig.getThreadFor1_12_2()),
            Integer.parseInt(BotConfig.getThreadFor1_20_5())
    );

    public TelegramBot() {
        super();

        String bootstrapServers = BotConfig.getKafkaBootstrapServers();

        this.kafkaProducer = KafkaMessageProducerFactory.forTelegram(bootstrapServers);
        this.kafkaConsumer = KafkaMessageConsumerFactory.forTelegram(bootstrapServers, this::handleIncomingMessage);
    }

    @Override
    public void onStart() {
        kafkaConsumer.start();

        telegramThreads.forEach(thread -> {
            sendMessage("\uD83D\uDD0B Телеграм бот включен!", thread);
        });
    }

    @Override
    public void onStop() {
        this.kafkaConsumer.stop();

        telegramThreads.forEach(thread -> {
            sendMessage("\uD83E\uDEAB Телеграм бот выключен!", thread);
        });
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (isText.test(update)) {
            String messageText = message.getText();

            if (isGlobalMessage.test(messageText)) {
                String userName = message.getFrom().getUserName();

                kafkaProducer.sendFromTelegram("@" + userName, messageText, null);
            }
        }
    }

    private void handleIncomingMessage(@NotNull BridgeMessage message) {
        if (message.getSource() == null || message.getContent() == null) {
            return;
        }

        String formatted;

        switch (message.getSource()) {
            case MINECRAFT_1_12_2 -> formatted = String.format("%s | %s (%s) » %s",
                    message.getMetadata().get("group_prefix"),
                    message.getAuthor(),
                    message.getMetadata().get("world_name"),
                    message.getContent());

            case MINECRAFT_1_20_5 -> formatted = String.format("%s%s (%s) » %s",
                    message.getMetadata().get("group_prefix").isBlank()
                            ? ""
                            : message.getMetadata().get("group_prefix") + " | ",
                    message.getAuthor(),
                    message.getMetadata().get("world_name"),
                    message.getContent());

            default -> formatted = message.getContent();
        }

        identifyThreads(message).forEach(thread -> {
            sendMessage(formatted, thread);
        });
    }

    private void sendMessage(@NotNull String text, int thread) {
        if (text.trim().isEmpty()) {
            return;
        }

        SendMessage message = new SendMessage();
        message.setChatId(BotConfig.getChatId());
        message.setMessageThreadId(thread);
        message.setText(text);
        message.enableHtml(false);
        message.disableWebPagePreview();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send message to Telegram: " + e.getMessage());
        }
    }

    private List<Integer> identifyThreads(@NotNull BridgeMessage message) {
        List<Integer> threads = new ArrayList<>();
        Source source = message.getSource();

        switch (source) {
            case MINECRAFT_1_12_2, MINECRAFT_1_20_5 ->
                    threads.add(Integer.parseInt(BotConfig.getEnv("BOT_CHAT_THREAD_" + source.getVersion())));

            case SYSTEM -> {
                if (message.getMetadata().containsKey("sent_from")) {
                    threads.add(Integer.parseInt(BotConfig.getEnv("BOT_CHAT_THREAD_" + message.getMetadata().get("sent_from"))));
                } else {
                    threads.addAll(telegramThreads);
                }
            }

            default -> throw new RuntimeException("Couldn't identify thread");
        }

        return threads;
    }

}