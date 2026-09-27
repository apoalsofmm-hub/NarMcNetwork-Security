package com.narmc.security;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.narmc.security.managers.PlayerDataManager;
import com.narmc.security.managers.ConfigManager;
import com.narmc.security.listeners.PlayerMoveListener;
import com.narmc.security.listeners.PlayerInteractListener;
import com.narmc.security.listeners.PlayerJoinListener;
import com.narmc.security.listeners.PlayerQuitListener;
import com.narmc.security.commands.AlertsCommand;
import com.narmc.security.commands.UnbanCommand;

public class NarMcSecurityPlugin extends JavaPlugin {

    private static NarMcSecurityPlugin instance;
    private PlayerDataManager playerDataManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("======================================");
        getLogger().info("NarMcNetwork Security v1.0.0");
        getLogger().info("Advanced Anti-Cheat System");
        getLogger().info("======================================");
        
        // Initialize config
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // Initialize player data manager
        playerDataManager = new PlayerDataManager(this);
        playerDataManager.loadData();
        
        // Register listeners
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        
        // Register commands
        getCommand("alerts").setExecutor(new AlertsCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        
        getLogger().info("Anti-Cheat System loaded successfully!");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.saveData();
        }
        getLogger().info("NarMcNetwork Security disabled!");
    }

    public static NarMcSecurityPlugin getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
