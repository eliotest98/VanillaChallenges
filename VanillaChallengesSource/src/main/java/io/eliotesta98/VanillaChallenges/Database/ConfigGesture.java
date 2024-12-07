package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Interfaces.Interface;
import io.eliotesta98.VanillaChallenges.Interfaces.ItemConfig;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;

public class ConfigGesture {

    private Map<String, Boolean> debug = new HashMap<>();
    private final Map<String, String> messages = new HashMap<>();
    private Map<String, Challenge> challenges = new HashMap<>();
    private final Map<String, Challenge> challengesEvent = new HashMap<>();
    private final Map<String, Boolean> hooks = new HashMap<>();
    private final Map<String, Interface> interfaces = new HashMap<>();
    private boolean activeOnlinePoints, rankingReward, yesterdayTop, resetPointsAtNewChallenge,
            backupEnabled, pointsResume, lockedInterface, randomReward = false;
    private String challengeGeneration, url, username, password, mySqlPrefix, database;
    private int timeBroadcastMessageTitle, pointsOnlinePoints, minutesOnlinePoints, numberOfFilesInFolderForBackup,
            numberOfRewardPlayer, minimumPoints, number, time, numberOfTop;
    private ItemStack chestCollection;
    private final Tasks tasks = new Tasks();
    private final List<String> controlIfChallengeExist = new ArrayList<>();

    public ConfigGesture(FileConfiguration file) throws IOException {

        ConfigurationSection section = file.getConfigurationSection("Debug");
        if (section == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR with Debug configuration section, please refresh the " + file.getName() + " file!"));
            return;
        }
        for (String event : section.getKeys(false)) {
            debug.put(event, file.getBoolean("Debug." + event));
        }
        section = file.getConfigurationSection("Messages");
        if (section == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR with Messages configuration section, please refresh the " + file.getName() + " file!"));
            return;
        }
        for (String message : section.getKeys(false)) {
            if (message.equalsIgnoreCase("Commands") || message.equalsIgnoreCase("Errors")
                    || message.equalsIgnoreCase("Success") || message.equalsIgnoreCase("Lists")
                    || message.equalsIgnoreCase("Points")) {
                section = file.getConfigurationSection("Messages." + message);
                if (section == null) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR with Messages configuration section, please refresh the " + file.getName() + " file!"));
                    return;
                }
                for (String command : section.getKeys(false)) {
                    messages.put(message + "." + command, file.getString("Messages." + message + "." + command, "").replace("{prefix}", messages.get("Prefix")));
                }
            } else if (message.equalsIgnoreCase("Prefix")) {
                messages.put(message, file.getString("Messages." + message));
            } else if (message.equalsIgnoreCase("TopPlayers")) {
                List<String> topMessages = file.getStringList("Messages.TopPlayers");
                numberOfTop = topMessages.size();
                int i = 1;
                while (!topMessages.isEmpty()) {
                    messages.put("topPlayers" + i, topMessages.get(0));
                    topMessages.remove(0);
                    i++;
                }
            } else {
                messages.put(message, file.getString("Messages." + message, "").replace("{prefix}", messages.get("Prefix")));
            }
        }
        section = file.getConfigurationSection("Configuration.Hooks");
        if (section == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR with Hooks configuration section, please refresh the " + file.getName() + " file!"));
            return;
        }
        for (String hook : section.getKeys(false)) {
            hooks.put(hook, file.getBoolean("Configuration.Hooks." + hook));
        }

        String challengeRegenerationType = file.getString("Configuration.ChallengeRegeneration.Type", "Blacklist");
        List<String> regenerationFilesGlobalChallenges = file.getStringList("Configuration.ChallengeRegeneration.Globals");
        List<String> regenerationFilesEventChallenges = file.getStringList("Configuration.ChallengeRegeneration.Events");

        FileCreator.addFiles(hooks);

        boolean folderCreate = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges").mkdir();

        File[] listOfChallengesGlobalFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Global").listFiles();
        File[] listOfChallengesEventFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Event").listFiles();

        if (folderCreate) {
            FileCreator.createAllFiles("Global");
            listOfChallengesGlobalFiles = new File(Main.instance.getDataFolder() +
                    File.separator + "Challenges" + File.separator + "Global").listFiles();
        } else {
            FileCreator.controlFiles("Global", listOfChallengesGlobalFiles, regenerationFilesGlobalChallenges, challengeRegenerationType);
        }

