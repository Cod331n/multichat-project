package ru.geoderma.multichat.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.cristalix.core.permissions.IPermissionService;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;
import ru.geoderma.multichat.core.model.Source;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final KafkaMessageProducer producer;

    @EventHandler
    public void onPlayerJoinListener(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.player;

        IPermissionService.get().getBestGroup(player.getUniqueId()).thenAccept(group -> {
            String rawMessage = String.format("\uD83D\uDCD7 %s | %s зашёл на билдсервер",
                    group.getTabPrefix(),
                    player.getName());

            String minecraftFormatted = String.format("%s%s §8| %s%s §fзашёл на билдсервер",
                    group.getPrefixColor(),
                    group.getTabPrefix(),
                    group.getNameColor(),
                    player.getName());

            Map<String, String> meta = new HashMap<>();
            meta.put("minecraft_message", minecraftFormatted);
            meta.put("sent_from", Source.MINECRAFT_1_12_2.getVersion());

            producer.sendSystem(rawMessage, meta);
        }).exceptionally(ex -> {
            String rawMessage = String.format("\uD83D\uDCD7 %s зашёл на билдсервер", player.getName());
            String minecraftFormatted = String.format("§e%s §fзашёл на билдсервер", player.getName());

            Map<String, String> meta = new HashMap<>();
            meta.put("minecraft_message", minecraftFormatted);
            meta.put("sent_from", Source.MINECRAFT_1_12_2.getVersion());

            producer.sendSystem(rawMessage, meta);

            return null;
        });
    }

}
