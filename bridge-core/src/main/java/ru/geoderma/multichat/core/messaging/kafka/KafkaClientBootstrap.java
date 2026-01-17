package ru.geoderma.multichat.core.messaging.kafka;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.core.model.BridgeMessage;
import ru.geoderma.multichat.core.model.Source;

import java.util.function.Consumer;

public final class KafkaClientBootstrap {

    private KafkaClientBootstrap() {
    }

    @NotNull
    public static BootstrapResponse bootstrap(@NotNull Source source,
                                              @NotNull String bootstrapServers,
                                              @NotNull Consumer<BridgeMessage> handler) {
        KafkaTopicInitializer.init(bootstrapServers);

        BootstrapResponse bootstrapResponse = BootstrapResponse.builder()
                .producer(KafkaMessageProducerFactory.create(source, bootstrapServers))
                .consumer(KafkaMessageConsumerFactory.create(source, bootstrapServers, handler))
                .build();

        bootstrapResponse.getConsumer().start();

        return bootstrapResponse;
    }

    @Data
    @Builder
    public static class BootstrapResponse {

        private final KafkaMessageProducer producer;

        private final KafkaMessageConsumer consumer;

    }

}
