package ru.geoderma.multichat.core.messaging.kafka;

import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.core.model.Source;

public final class KafkaMessageProducerFactory {

    private KafkaMessageProducerFactory() {
    }

    @NotNull
    public static KafkaMessageProducer create(@NotNull Source source,
                                              @NotNull String bootstrapServers) {
        return new KafkaMessageProducer(
                bootstrapServers,
                IdGenerator.generateClientId(source)
        );
    }

}
