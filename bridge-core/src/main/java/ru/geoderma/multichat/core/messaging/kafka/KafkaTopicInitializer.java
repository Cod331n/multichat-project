package ru.geoderma.multichat.core.messaging.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.core.model.Source;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public final class KafkaTopicInitializer {

    private KafkaTopicInitializer() {
    }

    public static void init(@NotNull String bootstrapServers) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000);

        try (AdminClient adminClient = AdminClient.create(props)) {
            Set<String> existingTopics = adminClient.listTopics().names().get();

            List<NewTopic> topics = Arrays.stream(Source.values())
                    .map(Source::getTopicName)
                    .filter(topic -> !existingTopics.contains(topic))
                    .map(topic -> new NewTopic(topic, 3, (short) 1))
                    .collect(Collectors.toList());

            topics.addAll(Arrays.stream(Source.values())
                    .filter(Source::isHasPrivateTopic)
                    .map(Source::getPrivateTopic)
                    .filter(topic -> !existingTopics.contains(topic))
                    .map(topic -> new NewTopic(topic, 1, (short) 1))
                    .collect(Collectors.toList()));

            adminClient.createTopics(topics);

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
