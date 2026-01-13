package ru.geoderma.multichat.core.messaging.kafka;

import lombok.SneakyThrows;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public final class KafkaStaticConfig {

    private KafkaStaticConfig() {
    }

    @SneakyThrows
    @NotNull
    private static Class<?> loadClass(@NotNull String className) {
        try {
            return Class.forName(className, true, KafkaStaticConfig.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + className, e);
        }
    }

    @NotNull
    public static Properties getProducerProperties(@NotNull String bootstrapServers,
                                                   @NotNull String clientId) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                loadClass("org.apache.kafka.common.serialization.StringSerializer"));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                loadClass("ru.geoderma.multichat.core.messaging.MessageSerializer"));
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);

        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");

        return props;
    }

    @NotNull
    public static Properties getConsumerProperties(@NotNull String bootstrapServers,
                                                   @NotNull String groupId,
                                                   @NotNull String clientId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                loadClass("org.apache.kafka.common.serialization.StringDeserializer"));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                loadClass("ru.geoderma.multichat.core.messaging.MessageDeserializer"));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");

        return props;
    }

}