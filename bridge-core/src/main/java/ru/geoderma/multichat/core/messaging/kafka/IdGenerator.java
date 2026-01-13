package ru.geoderma.multichat.core.messaging.kafka;

import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.core.model.Source;

import java.util.UUID;

public interface IdGenerator {

    @NotNull
    static String generateClientId(@NotNull Source source) {
        return source.getTopicName() + "-client-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @NotNull
    static String generateGroupId(@NotNull Source source) {
        return source.getTopicName() + "-group-" + UUID.randomUUID().toString().substring(0, 8);
    }

}
