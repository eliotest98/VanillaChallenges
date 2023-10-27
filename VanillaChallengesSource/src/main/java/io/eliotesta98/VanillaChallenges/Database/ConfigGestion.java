package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Interfaces.Interface;
import io.eliotesta98.VanillaChallenges.Interfaces.ItemConfig;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;

public class ConfigGestion {

    private HashMap<String, Boolean> debug = new HashMap<>();
    private HashMap<String, String> messages = new HashMap<>();
    private HashMap<String, Challenge> challenges = new HashMap<>();
    private HashMap<String, Challenge> challengesEvent = new HashMap<>();
    private HashMap<String, Boolean> hooks = new HashMap<>();
    private HashMap<String, Interface> interfaces = new HashMap<>();
    private boolean activeOnlinePoints, rankingReward, randomReward = false, yesterdayTop, resetPointsAtNewChallenge,
            backupEnabled, pointsResume, lockedInterface;
    private String database, challengeGeneration;
    private int timeBrodcastMessageTitle, pointsOnlinePoints, minutesOnlinePoints, numberOfFilesInFolderForBackup, number,
            time, numberOfTop, minimumPoints;
    private ItemStack chestCollection;
    private Tasks tasks = new Tasks();
    private ArrayList<String> controlIfChallengeExist = new ArrayList<>();

