package ru.geoderma.multichat.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.cristalix.core.CoreApi;
import ru.cristalix.core.permissions.IPermissionService;
import ru.cristalix.core.permissions.PermissionService;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerChatListener implements Listener {

    private final KafkaMessageProducer producer;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        String message = event.getMessage();
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        IPermissionService.get().getBestGroup(playerUuid).thenAccept(group -> {
            String worldName = player.getWorld()
                    .getName()
                    .replaceFirst("maps/", "");

            if (worldName.length() > 10) {
                worldName = worldName.substring(0, 10) + "...";
            }

            Map<String, String> meta = new HashMap<>();
            meta.put("world_name", worldName);
            meta.put("group_prefix", group.getTabPrefix());
            meta.put("group_color", group.getTabPrefixColor());
            meta.put("name_color", group.getNameColor());

            producer.sendFromMinecraft1_12_2(player.getName(), message, meta);
        });
    }

}