        if (listOfChallengesGlobalFiles == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR with Challenge Files, please refresh all files!"));
            return;
        }
        for (File fileChallenge : listOfChallengesGlobalFiles) {
            String splits = "bho";
            String[] strings = splits.split(":");
            String configName = "Challenges/Global/" + fileChallenge.getName();
            CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(fileChallenge);
            cfg.syncWithConfig(fileChallenge, Main.instance.getResource(configName), strings);

            YamlConfiguration yamlChallenge = YamlConfiguration.loadConfiguration(fileChallenge);
            String challengeName = fileChallenge.getName().replace(".yml", "");
            boolean enabled = yamlChallenge.getBoolean(challengeName + ".Enabled");
            if (!enabled) {
                controlIfChallengeExist.add(challengeName);
                continue;
            }
            List<String> worlds = yamlChallenge.getStringList(challengeName + ".Worlds");
            List<String> blocks = yamlChallenge.getStringList(challengeName + ".Blocks");
            if (blocks.contains("RANDOM")) {
                blocks.remove(blocks.size() - 1);
                Collections.shuffle(blocks);
                String block = blocks.get(0);
                blocks.clear();
                blocks.add(block);
            }
            List<String> blocksOnPlaced = yamlChallenge.getStringList(challengeName + ".BlocksOnPlaced");
            if (blocksOnPlaced.contains("RANDOM")) {
                blocksOnPlaced.remove(blocksOnPlaced.size() - 1);
                Collections.shuffle(blocksOnPlaced);
                String block = blocksOnPlaced.get(0);
                blocksOnPlaced.clear();
                blocksOnPlaced.add(block);
            }
            String typeChallenge = yamlChallenge.getString(challengeName + ".TypeChallenge");
            String nameChallenge = yamlChallenge.getString(challengeName + ".NameChallenge");
            String endTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.End", "24:00");
            int end;
            if (endTimeChallenge.equalsIgnoreCase("Random")) {
                Random random = new Random();
                end = random.nextInt(24) + 1;
                endTimeChallenge = end + ":00";
            } else {
                end = Integer.parseInt(endTimeChallenge.split(":")[0]);
            }
            String startTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.Start", "00:00");
            int start;
            if (startTimeChallenge.equalsIgnoreCase("Random")) {
                Random random = new Random();
                start = random.nextInt(end) + 1;
                startTimeChallenge = start + ":00";
            }
            List<String> rewards = yamlChallenge.getStringList(challengeName + ".Rewards");
            List<String> title = yamlChallenge.getStringList(challengeName + ".Title");
            List<String> items = yamlChallenge.getStringList(challengeName + ".Items");
            if (items.contains("RANDOM")) {
                items.remove(items.size() - 1);
                Collections.shuffle(items);
                String block = items.get(0);
                items.clear();
                items.add(block);
            }
            List<String> mobs = yamlChallenge.getStringList(challengeName + ".Mobs");
            if (mobs.contains("RANDOM")) {
                mobs.remove(mobs.size() - 1);
                Collections.shuffle(mobs);
                String block = mobs.get(0);
                mobs.clear();
                mobs.add(block);
            }
            List<String> itemsInHand = yamlChallenge.getStringList(challengeName + ".ItemsInHand");
            if (itemsInHand.contains("RANDOM")) {
                itemsInHand.remove(itemsInHand.size() - 1);
                Collections.shuffle(itemsInHand);
                String block = itemsInHand.get(0);
                itemsInHand.clear();
                itemsInHand.add(block);
            }
            double force = yamlChallenge.getDouble(challengeName + ".Force");
            double power = yamlChallenge.getDouble(challengeName + ".Power");
            List<String> colors = yamlChallenge.getStringList(challengeName + ".Colors");
            if (colors.contains("RANDOM")) {
                colors.remove(colors.size() - 1);
                Collections.shuffle(colors);
                String block = colors.get(0);
                colors.clear();
                colors.add(block);
            }
            List<String> causes = yamlChallenge.getStringList(challengeName + ".Causes");
            if (causes.contains("RANDOM")) {
                causes.remove(causes.size() - 1);
                Collections.shuffle(causes);
                String block = causes.get(0);
                causes.clear();
                causes.add(block);
            }
            List<String> vehicles = yamlChallenge.getStringList(challengeName + ".Vehicles");
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
            if (yamlChallenge.getBoolean(challengeName + ".Boost.Enabled", false)) {
                pointsBoost = yamlChallenge.getInt(challengeName + ".Boost.Points");
                multiplier = yamlChallenge.getInt(challengeName + ".Boost.Multiplier");
                boostMinutes = yamlChallenge.getInt(challengeName + ".Boost.Minutes");
            }
            int pointsBoostSinglePlayer = 0;
            int multiplierSinglePlayer = 1;
            int minutesSinglePlayer = 0;
            if (yamlChallenge.getBoolean(challengeName + ".BoostPlayer.Enabled", false)) {
                pointsBoostSinglePlayer = yamlChallenge.getInt(challengeName + ".BoostPlayer.Points");
                multiplierSinglePlayer = yamlChallenge.getInt(challengeName + ".BoostPlayer.Multiplier");
                minutesSinglePlayer = yamlChallenge.getInt(challengeName + ".BoostPlayer.Minutes");
            }
            String sneaking = (yamlChallenge.getString(challengeName + ".Sneaking") == null) ? "NOBODY" : yamlChallenge.getString(challengeName + ".Sneaking");
            String onGround = (yamlChallenge.getString(challengeName + ".OnGround") == null) ? "NOBODY" : yamlChallenge.getString(challengeName + ".OnGround");
            List<String> quests = new ArrayList<>();
            for (String quest : yamlChallenge.getStringList(challengeName + ".Strings.Quests")) {
                quests.add(quest.replace("{prefix}", messages.get("Prefix")));
            }
            if (quests.isEmpty()) {
                quests.add("Formatter:" + yamlChallenge.getString(challengeName + ".Strings.StringFormatter"));
            }
            boolean keepInventory = yamlChallenge.getBoolean(challengeName + ".KeepInventory", true);
            boolean deathInLand = yamlChallenge.getBoolean(challengeName + ".DeathInLand", true);
            String itemChallenge = yamlChallenge.getString(challengeName + ".ItemChallenge", "DIRT");
            Challenge challenge = new Challenge(nameChallenge, blocks, blocksOnPlaced, typeChallenge, rewards,
                    title, items, itemsInHand, mobs, force, power, colors, causes, point, pointsBoost,
                    multiplier, boostMinutes, number, time, vehicles, sneaking, onGround,
                    pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, endTimeChallenge,
                    challengeName, quests, minutes, startTimeChallenge, keepInventory, deathInLand, worlds, itemChallenge);
            challenges.put(challengeName, challenge);
        }

