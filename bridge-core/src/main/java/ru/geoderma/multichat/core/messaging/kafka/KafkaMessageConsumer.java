package ru.geoderma.multichat.core.messaging.kafka;

import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.core.model.BridgeMessage;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaMessageConsumer {

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final Consumer<String, BridgeMessage> consumer;

    private final Thread consumerThread;

    private final List<String> topics;

    @Setter
    private java.util.function.Consumer<BridgeMessage> messageHandler;

    public KafkaMessageConsumer(@NotNull String bootstrapServers,
                                @NotNull String groupId,
                                @NotNull String clientId,
                                @NotNull List<String> topics) {
        Properties props = KafkaStaticConfig.getConsumerProperties(bootstrapServers, groupId, clientId);
        this.consumer = new KafkaConsumer<>(props);
        this.consumerThread = new Thread(this::runConsumer, "kafka-consumer-" + clientId);
        this.topics = topics;

        consumer.subscribe(topics);
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            consumer.subscribe(topics);
            consumerThread.start();
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            try {
                consumerThread.join(4000);
                consumer.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void runConsumer() {
        try {
            while (running.get()) {
                ConsumerRecords<String, BridgeMessage> records = consumer.poll(Duration.ofMillis(100));

                records.forEach(record -> {
                    BridgeMessage message = record.value();
                    if (message != null && messageHandler != null) {
                        messageHandler.accept(message);
                    }
                });
            }
        } finally {
            consumer.close();
        }
    }

}