    public ConfigGestion(FileConfiguration file) throws IOException {

        for (String event : file.getConfigurationSection("Debug").getKeys(false)) {
            debug.put(event, file.getBoolean("Debug." + event));
        }
        for (String message : file.getConfigurationSection("Messages").getKeys(false)) {
            if (message.equalsIgnoreCase("Commands") || message.equalsIgnoreCase("Errors")
                    || message.equalsIgnoreCase("Success") || message.equalsIgnoreCase("Lists")
                    || message.equalsIgnoreCase("Points")) {
                for (String command : file.getConfigurationSection("Messages." + message).getKeys(false)) {
                    messages.put(message + "." + command, file.getString("Messages." + message + "." + command).replace("{prefix}", messages.get("Prefix")));
                }
            } else if (message.equalsIgnoreCase("Prefix")) {
                messages.put(message, file.getString("Messages." + message));
            } else if (message.equalsIgnoreCase("TopPlayers")) {
                ArrayList<String> mexs = new ArrayList<>(file.getStringList("Messages.TopPlayers"));
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

        FileCreator.addFiles(hooks);

        File folderChallenges = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges");
        File folderGlobal = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Global");
        File folderEvent = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Event");

        boolean folderCreate = folderChallenges.mkdir();

        if (folderCreate) {
            FileCreator.createAllFiles("Global");
        } else {
            FileCreator.controlFiles("Global", folderGlobal.listFiles());
        }

        for (File fileChallenge : folderGlobal.listFiles()) {
            String splits = "bho";
            String[] strings = splits.split(":");
            String configname = "Challenges/Global/" + fileChallenge.getName();
            CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(fileChallenge);
            cfg.syncWithConfig(fileChallenge, Main.instance.getResource(configname), strings);

            YamlConfiguration yamlChallenge = YamlConfiguration.loadConfiguration(fileChallenge);
            String challengeName = fileChallenge.getName().replace(".yml", "");
            boolean enabled = yamlChallenge.getBoolean(challengeName + ".Enabled");
            if (!enabled) {
                controlIfChallengeExist.add(challengeName);
                continue;
            }
            ArrayList<String> worlds = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Worlds");
            ArrayList<String> blocks = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Blocks");
            if (blocks.contains("RANDOM")) {
                blocks.remove(blocks.size() - 1);
                Collections.shuffle(blocks);
                String block = blocks.get(0);
                blocks.clear();
                blocks.add(block);
            }
            ArrayList<String> blocksOnPlaced = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".BlocksOnPlaced");
            if (blocksOnPlaced.contains("RANDOM")) {
                blocksOnPlaced.remove(blocksOnPlaced.size() - 1);
                Collections.shuffle(blocksOnPlaced);
                String block = blocksOnPlaced.get(0);
                blocksOnPlaced.clear();
                blocksOnPlaced.add(block);
            }
            String typeChallenge = yamlChallenge.getString(challengeName + ".TypeChallenge");
            String nameChallenge = yamlChallenge.getString(challengeName + ".NameChallenge");
            String endTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.End");
            int end;
            if (endTimeChallenge.equalsIgnoreCase("Random")) {
                Random random = new Random();
                end = random.nextInt(24) + 1;
                endTimeChallenge = end + ":00";
            } else {
                end = Integer.parseInt(endTimeChallenge.split(":")[0]);
            }
            String startTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.Start");
            int start;
            if (startTimeChallenge.equalsIgnoreCase("Random")) {
                Random random = new Random();
                start = random.nextInt(end) + 1;
                startTimeChallenge = start + ":00";
            }
            ArrayList<String> rewards = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Rewards");
            ArrayList<String> title = new ArrayList<>(yamlChallenge.getStringList(challengeName + ".Title"));
            ArrayList<String> items = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Items");
            if (items.contains("RANDOM")) {
                items.remove(items.size() - 1);
                Collections.shuffle(items);
                String block = items.get(0);
                items.clear();
                items.add(block);
            }
            ArrayList<String> mobs = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Mobs");
            if (mobs.contains("RANDOM")) {
                mobs.remove(mobs.size() - 1);
                Collections.shuffle(mobs);
                String block = mobs.get(0);
                mobs.clear();
                mobs.add(block);
            }
            ArrayList<String> itemsInHand = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".ItemsInHand");
            if (itemsInHand.contains("RANDOM")) {
                itemsInHand.remove(itemsInHand.size() - 1);
                Collections.shuffle(itemsInHand);
                String block = itemsInHand.get(0);
                itemsInHand.clear();
                itemsInHand.add(block);
            }
            double force = yamlChallenge.getDouble(challengeName + ".Force");
            double power = yamlChallenge.getDouble(challengeName + ".Power");
            ArrayList<String> colors = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Colors");
            if (colors.contains("RANDOM")) {
                colors.remove(colors.size() - 1);
                Collections.shuffle(colors);
                String block = colors.get(0);
                colors.clear();
                colors.add(block);
            }
            ArrayList<String> causes = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Causes");
            if (causes.contains("RANDOM")) {
                causes.remove(causes.size() - 1);
                Collections.shuffle(causes);
                String block = causes.get(0);
                causes.clear();
                causes.add(block);
            }
            ArrayList<String> vehicles = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Vehicles");
            if (vehicles.contains("RANDOM")) {
                vehicles.remove(vehicles.size() - 1);
                Collections.shuffle(vehicles);
                String block = vehicles.get(0);
                vehicles.clear();
                vehicles.add(block);
            }
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
            String sneaking = (yamlChallenge.getString(challengeName + ".Sneaking") == null) ? "NOBODY" : yamlChallenge.getString(challengeName + ".Sneaking");
            String onGround = (yamlChallenge.getString(challengeName + ".OnGround") == null) ? "NOBODY" : yamlChallenge.getString(challengeName + ".OnGround");
            ArrayList<String> quests = new ArrayList<>();
            for (String quest : yamlChallenge.getStringList(challengeName + ".Strings.Quests")) {
                quests.add(quest.replace("{prefix}", messages.get("Prefix")));
            }
            if (quests.isEmpty()) {
                quests.add("Formatter:" + yamlChallenge.getString(challengeName + ".Strings.StringFormatter"));
            }
            boolean keepInventory = yamlChallenge.getBoolean(challengeName + ".KeepInventory");
            boolean deathInLand = yamlChallenge.getBoolean(challengeName + ".DeathInLand");
            String itemChallenge = yamlChallenge.getString(challengeName + ".ItemChallenge");
            if (itemChallenge.contains(";")) {
                String[] x = itemChallenge.split(";");
                if (Material.getMaterial(x[0]) == null) {
                    Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR WITH MATERIAL " + x[0] + " IN " + configname + " AT LINE: " + challengeName + ".ItemChallenge"));
                    itemChallenge = "DIRT";
                }
            } else {
                if (Material.getMaterial(itemChallenge) == null) {
                    Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR WITH MATERIAL " + itemChallenge + " IN " + configname + " AT LINE: " + challengeName + ".ItemChallenge"));
                    itemChallenge = "DIRT";
                }
            }
            Challenge challenge = new Challenge(nameChallenge, blocks, blocksOnPlaced, typeChallenge, rewards,
                    title, items, itemsInHand, mobs, force, power, colors, causes, point, pointsBoost,
                    multiplier, boostMinutes, number, time, vehicles, sneaking, onGround,
                    pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, endTimeChallenge,
                    challengeName, quests, minutes, startTimeChallenge, keepInventory, deathInLand, worlds, itemChallenge);
            challenges.put(challengeName, challenge);
        }

        Main.instance.getServer().getConsoleSender().sendMessage("§a" + folderGlobal.listFiles().length + " Global Challenges loaded!");

        if (folderCreate) {
            FileCreator.createAllFiles("Event");
        } else {
            FileCreator.controlFiles("Event", folderEvent.listFiles());
        }

        for (File fileChallenge : folderEvent.listFiles()) {
            String splits = "bho";
            String[] strings = splits.split(":");
            String configname = "Challenges/Event/" + fileChallenge.getName();
            CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(fileChallenge);
            cfg.syncWithConfig(fileChallenge, Main.instance.getResource(configname), strings);

            YamlConfiguration yamlChallenge = YamlConfiguration.loadConfiguration(fileChallenge);
            String challengeName = fileChallenge.getName().replace(".yml", "");
            ArrayList<String> worlds = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Worlds");
            ArrayList<String> blocks = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Blocks");
            if (blocks.contains("RANDOM")) {
                blocks.remove(blocks.size() - 1);
                Collections.shuffle(blocks);
                String block = blocks.get(0);
                blocks.clear();
                blocks.add(block);
            }
            ArrayList<String> blocksOnPlaced = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".BlocksOnPlaced");
            if (blocksOnPlaced.contains("RANDOM")) {
                blocksOnPlaced.remove(blocksOnPlaced.size() - 1);
                Collections.shuffle(blocksOnPlaced);
                String block = blocksOnPlaced.get(0);
                blocksOnPlaced.clear();
                blocksOnPlaced.add(block);
            }
            String typeChallenge = yamlChallenge.getString(challengeName + ".TypeChallenge");
            String nameChallenge = yamlChallenge.getString(challengeName + ".NameChallenge");
            String endTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.End");
            int end;
            if (endTimeChallenge.equalsIgnoreCase("Random")) {
                Random random = new Random();
                end = random.nextInt(24) + 1;
                endTimeChallenge = end + ":00";
            } else {
                end = Integer.parseInt(endTimeChallenge.split(":")[0]);
            }
            String startTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.Start");
            int start;
            if (startTimeChallenge.equalsIgnoreCase("Random")) {
                Random random = new Random();
                start = random.nextInt(end) + 1;
                startTimeChallenge = start + ":00";
            }
            ArrayList<String> rewards = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Rewards");
            ArrayList<String> title = new ArrayList<>(yamlChallenge.getStringList(challengeName + ".Title"));
            ArrayList<String> items = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Items");
            if (items.contains("RANDOM")) {
                items.remove(items.size() - 1);
                Collections.shuffle(items);
                String block = items.get(0);
                items.clear();
                items.add(block);
            }
            ArrayList<String> mobs = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Mobs");
            if (mobs.contains("RANDOM")) {
                mobs.remove(mobs.size() - 1);
                Collections.shuffle(mobs);
                String block = mobs.get(0);
                mobs.clear();
                mobs.add(block);
            }
            ArrayList<String> itemsInHand = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".ItemsInHand");
            if (itemsInHand.contains("RANDOM")) {
                itemsInHand.remove(itemsInHand.size() - 1);
                Collections.shuffle(itemsInHand);
                String block = itemsInHand.get(0);
                itemsInHand.clear();
                itemsInHand.add(block);
            }
            double force = yamlChallenge.getDouble(challengeName + ".Force");
            double power = yamlChallenge.getDouble(challengeName + ".Power");
            ArrayList<String> colors = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Colors");
            if (colors.contains("RANDOM")) {
                colors.remove(colors.size() - 1);
                Collections.shuffle(colors);
                String block = colors.get(0);
                colors.clear();
                colors.add(block);
            }
            ArrayList<String> causes = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Causes");
            if (causes.contains("RANDOM")) {
                causes.remove(causes.size() - 1);
                Collections.shuffle(causes);
                String block = causes.get(0);
                causes.clear();
                causes.add(block);
            }
            ArrayList<String> vehicles = (ArrayList<String>) yamlChallenge.getStringList(challengeName + ".Vehicles");
            if (vehicles.contains("RANDOM")) {
                vehicles.remove(vehicles.size() - 1);
                Collections.shuffle(vehicles);
                String block = vehicles.get(0);
                vehicles.clear();
                vehicles.add(block);
            }
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

