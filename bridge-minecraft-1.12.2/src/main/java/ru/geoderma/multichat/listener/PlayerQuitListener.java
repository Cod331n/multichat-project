package ru.geoderma.multichat.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.cristalix.core.permissions.IPermissionService;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;
import org.bukkit.entity.Player;
import ru.geoderma.multichat.core.model.Source;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {

    private final KafkaMessageProducer producer;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.player;

        IPermissionService.get().getBestGroup(player.getUniqueId()).thenAccept(group -> {
            String rawMessage = String.format("\uD83D\uDCD7 Игрок %s | %s вышел с билдсервера",
                    group.getTabPrefix(),
                    player.getName());

            String minecraftFormatted = String.format("Игрок %s%s §8| %s%s §fвышел с билдсервера",
                    group.getPrefixColor(),
                    group.getTabPrefix(),
                    group.getNameColor(),
                    player.getName());

            Map<String, String> meta = new HashMap<>();
            meta.put("minecraft_message", minecraftFormatted);
            meta.put("sent_from", Source.MINECRAFT_1_12_2.getVersion());

            producer.sendDirectMessage(Source.MINECRAFT_1_12_2, Source.TELEGRAM, "", rawMessage, meta);
        }).exceptionally(e -> {
            String rawMessage = String.format("\uD83D\uDCD7 Игрок %s вышел с билдсервера", player.getName());
            String minecraftFormatted = String.format("Игрок §e%s §fвышел с билдсервера", player.getName());

            Map<String, String> meta = new HashMap<>();
            meta.put("minecraft_message", minecraftFormatted);
            meta.put("sent_from", Source.MINECRAFT_1_12_2.getVersion());

            producer.sendDirectMessage(Source.MINECRAFT_1_12_2, Source.TELEGRAM, "", rawMessage, meta);

            return null;
        });
    }

}