        Main.instance.getServer().getConsoleSender().sendMessage("§a" + listOfChallengesGlobalFiles.length + " Global Challenges loaded!");

        if (folderCreate) {
            FileCreator.createAllFiles("Event");
            listOfChallengesEventFiles = new File(Main.instance.getDataFolder() +
                    File.separator + "Challenges" + File.separator + "Event").listFiles();
        } else {
            FileCreator.controlFiles("Event", listOfChallengesEventFiles, regenerationFilesEventChallenges, challengeRegenerationType);
        }

        if (listOfChallengesEventFiles == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR with Challenge Files, please refresh all files!"));
            return;
        }
        for (File fileChallenge : listOfChallengesEventFiles) {
            String splits = "bho";
            String[] strings = splits.split(":");
            String configName = "Challenges/Event/" + fileChallenge.getName();
            CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(fileChallenge);
            cfg.syncWithConfig(fileChallenge, Main.instance.getResource(configName), strings);

            YamlConfiguration yamlChallenge = YamlConfiguration.loadConfiguration(fileChallenge);
            String challengeName = fileChallenge.getName().replace(".yml", "");
            List<String> worlds = yamlChallenge.getStringList(challengeName + ".Worlds");
            List<String> blocks = yamlChallenge.getStringList(challengeName + ".Blocks");
            if (blocks.contains("RANDOM")) {
                blocks.remove(blocks.size() - 1);
                Collections.shuffle(blocks);
                String block = blocks.get(0);
                blocks.clear();
                blocks.add(block);
            }
            List<String> blocksOnPlaced = yamlChallenge.getStringList(challengeName + ".BlocksOnPlaced");
            if (blocksOnPlaced.contains("RANDOM")) {
                blocksOnPlaced.remove(blocksOnPlaced.size() - 1);
                Collections.shuffle(blocksOnPlaced);
                String block = blocksOnPlaced.get(0);
                blocksOnPlaced.clear();
                blocksOnPlaced.add(block);
            }
            String typeChallenge = yamlChallenge.getString(challengeName + ".TypeChallenge");
            String nameChallenge = yamlChallenge.getString(challengeName + ".NameChallenge");
            String endTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.End", "24:00");
            int end;
            if (endTimeChallenge.equalsIgnoreCase("Random")) {
                Random random = new Random();
                end = random.nextInt(24) + 1;
                endTimeChallenge = end + ":00";
            } else {
                end = Integer.parseInt(endTimeChallenge.split(":")[0]);
            }
            String startTimeChallenge = yamlChallenge.getString(challengeName + ".TimeSettings.Start", "00:00");
            int start;
            if (startTimeChallenge.equalsIgnoreCase("Random")) {
                Random random = new Random();
                start = random.nextInt(end) + 1;
                startTimeChallenge = start + ":00";
            }
            List<String> rewards = yamlChallenge.getStringList(challengeName + ".Rewards");
            List<String> title = yamlChallenge.getStringList(challengeName + ".Title");
            List<String> items = yamlChallenge.getStringList(challengeName + ".Items");
            if (items.contains("RANDOM")) {
                items.remove(items.size() - 1);
                Collections.shuffle(items);
                String block = items.get(0);
                items.clear();
                items.add(block);
            }
            List<String> mobs = yamlChallenge.getStringList(challengeName + ".Mobs");
            if (mobs.contains("RANDOM")) {
                mobs.remove(mobs.size() - 1);
                Collections.shuffle(mobs);
                String block = mobs.get(0);
                mobs.clear();
                mobs.add(block);
            }
            List<String> itemsInHand = yamlChallenge.getStringList(challengeName + ".ItemsInHand");
            if (itemsInHand.contains("RANDOM")) {
                itemsInHand.remove(itemsInHand.size() - 1);
                Collections.shuffle(itemsInHand);
                String block = itemsInHand.get(0);
                itemsInHand.clear();
                itemsInHand.add(block);
            }
            double force = yamlChallenge.getDouble(challengeName + ".Force");
            double power = yamlChallenge.getDouble(challengeName + ".Power");
            List<String> colors = yamlChallenge.getStringList(challengeName + ".Colors");
            if (colors.contains("RANDOM")) {
                colors.remove(colors.size() - 1);
                Collections.shuffle(colors);
                String block = colors.get(0);
                colors.clear();
                colors.add(block);
            }
            List<String> causes = yamlChallenge.getStringList(challengeName + ".Causes");
            if (causes.contains("RANDOM")) {
                causes.remove(causes.size() - 1);
                Collections.shuffle(causes);
                String block = causes.get(0);
                causes.clear();
                causes.add(block);
            }
            List<String> vehicles = yamlChallenge.getStringList(challengeName + ".Vehicles");
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

            List<String> quests = new ArrayList<>();
            for (String quest : yamlChallenge.getStringList(challengeName + ".Strings.Quests")) {
                quests.add(quest.replace("{prefix}", messages.get("Prefix")));
            }
            if (quests.isEmpty()) {
                quests.add("Formatter:" + yamlChallenge.getString(challengeName + ".Strings.StringFormatter"));
            }
            boolean keepInventory = yamlChallenge.getBoolean(challengeName + ".KeepInventory");
            boolean deathInLand = yamlChallenge.getBoolean(challengeName + ".DeathInLand");
            String itemChallenge = yamlChallenge.getString(challengeName + ".ItemChallenge", "DIRT");
            Challenge challenge = new Challenge(nameChallenge, blocks, blocksOnPlaced, typeChallenge, rewards,
                    title, items, itemsInHand, mobs, force, power, colors, causes, point, pointsBoost,
                    multiplier, boostMinutes, number, time, vehicles, sneaking, onGround,
                    pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, endTimeChallenge,
                    challengeName, quests, minutes, startTimeChallenge, keepInventory, deathInLand, worlds, itemChallenge);
            challengesEvent.put(challengeName, challenge);
        }

