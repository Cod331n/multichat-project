package ru.geoderma.multichat.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Getter
public class ConfigManager {
    
    private final JavaPlugin plugin;

    private FileConfiguration config;

    private String kafkaBootstrapServers;
    
    public ConfigManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;

        loadConfig();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        loadSettings();
    }
    
    public void saveConfig() {
        plugin.saveConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        loadSettings();
    }
    
    private void loadSettings() {
        kafkaBootstrapServers = config.getString("kafka.bootstrap-servers");

        if (kafkaBootstrapServers == null || kafkaBootstrapServers.trim().isEmpty()) {
            throw new RuntimeException("Couldn't enable plugin without parameter in 'kafka.bootstrap-servers' in " + plugin.getConfig().getName());
        }
    }
}