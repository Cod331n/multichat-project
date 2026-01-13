package ru.geoderma.multichat;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.plugin.java.annotation.plugin.author.Authors;
import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.config.ConfigManager;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageConsumer;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageConsumerFactory;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducerFactory;
import ru.geoderma.multichat.core.model.BridgeMessage;
import ru.geoderma.multichat.listener.PlayerChatListener;
import ru.geoderma.multichat.listener.PlayerJoinListener;
import ru.geoderma.multichat.listener.PlayerQuitListener;

@Getter
@Plugin(name = "Multichat", version = "1.0.0")
@Authors({@Author("GeoDerma")})
@SoftDependency("LuckPerms")
public class MultichatPlugin extends JavaPlugin {

    private KafkaMessageProducer producer;

    private KafkaMessageConsumer consumer;

    private ConfigManager configManager;

    private boolean luckPermsEnabled = false;

    @Override
    public void onEnable() {
        luckPermsEnabled = LuckPermsUtils.initialize();

        if (!luckPermsEnabled) {
            getLogger().warning("LuckPerms не найден. Функции форматирования чата будут ограничены.");
        }

        configManager = new ConfigManager(this);

        this.producer = KafkaMessageProducerFactory.forMinecraft1_20_5(configManager.getKafkaBootstrapServers());
        this.consumer = KafkaMessageConsumerFactory.forMinecraft1_20_5(configManager.getKafkaBootstrapServers(), this::handleIncomingMessage);
        consumer.start();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerChatListener(producer), this);
        pluginManager.registerEvents(new PlayerJoinListener(producer), this);
        pluginManager.registerEvents(new PlayerQuitListener(producer), this);

        producer.sendSystem("\uD83D\uDD0B Билдсервер запущен!",
                null);
    }

    @Override
    public void onDisable() {
        producer.sendSystem(
                "\uD83E\uDEAB Билдсервер выключен!",
                null);

        consumer.stop();
    }

    public void registerCommand(@NotNull String command, @NotNull CommandExecutor executor) {
        getServer().getPluginCommand(command).setExecutor(executor);
    }

    private void handleIncomingMessage(@NotNull BridgeMessage message) {
        if (message.getContent() == null) {
            return;
        }

        String formatted;

        switch (message.getSource()) {
            case MINECRAFT_1_12_2 -> formatted = String.format("%s%s §8| %s%s §8(§6%s§8) §8» §f%s",
                    message.getMetadata().get("group_color"),
                    message.getMetadata().get("group_prefix"),
                    message.getMetadata().get("name_color"),
                    message.getAuthor(),
                    message.getMetadata().get("world_name"),
                    message.getContent());

            case TELEGRAM -> formatted = String.format("§7TG §f%s §8» §f%s",
                    message.getAuthor(),
                    message.getContent());

            case null, default -> formatted = message.getMetadata().containsKey("minecraft_message")
                    ? message.getMetadata().get("minecraft_message")
                    : message.getContent();
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', formatted));
        }
    }

}