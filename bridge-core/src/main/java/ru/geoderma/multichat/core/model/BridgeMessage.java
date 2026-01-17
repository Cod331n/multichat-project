package ru.geoderma.multichat.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public final class BridgeMessage {

    @Builder.Default
    @JsonProperty("messageId")
    private String messageId = UUID.randomUUID().toString();

    @JsonProperty("source")
    private Source source;

    @Builder.Default
    @JsonProperty("isPrivate")
    private boolean isPrivate = false;

    @JsonProperty("author")
    private String author;

    @JsonProperty("content")
    private String content;

    @JsonProperty("metadata")
    private Map<String, String> metadata;

    @Builder.Default
    @JsonProperty("timestamp")
    private long timestamp = Instant.now().toEpochMilli();

}