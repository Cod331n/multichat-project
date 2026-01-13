package ru.geoderma.multichat;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LuckPermsUtils {

    @Getter
    @Nullable
    private static LuckPerms luckPerms = null;

    @Getter
    private static boolean enabled = false;

    public static boolean initialize() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            return false;
        }

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            luckPerms = provider.getProvider();
            enabled = true;

            return true;
        }

        return false;
    }

    @Nullable
    public static User getUser(@NotNull Player player) {
        if (!enabled || luckPerms == null) {
            return null;
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            user = luckPerms.getUserManager().loadUser(player.getUniqueId()).join();
        }

        return user;
    }

    @Nullable
    public static String getPrimaryGroup(@NotNull Player player) {
        User user = getUser(player);
        return user != null
                ? user.getPrimaryGroup()
                : null;
    }

    @Nullable
    public static String getPlayerPrefix(@NotNull Player player) {
        if (!enabled || luckPerms == null) {
            return null;
        }

        User user = getUser(player);

        if (user == null) {
            return null;
        }

        String prefix = user.getCachedData().getMetaData().getPrefix();

        if (prefix == null) {
            Group group = luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());

            if (group != null) {
                prefix = group.getCachedData().getMetaData().getPrefix();
            }
        }

        return prefix;
    }

}