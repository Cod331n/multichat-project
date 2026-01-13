package ru.geoderma.multichat.core.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.geoderma.multichat.core.model.BridgeMessage;

import java.nio.charset.StandardCharsets;

public final class MessageSerializer implements Serializer<BridgeMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, BridgeMessage message) {
        if (message == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error while serializing message: ", e);
        }
    }

}