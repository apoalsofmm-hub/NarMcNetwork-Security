package com.narmc.security.managers;

import com.narmc.security.NarMcSecurityPlugin;
import com.narmc.security.model.PlayerViolationData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class PlayerDataManager {

    private NarMcSecurityPlugin plugin;
    private File dataFolder;
    private Map<String, PlayerViolationData> playerData;
    private Gson gson;

    public PlayerDataManager(NarMcSecurityPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "player-data");
        this.playerData = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void loadData() {
        playerData.clear();
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try {
                    FileReader reader = new FileReader(file);
                    PlayerViolationData data = gson.fromJson(reader, PlayerViolationData.class);
                    reader.close();
                    playerData.put(data.getPlayerName(), data);
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to load player data from " + file.getName());
                }
            }
        }
        plugin.getLogger().info("Loaded data for " + playerData.size() + " players");
    }

    public void saveData() {
        for (PlayerViolationData data : playerData.values()) {
            savePlayerData(data);
        }
    }

    private void savePlayerData(PlayerViolationData data) {
        try {
            File file = new File(dataFolder, data.getPlayerUUID() + ".json");
            FileWriter writer = new FileWriter(file);
            gson.toJson(data, writer);
            writer.close();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save player data for " + data.getPlayerName());
        }
    }

    public PlayerViolationData getPlayerData(String playerName, String playerUUID) {
        if (!playerData.containsKey(playerName)) {
            playerData.put(playerName, new PlayerViolationData(playerName, playerUUID));
            savePlayerData(playerData.get(playerName));
        }
        return playerData.get(playerName);
    }

    public void addViolation(String playerName, String playerUUID, String violationType) {
        PlayerViolationData data = getPlayerData(playerName, playerUUID);
        data.addViolation(violationType);
        savePlayerData(data);
    }

    public void resetPlayerData(String playerName) {
        if (playerData.containsKey(playerName)) {
            PlayerViolationData data = playerData.get(playerName);
            data.resetViolations();
            savePlayerData(data);
        }
    }

    public void banPlayer(String playerName, String playerUUID, String reason) {
        PlayerViolationData data = getPlayerData(playerName, playerUUID);
        data.setBanned(true);
        data.setBanReason(reason);
        data.setBanTime(System.currentTimeMillis());
        savePlayerData(data);
    }

    public boolean isPlayerBanned(String playerName) {
        if (playerData.containsKey(playerName)) {
            return playerData.get(playerName).isBanned();
        }
        return false;
    }

    public PlayerViolationData getPlayerBanData(String playerName) {
        return playerData.getOrDefault(playerName, null);
    }

    public void unbanPlayer(String playerName) {
        if (playerData.containsKey(playerName)) {
            PlayerViolationData data = playerData.get(playerName);
            data.setBanned(false);
            data.setBanReason(null);
            data.setBanTime(0);
            savePlayerData(data);
        }
    }

    public Map<String, PlayerViolationData> getAllPlayerData() {
        return new HashMap<>(playerData);
    }
}
