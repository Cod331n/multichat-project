package ru.geoderma.multichat.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public enum Source {

    MINECRAFT_1_12_2("minecraft-1-12-2", "1_12_2", true),

    MINECRAFT_1_20_5("minecraft-1-20-5", "1_20_5", true),

    TELEGRAM("telegram", null, true),

    SYSTEM("system", null, false);

    @Getter
    @NotNull
    private final String topicName;

    @Getter
    @Nullable
    private final String version;

    @Getter
    private final boolean hasPrivateTopic;

    @NotNull
    public String getPrivateTopic() {
        return topicName + "-private";
    }

}