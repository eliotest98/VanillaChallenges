package io.eliotesta98.VanillaChallenges.Utils;

import com.HeroxWar.HeroxCore.MessageGesture;
import com.HeroxWar.HeroxCore.TimeGesture.Time;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Events.ApiEvents.ChallengeChangeEvent;
import io.eliotesta98.VanillaChallenges.Events.Challenges.ItemCollector.ItemCollector;
import io.eliotesta98.VanillaChallenges.Interfaces.Interface;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Challenge {

    private final Map<String, Long> players = new ConcurrentHashMap<>();
    private final Map<String, Long> min10PlayersPoints = new ConcurrentHashMap<>();
    private final Map<String, Long> boostSinglePlayers = new ConcurrentHashMap<>();
    private final Map<String, Boolean> startBoostSinglePlayers = new ConcurrentHashMap<>();
    private final Map<String, BukkitTask> tasksSinglePlayers = new ConcurrentHashMap<>();
    private List<String> worlds = new ArrayList<>();
    private String challengeName = "nessuna";
    private String itemChallenge = "BEDROCK";
    private List<String> blocks = new ArrayList<>();
    private List<String> blocksOnPlaced = new ArrayList<>();
    private String typeChallenge = "nessuna";
    private List<String> rewards = new ArrayList<>();
    private List<String> title = new ArrayList<>();
    private List<String> items = new ArrayList<>();
    private List<String> itemsInHand = new ArrayList<>();
    private List<String> mobs = new ArrayList<>();
    private List<String> colors = new ArrayList<>();
    private List<String> causes = new ArrayList<>();
    private List<String> vehicles = new ArrayList<>();
    private double force = 0.0;
    private double power = 0.0;
    private int number = 0;
    private String startTimeChallenge = "0:00";
    private String onGround = "NOBODY";
    private boolean keepInventory = false;
    // timer del salvataggio punti
    final int timeNumber = 20 * 60;
    private BukkitTask task, boostingTask;
    int point = 1;
    private int pointsBoost = 0;
    private int multiplier = 1;
    private int boostMinutes = 0;
    private int pointsBoostSinglePlayer = 0;
    private int multiplierSinglePlayer = 1;
    private int minutesSinglePlayer = 0;
    private Time timeChallenge = new Time();
    private String endTimeChallenge = "24:00";
    private String nameChallenge = "NOBODY";
    private boolean deathInLand = false;
    private long countPointsChallenge = 0;
    private boolean startBoost = false;
    private String sneaking = "NOBODY";
    private List<String> quests = new ArrayList<>();
    int minutes = 1;

    private final static Pattern blockOnPlacedPattern = Pattern.compile("\\{blockOnPlaced[0-9]}");
    private final static Pattern blockPattern = Pattern.compile("\\{block[0-9]}");
    private final static Pattern worldPattern = Pattern.compile("\\{world[0-9]}");
    private final static Pattern itemPattern = Pattern.compile("\\{item[0-9]}");
    private final static Pattern itemInHandPattern = Pattern.compile("\\{itemInHand[0-9]}");
    private final static Pattern vehiclePattern = Pattern.compile("\\{vehicle[0-9]}");
    private final static Pattern causePattern = Pattern.compile("\\{cause[0-9]}");
    private final static Pattern colorPattern = Pattern.compile("\\{color[0-9]}");
    private final static Pattern mobPattern = Pattern.compile("\\{mob[0-9]}");

    public Challenge() {

    }

    public Challenge(String nameChallenge, List<String> blocks, List<String> blocksOnPlaced, String typeChallenge,
                     List<String> rewards, List<String> title, List<String> items, List<String> itemsInHand,
                     List<String> mobs, double force, double power, List<String> colors, List<String> causes,
                     int point, int pointsBoost, int multiplier, int boostMinutes, int number, String challengeDuration,
                     List<String> vehicles, String sneaking, String onGround, int pointsBoostSinglePlayer,
                     int multiplierSinglePlayer, int minutesSinglePlayer, String endTimeChallenge, String challengeName,
                     List<String> quests, int minutes, String startTimeChallenge, boolean keepInventory,
                     boolean deathInLand, List<String> worlds, String itemChallenge) {
        this.blocks = blocks;
        this.worlds = worlds;
        this.blocksOnPlaced = blocksOnPlaced;
        this.typeChallenge = typeChallenge;
        this.rewards = rewards;
        this.title = title;
        this.items = items;
        this.itemsInHand = itemsInHand;
        this.mobs = mobs;
        this.force = force;
        this.power = power;
        this.colors = colors;
        this.causes = causes;
        this.point = point;
        this.pointsBoost = pointsBoost;
        this.multiplier = multiplier;
        this.boostMinutes = boostMinutes;
        this.number = number;
        this.vehicles = vehicles;
        this.sneaking = sneaking;
        this.onGround = onGround;
        this.pointsBoostSinglePlayer = pointsBoostSinglePlayer;
        this.minutesSinglePlayer = minutesSinglePlayer;
        this.multiplierSinglePlayer = multiplierSinglePlayer;
        this.endTimeChallenge = endTimeChallenge;
        this.challengeName = challengeName;
        this.quests = quests;
        this.minutes = minutes;
        this.startTimeChallenge = startTimeChallenge;
        this.keepInventory = keepInventory;
        this.nameChallenge = nameChallenge;
        this.deathInLand = deathInLand;
        this.itemChallenge = itemChallenge;
        timeChallenge(challengeDuration);
    }

    private void timeChallenge(String challengeDuration) {
        String typeTime = challengeDuration.charAt(challengeDuration.length() - 1) + "";
        int time = Integer.parseInt(challengeDuration.replace(typeTime, ""));

        if (time != 0) {
            switch (typeTime) {
                case "s":
                    this.timeChallenge = new Time(0, 0, 0, time, ':');
                    break;
                case "m":
                    this.timeChallenge = new Time(0, 0, time, 0, ':');
                    break;
                case "h":
                    this.timeChallenge = new Time(0, time, 0, 0, ':');
                    break;
                default:
                    this.timeChallenge = new Time(time, ':');
                    break;
            }
        } else {
            String[] startSplit = startTimeChallenge.split(":");
            String[] endSplit = endTimeChallenge.split(":");
            int hours = Integer.parseInt(endSplit[0]) - Integer.parseInt(startSplit[0]);
            int minutes = Integer.parseInt(endSplit[1]) - Integer.parseInt(startSplit[1]);
            this.timeChallenge = new Time(0, hours, minutes, 0, ':');
        }
    }

    public void addTime(Time time) {
        this.timeChallenge.sum(time);
    }

    public void removeTime(Time time) {
        this.timeChallenge.difference(time);
    }

    public Map<String, Long> getBoostSinglePlayers() {
        return boostSinglePlayers;
    }

    public int getPointsBoostSinglePlayer() {
        return pointsBoostSinglePlayer;
    }

    public int getMultiplierSinglePlayer() {
        return multiplierSinglePlayer;
    }

    public Map<String, Long> getMin10PlayersPoints() {
        return min10PlayersPoints;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        this.causes = causes;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<String> blocks) {
        this.blocks = blocks;
    }

    public List<String> getBlocksOnPlace() {
        return blocksOnPlaced;
    }

    public void setBlocksOnPlaced(List<String> blocksOnPlaced) {
        this.blocksOnPlaced = blocksOnPlaced;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public Map<String, Long> getPlayers() {
        return players;
    }

    public void setPlayers(List<Challenger> challengers) {
        for (Challenger challenger : challengers) {
            players.put(challenger.getNomePlayer(), challenger.getPoints());
        }
    }

    public boolean isDeathInLand() {
        return deathInLand;
    }

    public List<String> getQuests() {
        return quests;
    }

    public void setQuests(List<String> quests) {
        this.quests = quests;
    }

    public String getTypeChallenge() {
        return typeChallenge;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setPointsBoost(int pointsBoost) {
        this.pointsBoost = pointsBoost;
    }

    public void setPointsBoostSinglePlayer(int pointsBoostSinglePlayer) {
        this.pointsBoostSinglePlayer = pointsBoostSinglePlayer;
    }

    public Time getTimeChallenge() {
        return timeChallenge;
    }

    public void setTimeChallenge(Time timeChallenge) {
        this.timeChallenge = timeChallenge;
    }

    public String getNameChallenge() {
        return nameChallenge;
    }

    public String getItemChallenge() {
        return itemChallenge;
    }

    public void incrementCommands(String playerName, long amount) {
        if (Main.instance.getConfigGestion().getTasks().isChallengeStart()) {
            return;
        }
        if (!players.containsKey(playerName)) {
            players.put(playerName, amount);
        } else {
            players.replace(playerName, players.get(playerName) + amount);
        }
    }

    public void increment(String playerName, long amount) {
        if (Main.instance.getConfigGestion().getTasks().isChallengeStart()) {
            return;
        }
        if (!startBoostSinglePlayers.containsKey(playerName)) {
            startBoostSinglePlayers.put(playerName, false);
        }
        if (startBoost || startBoostSinglePlayers.get(playerName)) {
            if (startBoost) {
                if (!players.containsKey(playerName)) {
                    players.put(playerName, amount * multiplier);
                } else {
                    players.replace(playerName, players.get(playerName) + (amount * multiplier));
                }
                if (!min10PlayersPoints.containsKey(playerName)) {
                    min10PlayersPoints.put(playerName, amount * multiplier);
                } else {
                    min10PlayersPoints.replace(playerName, min10PlayersPoints.get(playerName) + (amount * multiplier));
                }
            }
            if (startBoostSinglePlayers.get(playerName)) {
                players.replace(playerName, players.get(playerName) + (amount * multiplierSinglePlayer));
                min10PlayersPoints.replace(playerName, min10PlayersPoints.get(playerName) + (amount * multiplierSinglePlayer));
            }
        } else {
            countPointsChallenge = countPointsChallenge + amount;
            if (!players.containsKey(playerName)) {
                players.put(playerName, amount);
            } else {
                players.replace(playerName, players.get(playerName) + amount);
            }
            if (!min10PlayersPoints.containsKey(playerName)) {
                min10PlayersPoints.put(playerName, amount);
            } else {
                min10PlayersPoints.replace(playerName, min10PlayersPoints.get(playerName) + amount);
            }
            if (!boostSinglePlayers.containsKey(playerName)) {
                boostSinglePlayers.put(playerName, amount);
            } else {
                boostSinglePlayers.replace(playerName, boostSinglePlayers.get(playerName) + amount);
            }
            checkPointsMultiplier();
            checkPointsMultiplierSinglePlayer(playerName);
        }
    }

    public void clearPlayers() {
        stopTask();
        if (boostingTask != null) {
            stopTaskBoost();
        }
        players.clear();
        min10PlayersPoints.clear();
        boostSinglePlayers.clear();
        startBoostSinglePlayers.clear();
        for (Map.Entry<String, BukkitTask> task : tasksSinglePlayers.entrySet()) {
            task.getValue().cancel();
        }
    }

    public void savePoints() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            for (Map.Entry<String, Long> player : players.entrySet()) {
                Bukkit.getScheduler().runTask(Main.instance, () -> {
                    try {
                        if (player.getValue() > 0) {
                            if (Main.db.isPresent(player.getKey())) {
                                Main.db.updateChallenger(player.getKey(), player.getValue());
                            } else {
                                Main.db.insertChallenger(player.getKey(), player.getValue());
                            }
                        }
                    } catch (Exception ex) {
                        Bukkit.getServer().getConsoleSender().sendMessage("Save Points Runtime: " + ex.getMessage());
                    }
                });
            }
            if (startBoost && (boostingTask == null || boostingTask.isCancelled())) {
                startBoosting();
            }
        }, 0, timeNumber);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(task, "SavePoints", false);
    }

    public void stopTask() {
        task.cancel();
    }

    public void stopTaskBoost() {
        boostingTask.cancel();
    }

    public long getPointFromPLayerName(String playerName) {
        return players.getOrDefault(playerName, 0L);
    }

    public List<String> getRewards() {
        return rewards;
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
    }

    public List<Challenger> getTopPlayers(int numberOfTops) {
        List<Challenger> topList = new ArrayList<>();
        try {
            for (int i = 0; i < numberOfTops; i++) {
                Challenger challenger = getChallenger(topList);
                if (!challenger.getNomePlayer().equalsIgnoreCase("Notch")) {
                    topList.add(challenger);
                } else {
                    break;
                }
            }
            return topList;
        } catch (Exception ex) {
            Bukkit.getServer().getConsoleSender().sendMessage("Top Players: " + ex.getMessage());
            return topList;
        }
    }

    private Challenger getChallenger(List<Challenger> topList) {
        Challenger challenger = new Challenger();
        for (Map.Entry<String, Long> player : players.entrySet()) {
            if (player.getValue() > challenger.getPoints()) {
                boolean trovato = false;
                for (Challenger value : topList) {
                    if (value.getNomePlayer().equalsIgnoreCase(player.getKey())) {
                        trovato = true;
                        break;
                    }
                }
                if (!trovato) {
                    challenger = new Challenger(player.getKey(), player.getValue());
                }
            }
        }
        return challenger;
    }

    public boolean isMinimumPointsReached() {
        long count = 0;
        for (long number : players.values()) {
            count = count + number;
        }
        return count >= Main.instance.getConfigGestion().getMinimumPoints();
    }

    public long getPointsRemain() {
        long count = 0;
        for (long number : players.values()) {
            count = count + number;
        }
        return Main.instance.getConfigGestion().getMinimumPoints() - count;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<String> getItemsInHand() {
        return itemsInHand;
    }

    public void setItemsInHand(List<String> itemsInHand) {
        this.itemsInHand = itemsInHand;
    }

    public List<String> getMobs() {
        return mobs;
    }

    public void setMobs(List<String> mobs) {
        this.mobs = mobs;
    }

    public int getPointsBoost() {
        return pointsBoost;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public void setMultiplierSinglePlayer(int multiplierSinglePlayer) {
        this.multiplierSinglePlayer = multiplierSinglePlayer;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getBoostMinutes() {
        return boostMinutes;
    }

    public void setBoostMinutes(int boostMinutes) {
        this.boostMinutes = boostMinutes;
    }

    public boolean isActive() {
        return startBoost || boostMinutes == 0 || multiplier == 1 || pointsBoost == 0;
    }

    public boolean isActiveSingleBoost(String playerName) {
        return startBoostSinglePlayers.getOrDefault(playerName, false);
    }

    public long getCountPointsChallenge() {
        return countPointsChallenge;
    }

    public long getCountPointsChallengeSinglePlayer(String playerName) {
        return boostSinglePlayers.getOrDefault(playerName, 0L);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<String> getVehicle() {
        return vehicles;
    }

    public void setVehicle(List<String> vehicles) {
        this.vehicles = vehicles;
    }

    public String getSneaking() {
        return sneaking;
    }

    public void setSneaking(String sneaking) {
        this.sneaking = sneaking;
    }

    public String getOnGround() {
        return onGround;
    }

    public void setOnGround(String onGround) {
        this.onGround = onGround;
    }

    public String getStartTimeChallenge() {
        return startTimeChallenge;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public void setWorlds(List<String> worlds) {
        this.worlds = worlds;
    }

    public String getEndTimeChallenge() {
        return endTimeChallenge;
    }

    public int getMinutesSinglePlayer() {
        return minutesSinglePlayer;
    }

    public void setMinutesSinglePlayer(int minutesSinglePlayer) {
        this.minutesSinglePlayer = minutesSinglePlayer;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "players=" + players +
                ", min10PlayersPoints=" + min10PlayersPoints +
                ", boostSinglePlayers=" + boostSinglePlayers +
                ", startBoostSinglePlayers=" + startBoostSinglePlayers +
                ", tasksSinglePlayers=" + tasksSinglePlayers +
                ", worlds=" + worlds +
                ", challengeName='" + challengeName + '\'' +
                ", blocks=" + blocks +
                ", blocksOnPlaced=" + blocksOnPlaced +
                ", typeChallenge='" + typeChallenge + '\'' +
                ", rewards=" + rewards +
                ", title=" + title +
                ", items=" + items +
                ", itemsInHand=" + itemsInHand +
                ", mobs=" + mobs +
                ", colors=" + colors +
                ", causes=" + causes +
                ", vehicles=" + vehicles +
                ", force=" + force +
                ", power=" + power +
                ", number=" + number +
                ", startTimeChallenge='" + startTimeChallenge + '\'' +
                ", onGround='" + onGround + '\'' +
                ", keepInventory=" + keepInventory +
                ", timeNumber=" + timeNumber +
                ", task=" + task +
                ", boostingTask=" + boostingTask +
                ", point=" + point +
                ", pointsBoost=" + pointsBoost +
                ", multiplier=" + multiplier +
                ", boostMinutes=" + boostMinutes +
                ", pointsBoostSinglePlayer=" + pointsBoostSinglePlayer +
                ", multiplierSinglePlayer=" + multiplierSinglePlayer +
                ", minutesSinglePlayer=" + minutesSinglePlayer +
                ", timeChallenge=" + timeChallenge +
                ", nameChallenge='" + nameChallenge + '\'' +
                ", deathInLand=" + deathInLand +
                ", countPointsChallenge=" + countPointsChallenge +
                ", startBoost=" + startBoost +
                ", sneaking='" + sneaking + '\'' +
                ", quests=" + quests +
                ", minutes=" + minutes +
                '}';
    }

    private void checkPointsMultiplierSinglePlayer(String playerName) {
        if (minutesSinglePlayer == 0 && multiplierSinglePlayer == 1 && pointsBoostSinglePlayer == 0) {
            return;
        }
        if (boostSinglePlayers.get(playerName) >= pointsBoostSinglePlayer) {
            boostSinglePlayers.remove(playerName);
            startBoostSinglePlayers.replace(playerName, true);
            startBoostingSinglePlayer(playerName);
        }
    }

    private void startBoostingSinglePlayer(String playerName) {
        BukkitTask boostSingleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            int number = minutesSinglePlayer;

            @Override
            public void run() {
                number--;
                if (number <= 0) {
                    startBoostSinglePlayers.remove(playerName);
                    tasksSinglePlayers.get(playerName).cancel();
                    tasksSinglePlayers.remove(playerName);
                }
                Player p = Bukkit.getPlayer(playerName);
                if (p != null) {
                    MessageGesture.sendMessage(p, Main.instance.getConfigGestion().getMessages().get("BoostMessageSinglePlayer").replace("{number}", multiplierSinglePlayer + "").replace("{minutes}", number + ""));
                }
            }
        }, 0, timeNumber);
        tasksSinglePlayers.put(playerName, boostSingleTask);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(boostSingleTask, "SingleBoost", false);
    }

    private void checkPointsMultiplier() {
        if (boostMinutes == 0 && multiplier == 1 && pointsBoost == 0) {
            return;
        }
        if (countPointsChallenge >= pointsBoost) {
            countPointsChallenge = countPointsChallenge - pointsBoost;
            startBoost = true;
        }
    }

    private void startBoosting() {
        boostingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            int number = boostMinutes;

            @Override
            public void run() {
                number--;
                if (number <= 0) {
                    startBoost = false;
                    boostingTask.cancel();
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    MessageGesture.sendMessage(p, Main.instance.getConfigGestion().getMessages().get("BoostMessage").replace("{number}", multiplier + "").replace("{minutes}", number + ""));
                }
            }
        }, 0, timeNumber);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(boostingTask, "GlobalBoost", false);
    }

    public void message(CommandSender sender) {
        List<String> title = new ArrayList<>(this.title);
        for (int i = 0; i < title.size(); i++) {
            Matcher matcherBlockPlaced = blockOnPlacedPattern.matcher(title.get(i));
            while (matcherBlockPlaced.find()) {
                String number = matcherBlockPlaced.group().replace("{blockOnPlaced", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= blocksOnPlaced.size()) {
                    title.set(i,
                            title.get(i).replace("{blockOnPlaced" + number + "}",
                                    blocksOnPlaced.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{blockOnPlaced" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {blockOnPlaced" + number + "}");
                }
            }
            Matcher matcherBlock = blockPattern.matcher(title.get(i));
            while (matcherBlock.find()) {
                String number = matcherBlock.group().replace("{block", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= blocks.size()) {
                    title.set(i,
                            title.get(i).replace("{block" + number + "}",
                                    blocks.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{block" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {block" + number + "}");
                }
            }
            Matcher matcherWorld = worldPattern.matcher(title.get(i));
            while (matcherWorld.find()) {
                String number = matcherWorld.group().replace("{world", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= worlds.size()) {
                    title.set(i,
                            title.get(i).replace("{world" + number + "}",
                                    worlds.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{world" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {world" + number + "}");
                }
            }
            Matcher matcherItem = itemPattern.matcher(title.get(i));
            while (matcherItem.find()) {
                String number = matcherItem.group().replace("{item", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= items.size()) {
                    title.set(i,
                            title.get(i).replace("{item" + number + "}",
                                    items.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{item" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {item" + number + "}");
                }
            }
            Matcher matcherItemInHand = itemInHandPattern.matcher(title.get(i));
            while (matcherItemInHand.find()) {
                String number = matcherItemInHand.group().replace("{itemInHand", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= itemsInHand.size()) {
                    title.set(i,
                            title.get(i).replace("{itemInHand" + number + "}",
                                    itemsInHand.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{itemInHand" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {itemInHand" + number + "}");
                }
            }
            Matcher matcherVehicle = vehiclePattern.matcher(title.get(i));
            while (matcherVehicle.find()) {
                String number = matcherVehicle.group().replace("{vehicle", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= vehicles.size()) {
                    title.set(i,
                            title.get(i).replace("{vehicle" + number + "}",
                                    vehicles.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{vehicle" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {vehicle" + number + "}");
                }
            }
            Matcher causeVehicle = causePattern.matcher(title.get(i));
            while (causeVehicle.find()) {
                String number = causeVehicle.group().replace("{cause", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= causes.size()) {
                    title.set(i,
                            title.get(i).replace("{cause" + number + "}",
                                    causes.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{cause" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {cause" + number + "}");
                }
            }
            Matcher matcherColor = colorPattern.matcher(title.get(i));
            while (matcherColor.find()) {
                String number = matcherColor.group().replace("{color", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= colors.size()) {
                    title.set(i,
                            title.get(i).replace("{color" + number + "}",
                                    colors.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{color" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {color" + number + "}");
                }
            }
            Matcher matcherMob = mobPattern.matcher(title.get(i));
            while (matcherMob.find()) {
                String number = matcherMob.group().replace("{mob", "").replace("}", "");
                int numberInt = Integer.parseInt(number);
                if (numberInt <= mobs.size()) {
                    title.set(i,
                            title.get(i).replace("{mob" + number + "}",
                                    mobs.get(Integer.parseInt(number) - 1)));
                } else {
                    title.set(i,
                            title.get(i).replace("{mob" + number + "}", "&cDELETE PLACEHOLDER"));
                    MessageGesture.sendMessage(sender, "&c&o&lERROR WITH PLACEHOLDER {mob" + number + "}");
                }
            }
        }
        for (String s : title) {
            MessageGesture.sendMessage(sender, s
                    .replace("{hours}", getTimeChallenge().getHours() + "")
                    .replace("{minutes}", getTimeChallenge().getMinutes() + "")
                    .replace("{seconds}", getTimeChallenge().getSeconds() + "")
                    .replace("{points}", point + "")
                    .replace("{slots}", number + "")
                    .replace("{minutes}", minutes + "")
                    .replace("{challengeName}", nameChallenge)
                    .replace("{sneaking}", sneaking)
                    .replace("{force}", force + "")
                    .replace("{power}", power + "")
                    .replace("{onGround}", onGround)
                    .replace("{keepInventory}", keepInventory + "")
            );
        }
    }

    public void nextChallenge(boolean resetPoints, boolean rankingReward, boolean randomReward, int numberOfRewardedPlayer, int numberOfTop, String reason, boolean skipPeacefulTime) {
        ChallengeChangeEvent challengeChangeEvent = new ChallengeChangeEvent(reason, this);
        Bukkit.getPluginManager().callEvent(challengeChangeEvent);
        if (challengeChangeEvent.isCancelled()) {
            return;
        }

        List<Challenger> topPlayers = Main.instance.getDailyChallenge().getTopPlayers(numberOfRewardedPlayer);

        if (!Main.db.checkIfEventChallenge()) {
            Main.db.deleteChallengeWithName(Main.instance.getDailyChallenge().getChallengeName());
        } else {
            Main.db.deleteChallengeWithName("Event_" + Main.instance.getDailyChallenge().getChallengeName());
        }
        Main.db.removeTopYesterday();
        Main.db.saveTopYesterday(Main.instance.getDailyChallenge().getTopPlayers(numberOfTop));
        if (Main.instance.getDailyChallenge().getTypeChallenge().equalsIgnoreCase("ItemCollectionChallenge")) {
            ItemCollector.deleteDb();
        }
        if (Main.instance.getConfigGestion().isBackupEnabled()) {
            Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
        }
        int number = Main.db.lastDailyWinnerId();
        Random random = new Random();
        if (Main.instance.getDailyChallenge().isMinimumPointsReached()) {
            for (int z = 0; z < topPlayers.size(); z++) {
                int placeInTop = z;
                int rewardsSize = Main.instance.getDailyChallenge().getRewards().size();
                if (z >= rewardsSize) {
                    placeInTop = rewardsSize - 1;
                }

                // Player Stat section
                if (Main.db.isPlayerHaveStats(topPlayers.get(z).getNomePlayer())) {
                    PlayerStats playerStats = Main.db.getStatsPlayer(topPlayers.get(z).getNomePlayer());
                    playerStats.setNumberOfVictories(playerStats.getNumberOfVictories() + 1);
                    if (z == 0) {
                        playerStats.setNumberOfFirstPlace(playerStats.getNumberOfFirstPlace() + 1);
                    } else if (z == 1) {
                        playerStats.setNumberOfSecondPlace(playerStats.getNumberOfSecondPlace() + 1);
                    } else if (z == 2) {
                        playerStats.setNumberOfThirdPlace(playerStats.getNumberOfThirdPlace() + 1);
                    }
                    Main.db.updatePlayerStat(playerStats);
                } else {
                    PlayerStats playerStats = new PlayerStats();
                    playerStats.setPlayerName(topPlayers.get(z).getNomePlayer());
                    playerStats.setNumberOfVictories(1);
                    if (z == 0) {
                        playerStats.setNumberOfFirstPlace(1);
                    } else if (z == 1) {
                        playerStats.setNumberOfSecondPlace(1);
                    } else if (z == 2) {
                        playerStats.setNumberOfThirdPlace(1);
                    }
                    Main.db.insertPlayerStat(playerStats);
                }
                number++;

                //Register Winner
                DailyWinner dailyWinner = new DailyWinner();
                dailyWinner.setPlayerName(topPlayers.get(z).getNomePlayer());
                dailyWinner.setNomeChallenge(Main.instance.getDailyChallenge().getChallengeName());

                if (rankingReward) {
                    dailyWinner.setId(number);
                    dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(placeInTop));
                    Main.db.insertDailyWinner(dailyWinner);
                } else {
                    if (randomReward) {
                        dailyWinner.setId(number);
                        dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(random.nextInt(rewardsSize)));
                        Main.db.insertDailyWinner(dailyWinner);
                    } else {
                        for (int i = 0; i < rewardsSize; i++) {
                            dailyWinner.setId(number);
                            dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(i));
                            Main.db.insertDailyWinner(dailyWinner);
                            number++;
                        }
                    }
                }
            }
        }
        if (resetPoints) {
            Main.db.clearChallengers();
            Main.instance.getDailyChallenge().clearPlayers();
        }
        if (Main.challengeSelected) {
            for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                interfaces.getValue().closeAllInventories();
            }
        }
        Main.instance.unregisterCurrentListener();
        Main.instance.getConfigGestion().getTasks().stopAllTasks();
        if (Main.instance.getConfigGestion().getCooldown().getMilliseconds() > 0 && !skipPeacefulTime) {
            Main.db.setPeacefulTime(Main.instance.getConfigGestion().getCooldown().cloneTime());
        }
        Main.instance.pluginStartingProcess();
    }

    public Challenge cloneChallenge(String challengeDuration) {
        return new Challenge(nameChallenge, blocks, blocksOnPlaced, typeChallenge, rewards,
                title, items, itemsInHand, mobs, force, power, colors, causes, point, pointsBoost,
                multiplier, boostMinutes, number, challengeDuration, vehicles, sneaking, onGround,
                pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, endTimeChallenge,
                challengeName, quests, minutes, startTimeChallenge, keepInventory, deathInLand, worlds, itemChallenge);
    }

    public Challenge cloneChallenge() {
        return new Challenge(nameChallenge, blocks, blocksOnPlaced, typeChallenge, rewards,
                title, items, itemsInHand, mobs, force, power, colors, causes, point, pointsBoost,
                multiplier, boostMinutes, number, "0t", vehicles, sneaking, onGround,
                pointsBoostSinglePlayer, multiplierSinglePlayer, minutesSinglePlayer, endTimeChallenge,
                challengeName, quests, minutes, startTimeChallenge, keepInventory, deathInLand, worlds, itemChallenge);
    }
}