            String sneaking = (yamlChallenge.getString(challengeName + ".Sneaking") == null) ? "NOBODY" : yamlChallenge.getString(challengeName + ".Sneaking");
            String onGround = (yamlChallenge.getString(challengeName + ".OnGround") == null) ? "NOBODY" : yamlChallenge.getString(challengeName + ".OnGround");

            ArrayList<String> quests = new ArrayList<>();
            for (String quest : yamlChallenge.getStringList(challengeName + ".Strings.Quests")) {
                quests.add(quest.replace("{prefix}", messages.get("Prefix")));
            }
            if (quests.isEmpty()) {
                quests.add("Formatter:" + yamlChallenge.getString(challengeName + ".Strings.StringFormatter"));
            }
            boolean keepInventory = yamlChallenge.getBoolean(challengeName + ".KeepInventory");
            boolean deathInLand = yamlChallenge.getBoolean(challengeName + ".DeathInLand");
            String itemChallenge = yamlChallenge.getString(challengeName + ".ItemChallenge");
            if (itemChallenge.contains(";")) {
                String[] x = itemChallenge.split(";");
                if (Material.getMaterial(x[0]) == null) {
                    Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR WITH MATERIAL " + x[0] + " IN " + configname + " AT LINE: " + challengeName + ".ItemChallenge"));
                    itemChallenge = "DIRT";
                }
            } else {
                if (Material.getMaterial(itemChallenge) == null) {
                    Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR WITH MATERIAL " + itemChallenge + " IN " + configname + " AT LINE: " + challengeName + ".ItemChallenge"));
                    itemChallenge = "DIRT";
                }
            }
            Challenge challenge = new Challenge(nameChallenge, blocks, blocksOnPlaced, typeChallenge, rewards,
                    title, items, itemsInHand, mobs, force, power, colors, causes, point, pointsBoost,
                    multiplier, boostMinutes, number, time, vehicles, sneaking, onGround,
                    pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, endTimeChallenge,
                    challengeName, quests, minutes, startTimeChallenge, keepInventory, deathInLand, worlds, itemChallenge);
            challengesEvent.put(challengeName, challenge);
        }

        Main.instance.getServer().getConsoleSender().sendMessage("§a" + folderEvent.listFiles().length + " Event Challenges loaded!");

        numberOfTop = file.getInt("Configuration.Top.NumberOfReward");
        timeBrodcastMessageTitle = file.getInt("Configuration.BroadcastMessage.TimeTitleChallenges");
        lockedInterface = file.getBoolean("Configuration.LockedInterface");
        database = file.getString("Configuration.Database");
        resetPointsAtNewChallenge = file.getBoolean("Configuration.Points.ResetPointsAtNewChallenge");
        activeOnlinePoints = file.getBoolean("Configuration.Points.OnlinePoints.Enabled");
        yesterdayTop = file.getBoolean("Configuration.Top.YesterdayTop");
        rankingReward = file.getBoolean("Configuration.Top.RankingReward");
        if (!rankingReward) {
            randomReward = file.getBoolean("Configuration.Top.RandomReward");
        }
        backupEnabled = file.getBoolean("Configuration.Backup.Enabled");
        challengeGeneration = file.getString("Configuration.ChallengeGeneration");
        numberOfFilesInFolderForBackup = file.getInt("Configuration.Backup.NumberOfFilesInFolder");
        pointsOnlinePoints = file.getInt("Configuration.Points.OnlinePoints.Point");
        minutesOnlinePoints = file.getInt("Configuration.Points.OnlinePoints.Minutes");
        minimumPoints = file.getInt("Configuration.Points.MinimumPoints");
        ArrayList<String> lore = new ArrayList<>(file.getStringList("Configuration.CollectionChallengeItem.Lore"));
        chestCollection = ItemUtils.getChest(file.getString("Configuration.CollectionChallengeItem.Type"), file.getString("Configuration.CollectionChallengeItem.Name"), lore);
        pointsResume = file.getBoolean("Configuration.Points.PointsResume");

        for (String nameInterface : file.getConfigurationSection("Interfaces").getKeys(false)) {
            String title = file.getString("Interfaces." + nameInterface + "..Title");
            String openSound = file.getString("Interfaces." + nameInterface + ".OpenSound");
            ArrayList<String> slots = new ArrayList<>();
            ArrayList<String> contaSlots = new ArrayList<>();

            HashMap<String, ItemConfig> itemsConfig = new HashMap<>();
            for (String nameItem : file.getConfigurationSection("Interfaces." + nameInterface + ".Items").getKeys(false)) {
                String letter = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".Letter");
                String type = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".Type");
                if (type.contains(";")) {
                    String[] x = type.split(";");
                    if (Material.getMaterial(x[0]) == null) {
                        Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR WITH MATERIAL " + x[0] + " IN CONFIG.YML AT LINE: Interfaces." + nameInterface + ".Items." + nameItem + ".Type"));
                        type = "DIRT";
                    }
                } else {
                    if (Material.getMaterial(type) == null) {
                        Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR WITH MATERIAL " + type + " IN CONFIG.YML AT LINE: Interfaces." + nameInterface + ".Items." + nameItem + ".Type"));
                        type = "DIRT";
                    }
                }
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

    public String getChallengeGeneration() {
        return challengeGeneration;
    }

    public void setChallengeGeneration(String challengeGeneration) {
        this.challengeGeneration = challengeGeneration;
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

    public int getNumberOfTop() {
        return numberOfTop;
    }

    public void setNumberOfTop(int numberOfTop) {
        this.numberOfTop = numberOfTop;
    }

    public boolean isRandomReward() {
        return randomReward;
    }

    public void setRandomReward(boolean randomReward) {
        this.randomReward = randomReward;
    }

    public boolean isLockedInterface() {
        return lockedInterface;
    }

    public void setLockedInterface(boolean lockedInterface) {
        this.lockedInterface = lockedInterface;
    }

    public int getMinimumPoints() {
        return minimumPoints;
    }

    public void setMinimumPoints(int minimumPoints) {
        this.minimumPoints = minimumPoints;
    }

}