        Main.instance.getServer().getConsoleSender().sendMessage("§a" + listOfChallengesEventFiles.length + " Event Challenges loaded!");

        numberOfRewardPlayer = file.getInt("Configuration.Top.NumberOfReward");
        timeBroadcastMessageTitle = file.getInt("Configuration.BroadcastMessage.TimeTitleChallenges");
        lockedInterface = file.getBoolean("Configuration.LockedInterface");
        database = file.getString("Configuration.Database");
        mySqlPrefix = file.getString("Configuration.MySql.Prefix");
        url = file.getString("Configuration.MySql.Url");
        username = file.getString("Configuration.MySql.Username");
        password = file.getString("Configuration.MySql.Password");
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
        List<String> lore = file.getStringList("Configuration.CollectionChallengeItem.Lore");
        try {
            chestCollection = ItemUtils.getChest(file.getString("Configuration.CollectionChallengeItem.Type"), file.getString("Configuration.CollectionChallengeItem.Name"), lore);
        } catch (ExceptionInInitializerError ignore) {
            Main.instance.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lNBTItem initialization error! The plugin not work property because NbtApi not support this version! Sometimes is for the newest minecraft version, please use an old one!"));
        }
        pointsResume = file.getBoolean("Configuration.Points.PointsResume");

        section = file.getConfigurationSection("Interfaces");
        if (section == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR with Interfaces configuration section, please refresh the " + file.getName() + " file!"));
            return;
        }
        for (String nameInterface : section.getKeys(false)) {
            String title = file.getString("Interfaces." + nameInterface + "..Title");
            String openSound = file.getString("Interfaces." + nameInterface + ".OpenSound");
            List<String> slots = new ArrayList<>();
            List<String> contaSlots = new ArrayList<>();

            Map<String, ItemConfig> itemsConfig = new HashMap<>();
            section = file.getConfigurationSection("Interfaces." + nameInterface + ".Items");
            if (section == null) {
                Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR with Interfaces configuration section, please refresh the " + file.getName() + " file!"));
                return;
            }
            for (String nameItem : section.getKeys(false)) {
                String letter = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".Letter");
                String type = file.getString("Interfaces." + nameInterface + ".Items." + nameItem + ".Type", "DIRT");
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
                ItemConfig item = new ItemConfig(nameItem, name, type, texture, file.getStringList("Interfaces." + nameInterface + ".Items." + nameItem + ".Lore"), soundClick);
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

    public Map<String, Boolean> getDebug() {
        return debug;
    }

    public void setDebug(Map<String, Boolean> debug) {
        this.debug = debug;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public Map<String, Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(Map<String, Challenge> challenges) {
        this.challenges = challenges;
    }

    public int getTimeBroadcastMessageTitle() {
        return timeBroadcastMessageTitle;
    }

    public Map<String, Boolean> getHooks() {
        return hooks;
    }

    public boolean isActiveOnlinePoints() {
        return activeOnlinePoints;
    }

    public int getPointsOnlinePoints() {
        return pointsOnlinePoints;
    }

    public int getMinutesOnlinePoints() {
        return minutesOnlinePoints;
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

    public boolean isResetPointsAtNewChallenge() {
        return resetPointsAtNewChallenge;
    }

    public ItemStack getChestCollection() {
        return chestCollection;
    }

    public boolean isBackupEnabled() {
        return backupEnabled;
    }

    public int getNumberOfFilesInFolderForBackup() {
        return numberOfFilesInFolderForBackup;
    }

    public String getMySqlPrefix() {
        return mySqlPrefix;
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

    public Tasks getTasks() {
        return tasks;
    }

    public boolean isPointsResume() {
        return pointsResume;
    }

    public List<String> getControlIfChallengeExist() {
        return controlIfChallengeExist;
    }

    public Map<String, Interface> getInterfaces() {
        return interfaces;
    }

    public Map<String, Challenge> getChallengesEvent() {
        return challengesEvent;
    }

    public boolean isRankingReward() {
        return rankingReward;
    }

    public int getNumberOfRewardPlayer() {
        return numberOfRewardPlayer;
    }

    public boolean isRandomReward() {
        return randomReward;
    }

    public boolean isLockedInterface() {
        return lockedInterface;
    }

    public int getMinimumPoints() {
        return minimumPoints;
    }

    public int getNumberOfTop() {
        return numberOfTop;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
