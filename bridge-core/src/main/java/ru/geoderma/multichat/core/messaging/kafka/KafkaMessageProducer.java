package ru.geoderma.multichat.core.messaging.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.geoderma.multichat.core.model.BridgeMessage;
import ru.geoderma.multichat.core.model.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaMessageProducer {

    private final Producer<String, BridgeMessage> producer;

    public KafkaMessageProducer(@NotNull String bootstrapServers,
                                @NotNull String clientId) {
        Properties props = KafkaStaticConfig.getProducerProperties(bootstrapServers, clientId);

        this.producer = new KafkaProducer<>(props);
    }

    @NotNull
    public CompletableFuture<Void> sendMessage(@NotNull BridgeMessage message) {
        return CompletableFuture.runAsync(() -> {
            sendMessageDirect(message, message.getSource().getTopicName());
        });
    }

    @NotNull
    public CompletableFuture<Void> sendFromMinecraft1_12_2(@NotNull String player,
                                                           @NotNull String message,
                                                           @Nullable Map<String, String> meta) {
        BridgeMessage bridgeMessage = BridgeMessage.builder()
                .source(Source.MINECRAFT_1_12_2)
                .author(player)
                .content(message)
                .metadata(meta == null
                        ? new HashMap<>()
                        : meta)
                .build();

        return sendMessage(bridgeMessage);
    }

    @NotNull
    public CompletableFuture<Void> sendFromMinecraft1_20_5(@NotNull String player,
                                                           @NotNull String message,
                                                           @Nullable Map<String, String> meta) {
        BridgeMessage bridgeMessage = BridgeMessage.builder()
                .source(Source.MINECRAFT_1_20_5)
                .author(player)
                .content(message)
                .metadata(meta == null
                        ? new HashMap<>()
                        : meta)
                .build();

        return sendMessage(bridgeMessage);
    }

    @NotNull
    public CompletableFuture<Void> sendFromTelegram(@NotNull String user,
                                                    @NotNull String message,
                                                    @Nullable Map<String, String> meta) {
        BridgeMessage bridgeMessage = BridgeMessage.builder()
                .source(Source.TELEGRAM)
                .author(user)
                .content(message)
                .metadata(meta == null
                        ? new HashMap<>()
                        : meta)
                .build();

        return sendMessage(bridgeMessage);
    }

    @NotNull
    public CompletableFuture<Void> sendSystem(@NotNull String message,
                                              @Nullable Map<String, String> meta) {
        BridgeMessage bridgeMessage = BridgeMessage.builder()
                .source(Source.SYSTEM)
                .content(message)
                .metadata(meta == null
                        ? new HashMap<>()
                        : meta)
                .build();

        return sendMessage(bridgeMessage);
    }

    @NotNull
    private Future<RecordMetadata> sendMessageDirect(@NotNull BridgeMessage message, @NotNull String topic) {
        return producer.send(new ProducerRecord<>(topic, message.getMessageId(), message));
    }

}