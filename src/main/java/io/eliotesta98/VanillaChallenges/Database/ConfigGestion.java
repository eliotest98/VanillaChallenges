package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.FileCreator;
import io.eliotesta98.VanillaChallenges.Utils.ItemUtils;
import io.eliotesta98.VanillaChallenges.Utils.Tasks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigGestion {

    private HashMap<String, Boolean> debug = new HashMap<String, Boolean>();
    private HashMap<String, String> messages = new HashMap<String, String>();
    private HashMap<String, Challenge> challenges = new HashMap<String, Challenge>();
    private HashMap<String, Boolean> hooks = new HashMap<String, Boolean>();
    private boolean activeOnlinePoints, yesterdayTop, resetPointsAtNewChallenge, backupEnabled, randomChallengeGeneration, pointsResume;
    private String database;
    private int timeBrodcastMessageTitle, pointsOnlinePoints, minutesOnlinePoints, numberOfFilesInFolderForBackup, number, time;
    private ItemStack chestCollection;
    private Tasks tasks = new Tasks();

    public ConfigGestion(FileConfiguration file) {
        for (String event : file.getConfigurationSection("Debug").getKeys(false)) {
            debug.put(event, file.getBoolean("Debug." + event));
        }
        for (String message : file.getConfigurationSection("Messages").getKeys(false)) {
            if (message.equalsIgnoreCase("Commands") || message.equalsIgnoreCase("Errors")) {
                for (String command : file.getConfigurationSection("Messages." + message).getKeys(false)) {
                    messages.put(message + "." + command, file.getString("Messages." + message + "." + command).replace("{prefix}", messages.get("Prefix")));
                }
            } else if (message.equalsIgnoreCase("Prefix")) {
                messages.put(message, file.getString("Messages." + message));
            } else if (message.equalsIgnoreCase("TopPlayers")) {
                ArrayList<String> mexs = new ArrayList<>();
                file.getStringList("Messages.TopPlayers").forEach(value -> {
                    mexs.add(value);
                });
                int i = 1;
                while (!mexs.isEmpty()) {
                    messages.put("topPlayers" + i, mexs.get(0));
                    mexs.remove(0);
                    i++;
                }
            } else {
                messages.put(message, file.getString("Messages." + message).replace("{prefix}", messages.get("Prefix")));
            }
        }
        for (String hoock : file.getConfigurationSection("Configuration.Hooks").getKeys(false)) {
            hooks.put(hoock, file.getBoolean("Configuration.Hooks." + hoock));
        }
        File folder = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges");
        boolean folderCreate = folder.mkdir();
        if (folderCreate) {
            ArrayList<File> files = new ArrayList<>();
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "BlockBreaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "BlockPlacer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Cooker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Crafter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Consumer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "ExpCollector.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Killer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Breeder.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Feeder.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Shooter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "JumperHorse.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Jumper.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Dyer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Raider.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Fisher.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Sprinter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Mover.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Damager.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Sneaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "ItemBreaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Absorber.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Harvester.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "EggThrower.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Enchanter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Chatter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "ItemCollector.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "InventoryControl.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "BoatMove.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges", "Dyer.yml"));
            if(hooks.get("CubeGenerator")) {
                files.add(new File(Main.instance.getDataFolder() + "Challenges", "CubeGenerator.yml"));
            }
            FileCreator.createAllFiles(files);
        }
        for (File fileChallenge : folder.listFiles()) {
            YamlConfiguration yamlChallenge = YamlConfiguration.loadConfiguration(fileChallenge);
            String challengeName = fileChallenge.getName().replace(".yml", "");
            String block = yamlChallenge.getString(challengeName + ".Block");
            String blockOnPlaced = yamlChallenge.getString(challengeName + ".BlockOnPlaced");
            String typeChallenge = yamlChallenge.getString(challengeName + ".TypeChallenge");
            int timeChallenge = yamlChallenge.getInt(challengeName + ".Time");
            ArrayList<String> rewards = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Rewards");
            ArrayList<String> title = new ArrayList<>(yamlChallenge.getStringList(challengeName + ".Title"));
            String item = yamlChallenge.getString(challengeName + ".Item");
            String mob = yamlChallenge.getString(challengeName + ".Mob");
            String itemInHand = yamlChallenge.getString(challengeName + ".ItemInHand");
            double force = yamlChallenge.getDouble(challengeName + ".Force");
            double power = yamlChallenge.getDouble(challengeName + ".Power");
            String color = yamlChallenge.getString(challengeName + ".Color");
            String cause = yamlChallenge.getString(challengeName + ".Cause");
            String vehicle = yamlChallenge.getString(challengeName + ".Vehicle");
            int point = yamlChallenge.getInt(challengeName + ".Point");
            int number = yamlChallenge.getInt(challengeName + ".Number");
            int time = yamlChallenge.getInt(challengeName + ".Time");
            int minutes = yamlChallenge.getInt(challengeName+".Minutes");
            int pointsBoost = 0;
            int multiplier = 1;
            int boostMinutes = 0;
            if (yamlChallenge.getBoolean(challengeName + ".Boost.Enabled")) {
                pointsBoost = yamlChallenge.getInt(challengeName + ".Boost.Points");
                multiplier = yamlChallenge.getInt(challengeName + ".Boost.Multiplier");
                boostMinutes = yamlChallenge.getInt(challengeName + ".Boost.Minutes");
            }
            int pointsBoostSinglePlayer = 0;
            int multiplierSinglePlayer = 1;
            int minutesSinglePlayer = 0;
            if (yamlChallenge.getBoolean(challengeName + ".BoostPlayer.Enabled")) {
                pointsBoostSinglePlayer = yamlChallenge.getInt(challengeName + ".BoostPlayer.Points");
                multiplierSinglePlayer = yamlChallenge.getInt(challengeName + ".BoostPlayer.Multiplier");
                minutesSinglePlayer = yamlChallenge.getInt(challengeName + ".BoostPlayer.Minutes");
            }
            String sneaking = yamlChallenge.getString(challengeName + ".Sneaking");
            String onGround = yamlChallenge.getString(challengeName + ".OnGround");
            String stringFormatter = yamlChallenge.getString(challengeName + ".StringFormatter");
            Challenge challenge = new Challenge(block, blockOnPlaced, typeChallenge, rewards, title, item, itemInHand, mob, force, power, color, cause, point, pointsBoost, multiplier, boostMinutes, number, time, vehicle, sneaking, onGround, pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, timeChallenge, challengeName, stringFormatter,minutes);
            challenges.put(challengeName, challenge);
        }
        timeBrodcastMessageTitle = file.getInt("Configuration.BroadcastMessage.TimeTitleChallenges");
        database = file.getString("Configuration.Database");
        resetPointsAtNewChallenge = file.getBoolean("Configuration.ResetPointsAtNewChallenge");
        activeOnlinePoints = file.getBoolean("Configuration.OnlinePoints.Enabled");
        yesterdayTop = file.getBoolean("Configuration.Top.YesterdayTop");
        backupEnabled = file.getBoolean("Configuration.Backup.Enabled");
        randomChallengeGeneration = file.getBoolean("Configuration.RandomChallengeGeneration");
        numberOfFilesInFolderForBackup = file.getInt("Configuration.Backup.NumberOfFilesInFolder");
        pointsOnlinePoints = file.getInt("Configuration.OnlinePoints.Point");
        minutesOnlinePoints = file.getInt("Configuration.OnlinePoints.Minutes");
        ArrayList<String> lore = new ArrayList<>(file.getStringList("Configuration.CollectionChallengeItem.Lore"));
        chestCollection = ItemUtils.getChest(file.getString("Configuration.CollectionChallengeItem.Type"), file.getString("Configuration.CollectionChallengeItem.Name"), lore);
        pointsResume = file.getBoolean("Configuration.PointsResume");
    }

    public HashMap<String, Boolean> getDebug() {
        return debug;
    }

    public void setDebug(HashMap<String, Boolean> debug) {
        this.debug = debug;
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, String> messages) {
        this.messages = messages;
    }

    public HashMap<String, Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(HashMap<String, Challenge> challenges) {
        this.challenges = challenges;
    }

    public int getTimeBrodcastMessageTitle() {
        return timeBrodcastMessageTitle;
    }

    public void setTimeBrodcastMessageTitle(int timeBrodcastMessageTitle) {
        this.timeBrodcastMessageTitle = timeBrodcastMessageTitle;
    }

    public HashMap<String, Boolean> getHooks() {
        return hooks;
    }

    public void setHooks(HashMap<String, Boolean> hooks) {
        this.hooks = hooks;
    }

    public boolean isActiveOnlinePoints() {
        return activeOnlinePoints;
    }

    public void setActiveOnlinePoints(boolean activeOnlinePoints) {
        this.activeOnlinePoints = activeOnlinePoints;
    }

    public int getPointsOnlinePoints() {
        return pointsOnlinePoints;
    }

    public void setPointsOnlinePoints(int pointsOnlinePoints) {
        this.pointsOnlinePoints = pointsOnlinePoints;
    }

    public int getMinutesOnlinePoints() {
        return minutesOnlinePoints;
    }

    public void setMinutesOnlinePoints(int minutesOnlinePoints) {
        this.minutesOnlinePoints = minutesOnlinePoints;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public boolean isYesterdayTop() {
        return yesterdayTop;
    }

    public void setYesterdayTop(boolean yesterdayTop) {
        this.yesterdayTop = yesterdayTop;
    }

    public boolean isResetPointsAtNewChallenge() {
        return resetPointsAtNewChallenge;
    }

    public void setResetPointsAtNewChallenge(boolean resetPointsAtNewChallenge) {
        this.resetPointsAtNewChallenge = resetPointsAtNewChallenge;
    }

    public ItemStack getChestCollection() {
        return chestCollection;
    }

    public void setChestCollection(ItemStack chestCollection) {
        this.chestCollection = chestCollection;
    }

    public boolean isBackupEnabled() {
        return backupEnabled;
    }

    public void setBackupEnabled(boolean backupEnabled) {
        this.backupEnabled = backupEnabled;
    }

    public int getNumberOfFilesInFolderForBackup() {
        return numberOfFilesInFolderForBackup;
    }

    public void setNumberOfFilesInFolderForBackup(int numberOfFilesInFolderForBackup) {
        this.numberOfFilesInFolderForBackup = numberOfFilesInFolderForBackup;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isRandomChallengeGeneration() {
        return randomChallengeGeneration;
    }

    public void setRandomChallengeGeneration(boolean randomChallengeGeneration) {
        this.randomChallengeGeneration = randomChallengeGeneration;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public boolean isPointsResume() {
        return pointsResume;
    }

    public void setPointsResume(boolean pointsResume) {
        this.pointsResume = pointsResume;
    }
}
