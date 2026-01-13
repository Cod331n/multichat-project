package ru.geoderma.multichat.core.messaging.kafka;

import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.core.model.Source;

import static ru.geoderma.multichat.core.messaging.kafka.IdGenerator.generateClientId;

public final class KafkaMessageProducerFactory {

    private KafkaMessageProducerFactory() {
    }

    @NotNull
    public static KafkaMessageProducer forMinecraft1_12_2(@NotNull String bootstrapServers) {
        return new KafkaMessageProducer(
                bootstrapServers,
                generateClientId(Source.MINECRAFT_1_12_2)
        );
    }

    @NotNull
    public static KafkaMessageProducer forMinecraft1_20_5(@NotNull String bootstrapServers) {
        return new KafkaMessageProducer(
                bootstrapServers,
                generateClientId(Source.MINECRAFT_1_20_5)
        );
    }

    @NotNull
    public static KafkaMessageProducer forTelegram(@NotNull String bootstrapServers) {
        return new KafkaMessageProducer(
                bootstrapServers,
                generateClientId(Source.TELEGRAM)
        );
    }

}
