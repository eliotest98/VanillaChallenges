package io.eliotesta98.VanillaChallenges.Events.Challenges.ItemCollector;

import io.eliotesta98.VanillaChallenges.Core.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Database {

    private final FileConfiguration file;
    private final File configFile;

    public Database() {
        this.configFile = new File(Main.instance.getDataFolder(), "itemCollectorDb.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        file = YamlConfiguration.loadConfiguration(configFile);
    }

    public void loadChests(HashMap<String, Location> chestLocations) {
        if (!file.contains("Chest")) {
            return;
        }
        for (String playerName : file.getConfigurationSection("Chest").getKeys(false)) {
            String location = file.getString("Chest." + playerName);
            String[] locationSplitted = location.split(Pattern.quote("\\"));
            chestLocations.put(playerName, new Location(
                    Bukkit.getWorld(locationSplitted[0]),
                    Integer.parseInt(locationSplitted[1]),
                    Integer.parseInt(locationSplitted[2]),
                    Integer.parseInt(locationSplitted[3])));
        }
    }

    public void insertChest(String playerName, Location location) {
        file.set("Chest." + playerName, location.getWorld().getName() + "\\" + location.getBlockX() + "\\" + location.getBlockY() + "\\" + location.getBlockZ());
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateChest(String playerName, Location chestLocation) {
        deleteChest(playerName);
        insertChest(playerName, chestLocation);
    }

    public void deleteChest(String playerName) {
        file.set("Chest." + playerName, null);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile() {
        configFile.delete();
    }

    public void saveFile() throws IOException {
        file.save(configFile);
    }

}
