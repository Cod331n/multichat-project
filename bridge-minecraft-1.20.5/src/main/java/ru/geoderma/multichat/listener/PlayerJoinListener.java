package ru.geoderma.multichat.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.geoderma.multichat.LuckPermsUtils;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;
import ru.geoderma.multichat.core.model.Source;

import java.util.Map;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final KafkaMessageProducer producer;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        String prefix = LuckPermsUtils.getPlayerPrefix(player);

        String rawMessage;
        String minecraftFormatted;

        if (LuckPermsUtils.isEnabled()) {
            rawMessage = String.format("\uD83D\uDCD7 %s | %s зашёл на билдсервер",
                    prefix,
                    player.getName());

            minecraftFormatted = String.format("§3%s §8| §7%s §fзашёл на билдсервер",
                    prefix,
                    player.getName());

        } else {
            rawMessage = String.format("\uD83D\uDCD7 %s зашёл на билдсервер",
                    player.getName());

            minecraftFormatted = String.format("§7%s §fзашёл на билдсервер",
                    player.getName());

        }

        producer.sendDirectMessage(Source.MINECRAFT_1_20_5, Source.TELEGRAM, "", rawMessage, null);
    }

}