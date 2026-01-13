package ru.geoderma.multichat.core.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.geoderma.multichat.core.model.BridgeMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class MessageDeserializer implements Deserializer<BridgeMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BridgeMessage deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), BridgeMessage.class);
        } catch (IOException e) {
            throw new SerializationException("Error while deserializing message:", e);
        }
    }

}