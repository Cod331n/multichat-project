package ru.geoderma.multichat.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public enum Source {

    MINECRAFT_1_12_2("minecraft-1-12-2", "1_12_2"),

    MINECRAFT_1_20_5("minecraft-1-20-5", "1_20_5"),

    TELEGRAM("telegram", null),

    SYSTEM("system", null);

    @Getter
    @NotNull
    private final String topicName;

    @Getter
    @Nullable
    private final String version;

}