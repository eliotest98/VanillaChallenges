package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Interfaces.Interface;
import io.eliotesta98.VanillaChallenges.Interfaces.ItemConfig;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigGestion {

    private HashMap<String, Boolean> debug = new HashMap<>();
    private HashMap<String, String> messages = new HashMap<>();
    private HashMap<String, Challenge> challenges = new HashMap<>();
    private HashMap<String, Challenge> challengesEvent = new HashMap<>();
    private HashMap<String, Boolean> hooks = new HashMap<>();
    private HashMap<String,Interface> interfaces = new HashMap<>();
    private boolean activeOnlinePoints, rankingReward, yesterdayTop, resetPointsAtNewChallenge, backupEnabled, randomChallengeGeneration, pointsResume;
    private String database;
    private int timeBrodcastMessageTitle, pointsOnlinePoints, minutesOnlinePoints, numberOfFilesInFolderForBackup, number, time;
    private ItemStack chestCollection;
    private Tasks tasks = new Tasks();
    private ArrayList<String> controlIfChallengeExist = new ArrayList<>();

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
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "BlockBreaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "BlockPlacer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Cooker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Crafter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Consumer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "ExpCollector.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Killer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Breeder.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Feeder.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Shooter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "JumperHorse.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Jumper.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Dyer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Raider.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Fisher.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Sprinter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Mover.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Damager.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Sneaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "ItemBreaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Absorber.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Harvester.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "EggThrower.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Enchanter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Chatter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "ItemCollector.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "InventoryControl.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "BoatMove.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Dier.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Dropper.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "Healer.yml"));
            if (hooks.get("CubeGenerator")) {
                files.add(new File(Main.instance.getDataFolder() + "Challenges/Global", "CubeGenerator.yml"));
            }
            FileCreator.createAllFilesGlobal(files);
        }

        folder = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges/Global");

        for (File fileChallenge : folder.listFiles()) {
            YamlConfiguration yamlChallenge = YamlConfiguration.loadConfiguration(fileChallenge);
            String challengeName = fileChallenge.getName().replace(".yml", "");
            boolean enabled = yamlChallenge.getBoolean(challengeName + ".Enabled");
            if (!enabled) {
                controlIfChallengeExist.add(challengeName);
                continue;
            }
            ArrayList<String> worlds = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Worlds");
            ArrayList<String> blocks = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Blocks");
            ArrayList<String> blocksOnPlaced = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".BlocksOnPlaced");
            String typeChallenge = yamlChallenge.getString(challengeName + ".TypeChallenge");
            String nameChallenge = yamlChallenge.getString(challengeName + ".NameChallenge");
            int timeChallenge = yamlChallenge.getInt(challengeName + ".TimeSettings.Time");
            String startTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.Start");
            ArrayList<String> rewards = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Rewards");
            ArrayList<String> title = new ArrayList<>(yamlChallenge.getStringList(challengeName + ".Title"));
            ArrayList<String> items = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Items");
            ArrayList<String> mobs = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Mobs");
            ArrayList<String> itemsInHand = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".ItemsInHand");
            double force = yamlChallenge.getDouble(challengeName + ".Force");
            double power = yamlChallenge.getDouble(challengeName + ".Power");
            ArrayList<String> colors = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Colors");
            ArrayList<String> causes = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Causes");
            ArrayList<String> vehicles = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Vehicles");
            int point = yamlChallenge.getInt(challengeName + ".Point");
            int number = yamlChallenge.getInt(challengeName + ".Number");
            int time = yamlChallenge.getInt(challengeName + ".Time");
            int minutes = yamlChallenge.getInt(challengeName + ".Minutes");
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
            ArrayList<String> quests = new ArrayList<>();
            for(String quest: yamlChallenge.getStringList(challengeName + ".Strings.Quests")) {
                quests.add(quest.replace("{prefix}", messages.get("Prefix")));
            }
            if (quests.isEmpty()) {
                quests.add("Formatter:" + yamlChallenge.getString(challengeName + ".Strings.StringFormatter"));
            }
            boolean keepInventory = yamlChallenge.getBoolean(challengeName + ".KeepInventory");
            boolean deathInLand = yamlChallenge.getBoolean(challengeName + ".DeathInLand");
            String itemChallenge = yamlChallenge.getString(challengeName + ".ItemChallenge");
            Challenge challenge = new Challenge(nameChallenge, blocks, blocksOnPlaced, typeChallenge, rewards,
                    title, items, itemsInHand, mobs, force, power, colors, causes, point, pointsBoost,
                    multiplier, boostMinutes, number, time, vehicles, sneaking, onGround,
                    pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, timeChallenge,
                    challengeName, quests, minutes, startTimeChallenge, keepInventory, deathInLand, worlds, itemChallenge);
            challenges.put(challengeName, challenge);
        }

        if (folderCreate) {
            ArrayList<File> files = new ArrayList<>();
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "BlockBreaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "BlockPlacer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Cooker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Crafter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Consumer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "ExpCollector.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Killer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Breeder.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Feeder.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Shooter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "JumperHorse.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Jumper.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Dyer.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Raider.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Fisher.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Sprinter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Mover.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Damager.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Sneaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "ItemBreaker.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Absorber.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Harvester.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "EggThrower.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Enchanter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Chatter.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "ItemCollector.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "InventoryControl.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "BoatMove.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Dier.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Dropper.yml"));
            files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "Healer.yml"));
            if (hooks.get("CubeGenerator")) {
                files.add(new File(Main.instance.getDataFolder() + "Challenges/Event", "CubeGenerator.yml"));
            }
            FileCreator.createAllFilesEvent(files);
        }

        folder = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges/Event");

        for (File fileChallenge : folder.listFiles()) {
            YamlConfiguration yamlChallenge = YamlConfiguration.loadConfiguration(fileChallenge);
            String challengeName = fileChallenge.getName().replace(".yml", "");
            ArrayList<String> worlds = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Worlds");
            ArrayList<String> blocks = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Blocks");
            ArrayList<String> blocksOnPlaced = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".BlocksOnPlaced");
            String typeChallenge = yamlChallenge.getString(challengeName + ".TypeChallenge");
            String nameChallenge = yamlChallenge.getString(challengeName + ".NameChallenge");
            int timeChallenge = yamlChallenge.getInt(challengeName + ".TimeSettings.Time");
            String startTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.Start");
            ArrayList<String> rewards = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Rewards");
            ArrayList<String> title = new ArrayList<>(yamlChallenge.getStringList(challengeName + ".Title"));
            ArrayList<String> items = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Items");
            ArrayList<String> mobs = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Mobs");
            ArrayList<String> itemsInHand = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".ItemsInHand");
            double force = yamlChallenge.getDouble(challengeName + ".Force");
            double power = yamlChallenge.getDouble(challengeName + ".Power");
            ArrayList<String> colors = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Colors");
            ArrayList<String> causes = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Causes");
            ArrayList<String> vehicles = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Vehicles");
            int point = yamlChallenge.getInt(challengeName + ".Point");
            int number = yamlChallenge.getInt(challengeName + ".Number");
            int time = yamlChallenge.getInt(challengeName + ".Time");
            int minutes = yamlChallenge.getInt(challengeName + ".Minutes");
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
            ArrayList<String> quests = new ArrayList<>();
            for(String quest: yamlChallenge.getStringList(challengeName + ".Strings.Quests")) {
                quests.add(quest.replace("{prefix}", messages.get("Prefix")));
            }
            if (quests.isEmpty()) {
                quests.add("Formatter:" + yamlChallenge.getString(challengeName + ".Strings.StringFormatter"));
            }
            boolean keepInventory = yamlChallenge.getBoolean(challengeName + ".KeepInventory");
            boolean deathInLand = yamlChallenge.getBoolean(challengeName + ".DeathInLand");
            String itemChallenge = yamlChallenge.getString(challengeName + ".ItemChallenge");
            Challenge challenge = new Challenge(nameChallenge, blocks, blocksOnPlaced, typeChallenge, rewards,
                    title, items, itemsInHand, mobs, force, power, colors, causes, point, pointsBoost,
                    multiplier, boostMinutes, number, time, vehicles, sneaking, onGround,
                    pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, timeChallenge,
                    challengeName, quests, minutes, startTimeChallenge, keepInventory, deathInLand, worlds, itemChallenge);
            challengesEvent.put(challengeName, challenge);
        }

        timeBrodcastMessageTitle = file.getInt("Configuration.BroadcastMessage.TimeTitleChallenges");
        database = file.getString("Configuration.Database");
        resetPointsAtNewChallenge = file.getBoolean("Configuration.ResetPointsAtNewChallenge");
        activeOnlinePoints = file.getBoolean("Configuration.OnlinePoints.Enabled");
        yesterdayTop = file.getBoolean("Configuration.Top.YesterdayTop");
        rankingReward = file.getBoolean("Configuration.Top.RankingReward");
        backupEnabled = file.getBoolean("Configuration.Backup.Enabled");
        randomChallengeGeneration = file.getBoolean("Configuration.RandomChallengeGeneration");
        numberOfFilesInFolderForBackup = file.getInt("Configuration.Backup.NumberOfFilesInFolder");
        pointsOnlinePoints = file.getInt("Configuration.OnlinePoints.Point");
        minutesOnlinePoints = file.getInt("Configuration.OnlinePoints.Minutes");
        ArrayList<String> lore = new ArrayList<>(file.getStringList("Configuration.CollectionChallengeItem.Lore"));
        chestCollection = ItemUtils.getChest(file.getString("Configuration.CollectionChallengeItem.Type"), file.getString("Configuration.CollectionChallengeItem.Name"), lore);
        pointsResume = file.getBoolean("Configuration.PointsResume");

        for (String nameInterface : file.getConfigurationSection("Interfaces").getKeys(false)) {
            String title = file.getString("Interfaces." + nameInterface + ".Title");
            String openSound = file.getString("Interfaces." + nameInterface + ".OpenSound");
            ArrayList<String> slots = new ArrayList<String>();
            ArrayList<String> contaSlots = new ArrayList<String>();

            HashMap<String, ItemConfig> itemsConfig = new HashMap<String, ItemConfig>();
            for (String nameItem : file.getConfigurationSection("Interfaces." + nameInterface + ".Items").getKeys(false)) {
                String letter = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".Letter");
                String type = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".Type");
                String name = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".Name");
                String texture = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".Texture");
                String soundClick = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".SoundClick");
                ArrayList<String> loreItem = new ArrayList<String>(file.getStringList("Interfaces." + nameInterface + ".Items." + nameItem + ".Lore"));
                ItemConfig item = new ItemConfig(nameItem, name, type, texture, loreItem, soundClick);
                itemsConfig.put(letter, item);
            }

            file.getStringList("Interfaces." + nameInterface + ".Slots").forEach(value -> {
                for (int i = 0; i < value.length(); i++) {
                    for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                        if (itemConfig.getKey().equalsIgnoreCase(value.charAt(i) + "") && itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Challenge")) {
                            contaSlots.add("" + value.charAt(i));
                        }
                    }
                    slots.add("" + value.charAt(i));
                }
            });
            Interface customInterface;
            /*if (nameInterface.equalsIgnoreCase("CustomUpgradeCategories")) {
                customInterface = new Interface(title, openSound, slots, itemsConfig, debug.get("ClickGui"),
                        contaSlots.size(), nameInterface, "", "Generator");
            } else {*/
                customInterface = new Interface(title, openSound, slots, itemsConfig, debug.get("ClickGui"),
                        contaSlots.size(), nameInterface, "", "");
            //}
            interfaces.put(nameInterface, customInterface);
        }

    }

    public void loadCommentedConfiguration() {
        File folder = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges");
        for (File fileChallenge : folder.listFiles()) {
            CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(fileChallenge);
            try {
                String configname;

                configname = fileChallenge.getName();

                //esempio
                String splits = "bho";
                String[] strings = splits.split(":");
                InputStream resource = Main.instance.getResource("Challenges/" + configname);
                if (resource != null) {
                    cfg.syncWithConfig(fileChallenge, resource, strings);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public ArrayList<String> getControlIfChallengeExist() {
        return controlIfChallengeExist;
    }

    public void setControlIfChallengeExist(ArrayList<String> controlIfChallengeExist) {
        this.controlIfChallengeExist = controlIfChallengeExist;
    }

    public HashMap<String, Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(HashMap<String, Interface> interfaces) {
        this.interfaces = interfaces;
    }

    public HashMap<String, Challenge> getChallengesEvent() {
        return challengesEvent;
    }

    public void setChallengesEvent(HashMap<String, Challenge> challengesEvent) {
        this.challengesEvent = challengesEvent;
    }

    public boolean isRankingReward() {
        return rankingReward;
    }

    public void setRankingReward(boolean rankingReward) {
        this.rankingReward = rankingReward;
    }
}
