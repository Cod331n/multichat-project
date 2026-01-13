package ru.geoderma.multichat;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.plugin.java.annotation.plugin.author.Authors;
import org.jetbrains.annotations.NotNull;
import ru.geoderma.multichat.command.ChatsCommand;
import ru.geoderma.multichat.config.ConfigManager;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageConsumer;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageConsumerFactory;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducer;
import ru.geoderma.multichat.core.messaging.kafka.KafkaMessageProducerFactory;
import ru.geoderma.multichat.core.model.BridgeMessage;
import ru.geoderma.multichat.listener.PlayerChatListener;
import ru.geoderma.multichat.listener.PlayerJoinListener;
import ru.geoderma.multichat.listener.PlayerQuitListener;

import java.io.File;

@Getter
@Plugin(name = "Multichat", version = "1.0.0")
@Authors({@Author("GeoDerma")})
public class MultichatPlugin extends JavaPlugin {

    private KafkaMessageProducer producer;

    private KafkaMessageConsumer consumer;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);

        this.producer = KafkaMessageProducerFactory.forMinecraft1_12_2(configManager.getKafkaBootstrapServers());
        this.consumer = KafkaMessageConsumerFactory.forMinecraft1_12_2(configManager.getKafkaBootstrapServers(), this::handleIncomingMessage);
        consumer.start();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerChatListener(producer), this);
        pluginManager.registerEvents(new PlayerJoinListener(producer), this);
        pluginManager.registerEvents(new PlayerQuitListener(producer), this);

        registerCommand("chats", new ChatsCommand(this));

        producer.sendSystem("\uD83E\uDEAB Билдсервер запущен!", null);
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
            case MINECRAFT_1_20_5:
                String groupPrefix = message.getMetadata().get("group_prefix");
                formatted = String.format("%s%s §8(§6%s§8) §8» §f%s",
                        groupPrefix.isEmpty()
                                ? ""
                                : "§3" + groupPrefix + " §8|§f ",
                        message.getAuthor(),
                        message.getMetadata().get("world_name"),
                        message.getContent());
                break;

            case TELEGRAM:
                String authorName = message.getAuthor();
                String colorPrefix = "";

                if (authorName != null && !authorName.trim().isEmpty()) {
                    String cleanName = authorName.startsWith("@")
                            ? authorName.substring(1)
                            : authorName;

                    if (isNameSaved(cleanName)) {
                        colorPrefix = "§c";
                    }
                }

                formatted = String.format("§r\uE0E2 §f%s%s §8» §f%s",
                        colorPrefix,
                        message.getAuthor(),
                        message.getContent());
                break;

            default:
                formatted = message.getMetadata().containsKey("minecraft_message")
                        ? message.getMetadata().get("minecraft_message")
                        : message.getContent();
                break;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', formatted));
        }
    }

    @NotNull
    public YamlConfiguration getDataConfig() {
        File dataFile = new File(getDataFolder(), "saved_players.yml");

        return YamlConfiguration.loadConfiguration(dataFile);
    }

    public boolean isNameSaved(@NotNull String playerName) {
        try {
            YamlConfiguration currentConfig = getDataConfig();

            return currentConfig.contains("players." + playerName.toLowerCase());
        } catch (Exception e) {
            getLogger().warning("Ошибка при проверке сохраненного игрока: " + e.getMessage());

            return false;
        }
    }

}
