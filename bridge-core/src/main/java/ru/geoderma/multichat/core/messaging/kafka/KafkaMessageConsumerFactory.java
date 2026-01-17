package ru.geoderma.multichat.core.messaging.kafka;

import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.core.model.BridgeMessage;
import ru.geoderma.multichat.core.model.Source;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static ru.geoderma.multichat.core.messaging.kafka.IdGenerator.generateClientId;
import static ru.geoderma.multichat.core.messaging.kafka.IdGenerator.generateGroupId;

public final class KafkaMessageConsumerFactory {

    private KafkaMessageConsumerFactory() {
    }

    @NotNull
    public static KafkaMessageConsumer create(@NotNull Source source,
                                              @NotNull String bootstrapServers,
                                              @NotNull Consumer<BridgeMessage> handler) {
        KafkaMessageConsumer consumer = new KafkaMessageConsumer(
                bootstrapServers,
                generateGroupId(source),
                generateClientId(source),
                getTopicsForSource(source)
        );

        consumer.setMessageHandler(msg -> {
            if (msg.getSource() != source) {
                handler.accept(msg);
            }
        });

        return consumer;
    }

    @NotNull
    private static List<String> getTopicsForSource(@NotNull Source source) {
        switch (source) {
            case MINECRAFT_1_12_2:
                return Arrays.asList(
                        Source.TELEGRAM.getTopicName(),
                        Source.SYSTEM.getTopicName(),
                        Source.MINECRAFT_1_12_2.getPrivateTopic(),
                        Source.MINECRAFT_1_20_5.getTopicName()
                );
            case MINECRAFT_1_20_5:
                return Arrays.asList(
                        Source.TELEGRAM.getTopicName(),
                        Source.SYSTEM.getTopicName(),
                        Source.MINECRAFT_1_12_2.getTopicName(),
                        Source.MINECRAFT_1_20_5.getPrivateTopic()
                );
            case TELEGRAM:
                return Arrays.asList(
                        Source.TELEGRAM.getPrivateTopic(),
                        Source.SYSTEM.getTopicName(),
                        Source.MINECRAFT_1_12_2.getTopicName(),
                        Source.MINECRAFT_1_20_5.getTopicName()
                );
            case SYSTEM:
                return Arrays.asList(
                        Source.MINECRAFT_1_12_2.getTopicName(),
                        Source.MINECRAFT_1_20_5.getTopicName(),
                        Source.TELEGRAM.getTopicName()
                );
            default:
                return Collections.emptyList();
        }
    }

}
