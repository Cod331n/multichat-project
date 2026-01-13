package ru.geoderma.multichat.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.geoderma.multichat.LuckPermsUtils;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;
import ru.geoderma.multichat.core.model.Source;

import java.util.Map;

@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {

    private final KafkaMessageProducer producer;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        String prefix = LuckPermsUtils.getPlayerPrefix(player);

        String rawMessage;
        String minecraftFormatted;

        if (LuckPermsUtils.isEnabled()) {
            rawMessage = String.format("\uD83D\uDCD7 %s | %s вышел с билдсервера",
                    prefix,
                    player.getName());

            minecraftFormatted = String.format("§3%s §8| §7%s §fвышел с билдсервера",
                    prefix,
                    player.getName());

        } else {
            rawMessage = String.format("\uD83D\uDCD7 %s вышел с билдсервера",
                    player.getName());

            minecraftFormatted = String.format("§7%s §fвышел с билдсервера",
                    player.getName());
        }

        producer.sendSystem(rawMessage, Map.of("minecraft_message", minecraftFormatted,
                "sent_from", Source.MINECRAFT_1_20_5.getVersion()));
    }

}