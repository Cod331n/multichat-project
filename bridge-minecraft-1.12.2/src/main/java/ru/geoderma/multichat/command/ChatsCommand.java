package ru.geoderma.multichat.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.jetbrains.annotations.NotNull;
import ru.cristalix.core.permissions.IPermissionService;
import ru.cristalix.core.permissions.StaffGroups;
import ru.geoderma.multichat.MultichatPlugin;

import java.io.File;
import java.io.IOException;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(
        name = "chats",
        desc = "chats",
        usage = "/chats"
))
public class ChatsCommand implements CommandExecutor {

    private final MultichatPlugin plugin;

    private File dataFile;

    private FileConfiguration dataConfig;

    public ChatsCommand(@NotNull MultichatPlugin plugin) {
        this.plugin = plugin;

        setupDataFile();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "save":
                handleSaveCommand(sender, args);
                break;
            case "check":
                handleCheckCommand(sender, args);
                break;
            case "list":
                handleListCommand(sender);
                break;
            case "remove":
                handleRemoveCommand(sender, args);
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                break;
        }

        return true;
    }


    private void setupDataFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        dataFile = new File(plugin.getDataFolder(), "saved_players.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать файл saved_players.yml: " + e.getMessage());
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void handleSaveCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Использовать команду может только игрок");
            return;
        }

        Player player = (Player) sender;

        if (IPermissionService.get().getBestGroup(player.getUniqueId()).join() != StaffGroups.CUR_BUILDER) {
            sender.sendMessage(ChatColor.RED + "Только Куратор Строителей может использовать эту команду");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /chats save <никнейм>");
            return;
        }

        String playerName = args[1];

        if (playerName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Имя должно быть от 3 символов");
            return;
        }

        if (isNameSaved(playerName)) {
            sender.sendMessage(ChatColor.GOLD + "Имя " + playerName + " уже сохранено!");
        } else {
            String formattedName = playerName.toLowerCase();
            dataConfig.set("players." + formattedName + ".original_name", playerName);
            dataConfig.set("players." + formattedName + ".saved_at", System.currentTimeMillis());
            dataConfig.set("players." + formattedName + ".saved_by", sender.getName());

            saveData();

            sender.sendMessage(ChatColor.GREEN + "Имя " + playerName + " успешно сохранено!");
        }
    }

    private void handleCheckCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /chats check <никнейм>");
            return;
        }

        String playerName = args[1];
        checkPlayerInFile(sender, playerName);
    }

    private void handleListCommand(CommandSender sender) {
        if (!dataConfig.contains("players") || dataConfig.getConfigurationSection("players").getKeys(false).isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Список сохраненных игроков пуст.");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "Сохраненные игроки");
        int count = 0;

        for (String nameKey : dataConfig.getConfigurationSection("players").getKeys(false)) {
            String originalName = dataConfig.getString("players." + nameKey + ".original_name");
            long savedAt = dataConfig.getLong("players." + nameKey + ".saved_at");
            String savedBy = dataConfig.getString("players." + nameKey + ".saved_by");
            String timeAgo = formatTimeAgo(savedAt);
            String savedByText = savedBy != null
                    ? " (сохранено: " + savedBy + ")"
                    : "";

            sender.sendMessage(ChatColor.GREEN + originalName + ChatColor.GRAY + " - " + timeAgo + savedByText);
            count++;

        }

        sender.sendMessage(ChatColor.GRAY + "Всего: " + count + " игроков");
    }

    private void handleRemoveCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Использовать команду может только игрок");
            return;
        }

        Player player = (Player) sender;

        if (IPermissionService.get().getStaffGroup(player.getUniqueId()).join() != StaffGroups.CUR_BUILDER) {
            sender.sendMessage(ChatColor.RED + "Только Куратор Строителей может использовать эту команду");
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /chats remove <никнейм>");
            return;
        }

        String playerName = args[1];
        String formattedName = playerName.toLowerCase();

        if (dataConfig.contains("players." + formattedName)) {
            String originalName = dataConfig.getString("players." + formattedName + ".original_name");
            dataConfig.set("players." + formattedName, null);
            saveData();
            sender.sendMessage(ChatColor.GREEN + "Игрок " + originalName + " удален из списка!");
        } else {
            sender.sendMessage(ChatColor.RED + "Игрок " + playerName + " не найден в списке!");
        }
    }

    private void checkPlayerInFile(CommandSender sender, String playerName) {
        String formattedName = playerName.toLowerCase();

        if (dataConfig.contains("players." + formattedName)) {
            String originalName = dataConfig.getString("players." + formattedName + ".original_name");
            long savedAt = dataConfig.getLong("players." + formattedName + ".saved_at");
            String savedBy = dataConfig.getString("players." + formattedName + ".saved_by");

            String timeAgo = formatTimeAgo(savedAt);
            String savedByText = savedBy != null
                    ? " (сохранено: " + savedBy + ")"
                    : "";

            sender.sendMessage(ChatColor.GREEN + "Игрок " + originalName + " найден в файле!");
            sender.sendMessage(ChatColor.GRAY + "Сохранен: " + timeAgo + savedByText);
        } else {
            sender.sendMessage(ChatColor.RED + "Игрок " + playerName + " не найден в файле!");
        }
    }

    private boolean isNameSaved(String playerName) {
        String formattedName = playerName.toLowerCase();
        return dataConfig.contains("players." + formattedName);
    }

    private String formatTimeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;

        if (diff < 60000) {
            return "только что";
        } else if (diff < 3600000) {
            long minutes = diff / 60000;
            return minutes + " мин. назад";
        } else if (diff < 86400000) {
            long hours = diff / 3600000;
            return hours + " ч. назад";
        } else {
            long days = diff / 86400000;
            return days + " дн. назад";
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Помощь /chats");
        sender.sendMessage(ChatColor.GOLD + "/chats" + ChatColor.WHITE + " save <никнейм>" + ChatColor.GRAY + " - сохранить имя");
        sender.sendMessage(ChatColor.GOLD + "/chats" + ChatColor.WHITE + " check <никнейм>" + ChatColor.GRAY + " - проверить сохранено ли имя");
        sender.sendMessage(ChatColor.GOLD + "/chats" + ChatColor.WHITE + " list" + ChatColor.GRAY + " - список всех сохраненных имен");
        sender.sendMessage(ChatColor.GOLD + "/chats" + ChatColor.WHITE + " remove <никнейм>" + ChatColor.GRAY + " - удалить имя из списка");
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить файл saved_players.yml: " + e.getMessage());
        }
    }

}