package com.narmc.security.managers;

import com.narmc.security.NarMcSecurityPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private NarMcSecurityPlugin plugin;
    private File configFile;
    private FileConfiguration config;

    public ConfigManager(NarMcSecurityPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            createDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void createDefaultConfig() {
        try {
            configFile.createNewFile();
            FileConfiguration newConfig = YamlConfiguration.loadConfiguration(configFile);

            // Settings
            newConfig.set("settings.enabled", true);
            newConfig.set("settings.max-violations", 3);
            newConfig.set("settings.check-interval", 5); // ticks

            // Detection Settings
            newConfig.set("detection.aim-assist.enabled", true);
            newConfig.set("detection.aim-assist.rotation-threshold", 15.0);
            newConfig.set("detection.aim-assist.camera-speed-threshold", 50.0);

            newConfig.set("detection.reach.enabled", true);
            newConfig.set("detection.reach.max-distance", 3.0);

            newConfig.set("detection.trigger-bot.enabled", true);
            newConfig.set("detection.trigger-bot.click-delay-threshold", 2);

            newConfig.set("detection.auto-totem.enabled", true);
            newConfig.set("detection.auto-totem.swap-speed-threshold", 3);

            newConfig.set("detection.speed.enabled", true);
            newConfig.set("detection.speed.horizontal-threshold", 0.55);
            newConfig.set("detection.speed.vertical-threshold", 0.45);

            newConfig.set("detection.fly.enabled", true);
            newConfig.set("detection.fly.vertical-threshold", 0.5);

            // Messages
            newConfig.set("messages.prefix", "&c[NarMc Security] &r");
            newConfig.set("messages.admin-alert", "&e{PLAYER} &cdetected for &e{HACK_TYPE}");
            newConfig.set("messages.warning-1", "&cWarning &8[&e1&8/&e3&8]: &cUnnatural behavior detected");
            newConfig.set("messages.warning-2", "&cWarning &8[&e2&8/&e3&8]: &cBan will be applied");
            newConfig.set("messages.banned", "&c{PLAYER} has been banned for &e{HACK_TYPE}");
            newConfig.set("messages.player-banned", "&cYou have been banned for cheating");

            newConfig.save(configFile);
            plugin.getLogger().info("Default config.yml created!");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create config: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    public boolean isDetectionEnabled(String detectionType) {
        return config.getBoolean("detection." + detectionType + ".enabled", true);
    }

    public double getDoubleValue(String path) {
        return config.getDouble(path);
    }

    public int getIntValue(String path) {
        return config.getInt(path);
    }

    public String getMessageFormatted(String key, String... replacements) {
        String message = config.getString("messages." + key, "");
        String prefix = config.getString("messages.prefix", "&c[Security] &r");

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }

        return translateColorCodes(prefix + message);
    }

    public static String translateColorCodes(String message) {
        return message.replace("&0", "\u00a70")
                .replace("&1", "\u00a71")
                .replace("&2", "\u00a72")
                .replace("&3", "\u00a73")
                .replace("&4", "\u00a74")
                .replace("&5", "\u00a75")
                .replace("&6", "\u00a76")
                .replace("&7", "\u00a77")
                .replace("&8", "\u00a78")
                .replace("&9", "\u00a79")
                .replace("&a", "\u00a7a")
                .replace("&b", "\u00a7b")
                .replace("&c", "\u00a7c")
                .replace("&d", "\u00a7d")
                .replace("&e", "\u00a7e")
                .replace("&f", "\u00a7f")
                .replace("&r", "\u00a7r");
    }
}
