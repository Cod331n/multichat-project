package ru.geoderma.multichat.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.geoderma.multichat.LuckPermsUtils;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class PlayerChatListener implements Listener {

    private final KafkaMessageProducer producer;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        Map<String, String> meta = getMeta(player);
        producer.sendFromMinecraft1_20_5(player.getName(), message, meta);
    }

    private Map<String, String> getMeta(Player player) {
        Map<String, String> meta = new HashMap<>();

        String prefix = LuckPermsUtils.getPlayerPrefix(player);
        String primaryGroup = LuckPermsUtils.getPrimaryGroup(player);

        String worldName = player.getWorld()
                .getName()
                .replaceFirst("maps/", "");

        if (worldName.length() > 10) {
            worldName = worldName.substring(0, 10) + "...";
        }

        meta.put("world_name", worldName);
        meta.put("group_prefix", prefix == null
                ? ""
                : prefix);
        meta.put("primary_group", primaryGroup == null
                ? ""
                : primaryGroup);

        return meta;
    }

}