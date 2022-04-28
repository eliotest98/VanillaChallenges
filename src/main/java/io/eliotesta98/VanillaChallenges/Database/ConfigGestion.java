package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.ItemUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigGestion {

    private HashMap<String, Boolean> debug = new HashMap<String, Boolean>();
    private HashMap<String, String> messages = new HashMap<String, String>();
    private HashMap<String, Challenge> challenges = new HashMap<String, Challenge>();
    private HashMap<String, Boolean> hooks = new HashMap<String, Boolean>();
    private boolean activeOnlinePoints, yesterdayTop, resetPointsAtNewChallenge, backupEnabled, randomChallengeGeneration;
    private String database;
    private int timeBrodcastMessageTitle, pointsOnlinePoints, minutesOnlinePoints, numberOfFilesInFolderForBackup, number, time;
    private ItemStack chestCollection;

    public ConfigGestion(FileConfiguration file) {
        for (String event : file.getConfigurationSection("Debug").getKeys(false)) {
            debug.put(event, file.getBoolean("Debug." + event));
        }
        for (String message : file.getConfigurationSection("Message").getKeys(false)) {
            if (message.equalsIgnoreCase("topPlayers")) {
                ArrayList<String> mexs = new ArrayList<>();
                file.getStringList("Message.topPlayers").forEach(value -> {
                    mexs.add(value);
                });
                int i = 1;
                while (!mexs.isEmpty()) {
                    messages.put("topPlayers" + i, mexs.get(0));
                    mexs.remove(0);
                    i++;
                }
            } else {
                messages.put(message, file.getString("Message." + message));
            }
        }
        for (String challengeName : file.getConfigurationSection("Configuration.Challenges").getKeys(false)) {
            String block = file.getString("Configuration.Challenges." + challengeName + ".Block");
            String blockOnPlaced = file.getString("Configuration.Challenges." + challengeName + ".BlockOnPlaced");
            String typeChallenge = file.getString("Configuration.Challenges." + challengeName + ".TypeChallenge");
            String reward = file.getString("Configuration.Challenges." + challengeName + ".Reward");
            ArrayList<String> title = new ArrayList<>();
            file.getStringList("Configuration.Challenges." + challengeName + ".Title").forEach(value -> {
                title.add(value);
            });
            String item = file.getString("Configuration.Challenges." + challengeName + ".Item");
            String mob = file.getString("Configuration.Challenges." + challengeName + ".Mob");
            String itemInHand = file.getString("Configuration.Challenges." + challengeName + ".ItemInHand");
            double force = file.getDouble("Configuration.Challenges." + challengeName + ".Force");
            double power = file.getDouble("Configuration.Challenges." + challengeName + ".Power");
            String color = file.getString("Configuration.Challenges." + challengeName + ".Color");
            String cause = file.getString("Configuration.Challenges." + challengeName + ".Cause");
            int point = file.getInt("Configuration.Challenges." + challengeName + ".Point");
            int number = file.getInt("Configuration.Challenges." + challengeName + ".Number");
            int time = file.getInt("Configuration.Challenges." + challengeName + ".Time");
            int pointsBoost = 0;
            int multiplier = 1;
            int minutes = 0;
            if (file.getBoolean("Configuration.Challenges." + challengeName + ".Boost.Enabled")) {
                pointsBoost = file.getInt("Configuration.Challenges." + challengeName + ".Boost.Points");
                multiplier = file.getInt("Configuration.Challenges." + challengeName + ".Boost.Multiplier");
                minutes = file.getInt("Configuration.Challenges." + challengeName + ".Boost.Minutes");
            }
            Challenge challenge = new Challenge(block, blockOnPlaced, typeChallenge, reward, title, item, itemInHand, mob, force, power, color, cause, point, pointsBoost, multiplier, minutes, number, time);
            challenges.put(challengeName, challenge);
        }
        timeBrodcastMessageTitle = file.getInt("Configuration.BroadcastMessage.TimeTitleChallenges");
        for (String hoock : file.getConfigurationSection("Configuration.Hooks").getKeys(false)) {
            hooks.put(hoock, file.getBoolean("Configuration.Hooks." + hoock));
        }
        for (String db : file.getConfigurationSection("Configuration.Database").getKeys(false)) {
            if (file.getBoolean("Configuration.Database." + db)) {
                database = db;
                break;
            }
        }
        resetPointsAtNewChallenge = file.getBoolean("Configuration.ResetPointsAtNewChallenge");
        activeOnlinePoints = file.getBoolean("Configuration.OnlinePoints.Enabled");
        yesterdayTop = file.getBoolean("Configuration.Top.YesterdayTop");
        backupEnabled = file.getBoolean("Configuration.Backup.Enabled");
        randomChallengeGeneration = file.getBoolean("Configuration.RandomChallengeGeneration");
        numberOfFilesInFolderForBackup = file.getInt("Configuration.Backup.NumberOfFilesInFolder");
        pointsOnlinePoints = file.getInt("Configuration.OnlinePoints.Point");
        minutesOnlinePoints = file.getInt("Configuration.OnlinePoints.Minutes");
        ArrayList<String> lore = new ArrayList<>();
        file.getStringList("Configuration.CollectionChallengeItem.Lore").forEach(value -> {
            lore.add(value);
        });
        chestCollection = ItemUtils.getChest(file.getString("Configuration.CollectionChallengeItem.Type"), file.getString("Configuration.CollectionChallengeItem.Name"), lore);
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
}
