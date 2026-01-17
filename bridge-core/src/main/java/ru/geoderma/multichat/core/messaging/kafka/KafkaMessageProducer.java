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
        return sendMessageToTopic(message, message.getSource().getTopicName());
    }

    @NotNull
    public CompletableFuture<Void> sendMessageToTopic(@NotNull BridgeMessage message, @NotNull String topic) {
        return CompletableFuture.runAsync(() -> sendMessageDirect(message, topic));
    }

    @NotNull
    public CompletableFuture<Void> sendDirectMessage(@NotNull Source from,
                                                     @NotNull Source to,
                                                     @NotNull String player,
                                                     @NotNull String message,
                                                     @Nullable Map<String, String> meta) {
        BridgeMessage bridgeMessage = createMessage(from, player, message, meta);
        bridgeMessage.setPrivate(true);

        return sendMessageToTopic(bridgeMessage, to.getPrivateTopic());
    }

    @NotNull
    public CompletableFuture<Void> sendFromSource(@NotNull Source source,
                                                  @NotNull String author,
                                                  @NotNull String message,
                                                  @Nullable Map<String, String> meta) {
        BridgeMessage bridgeMessage = createMessage(source, author, message, meta);
        return sendMessage(bridgeMessage);
    }

    @NotNull
    public CompletableFuture<Void> sendFromMinecraft1_12_2(@NotNull String player,
                                                           @NotNull String message,
                                                           @Nullable Map<String, String> meta) {
        return sendFromSource(Source.MINECRAFT_1_12_2, player, message, meta);
    }

    @NotNull
    public CompletableFuture<Void> sendFromMinecraft1_20_5(@NotNull String player,
                                                           @NotNull String message,
                                                           @Nullable Map<String, String> meta) {
        return sendFromSource(Source.MINECRAFT_1_20_5, player, message, meta);
    }

    @NotNull
    public CompletableFuture<Void> sendFromTelegram(@NotNull String user,
                                                    @NotNull String message,
                                                    @Nullable Map<String, String> meta) {
        return sendFromSource(Source.TELEGRAM, user, message, meta);
    }

    @NotNull
    public CompletableFuture<Void> sendSystem(@NotNull String message,
                                              @Nullable Map<String, String> meta) {
        BridgeMessage bridgeMessage = BridgeMessage.builder()
                .source(Source.SYSTEM)
                .content(message)
                .metadata(createSafeMetadata(meta))
                .build();
        return sendMessage(bridgeMessage);
    }

    @NotNull
    private BridgeMessage createMessage(@NotNull Source source,
                                        @NotNull String author,
                                        @NotNull String content,
                                        @Nullable Map<String, String> meta) {
        return BridgeMessage.builder()
                .source(source)
                .author(author)
                .content(content)
                .metadata(createSafeMetadata(meta))
                .build();
    }

    @NotNull
    private Map<String, String> createSafeMetadata(@Nullable Map<String, String> meta) {
        return meta == null
                ? new HashMap<>()
                : new HashMap<>(meta);
    }

    @NotNull
    private Future<RecordMetadata> sendMessageDirect(@NotNull BridgeMessage message, @NotNull String topic) {
        ProducerRecord<String, BridgeMessage> record = new ProducerRecord<>(topic, message.getMessageId(), message);
        return producer.send(record);
    }

}