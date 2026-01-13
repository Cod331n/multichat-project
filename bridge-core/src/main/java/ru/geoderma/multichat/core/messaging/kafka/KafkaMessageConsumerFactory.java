package ru.geoderma.multichat.core.messaging.kafka;

import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.core.model.BridgeMessage;
import ru.geoderma.multichat.core.model.Source;

import java.util.Arrays;
import java.util.function.Consumer;

import static ru.geoderma.multichat.core.messaging.kafka.IdGenerator.generateClientId;
import static ru.geoderma.multichat.core.messaging.kafka.IdGenerator.generateGroupId;

public final class KafkaMessageConsumerFactory {

    private KafkaMessageConsumerFactory() {
    }

    @NotNull
    public static KafkaMessageConsumer forMinecraft1_12_2(@NotNull String bootstrapServers,
                                                          @NotNull Consumer<BridgeMessage> handler) {
        KafkaMessageConsumer consumer = new KafkaMessageConsumer(
                bootstrapServers,
                generateGroupId(Source.MINECRAFT_1_12_2),
                generateClientId(Source.MINECRAFT_1_12_2),
                Arrays.asList(
                        Source.SYSTEM.getTopicName(),
                        Source.TELEGRAM.getTopicName(),
                        Source.MINECRAFT_1_20_5.getTopicName()
                )
        );
        consumer.setMessageHandler(msg -> {
            if (msg.getSource() != Source.MINECRAFT_1_12_2) {
                handler.accept(msg);
            }
        });

        return consumer;
    }

    @NotNull
    public static KafkaMessageConsumer forMinecraft1_20_5(@NotNull String bootstrapServers,
                                                          @NotNull Consumer<BridgeMessage> handler) {
        KafkaMessageConsumer consumer = new KafkaMessageConsumer(
                bootstrapServers,
                generateGroupId(Source.MINECRAFT_1_20_5),
                generateClientId(Source.MINECRAFT_1_20_5),
                Arrays.asList(
                        Source.SYSTEM.getTopicName(),
                        Source.TELEGRAM.getTopicName(),
                        Source.MINECRAFT_1_12_2.getTopicName()
                )
        );
        consumer.setMessageHandler(msg -> {
            if (msg.getSource() != Source.MINECRAFT_1_20_5) {
                handler.accept(msg);
            }
        });

        return consumer;
    }

    @NotNull
    public static KafkaMessageConsumer forTelegram(@NotNull String bootstrapServers,
                                                   @NotNull Consumer<BridgeMessage> handler) {
        KafkaMessageConsumer consumer = new KafkaMessageConsumer(
                bootstrapServers,
                generateGroupId(Source.TELEGRAM),
                generateClientId(Source.TELEGRAM),
                Arrays.asList(
                        Source.SYSTEM.getTopicName(),
                        Source.MINECRAFT_1_12_2.getTopicName(),
                        Source.MINECRAFT_1_20_5.getTopicName()
                )
        );
        consumer.setMessageHandler(msg -> {
            if (msg.getSource() != Source.TELEGRAM) {
                handler.accept(msg);
            }
        });

        return consumer;
    }

}
