package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Challenge {

    private HashMap<String, Long> players = new HashMap<String, Long>();
    private HashMap<String, Long> min10PlayersPoints = new HashMap<String, Long>();
    private HashMap<String, Long> boostSinglePlayers = new HashMap<String, Long>();
    private HashMap<String, Boolean> startBoostSinglePlayers = new HashMap<String, Boolean>();
    private HashMap<String, BukkitTask> tasksSinglePlayers = new HashMap<String, BukkitTask>();
    private ArrayList<String> worlds = new ArrayList<>();
    private String challengeName = "nessuna";
    private String itemChallenge = "BEDROCK";
    private ArrayList<String> blocks = new ArrayList<>();
    private ArrayList<String> blocksOnPlaced = new ArrayList<>();
    private String typeChallenge = "nessuna";
    private ArrayList<String> rewards = new ArrayList<>();
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> itemsInHand = new ArrayList<>();
    private ArrayList<String> mobs = new ArrayList<>();
    private ArrayList<String> colors = new ArrayList<>();
    private ArrayList<String> causes = new ArrayList<>();
    private ArrayList<String> vehicles = new ArrayList<>();
    private double force = 0.0;
    private double power = 0.0;
    private int number = 0;
    private int time = 1;
    private String startTimeChallenge = "0:00";
    private String onGround = "NOBODY";
    private boolean keepInventory = false;
    // timer del salvataggio punti
    int timeNumber = 20 * 60;
    private BukkitTask task, boostingTask;
    int point = 1;
    private int pointsBoost = 0;
    private int multiplier = 1;
    private int boostMinutes = 0;
    private int pointsBoostSinglePlayer = 0;
    private int multiplierSinglePlayer = 1;
    private int minutesSinglePlayer = 0;
    private int timeChallenge = 24;
    private String endTimeChallenge = "24:00";
    private String nameChallenge = "NOBODY";
    private boolean deathInLand = false;
    private long countPointsChallenge = 0;
    private boolean startBoost = false;
    private String sneaking = "NOBODY";
    private ArrayList<String> quests = new ArrayList<>();
    int minutes = 1;

    private HashMap<String, Long> tempPoints = new HashMap<>();

    private final static Pattern blockOnPlacedPattern = Pattern.compile("\\{blockOnPlaced[0-9]\\}");
    private final static Pattern blockPattern = Pattern.compile("\\{block[0-9]\\}");
    private final static Pattern worldPattern = Pattern.compile("\\{world[0-9]\\}");
    private final static Pattern itemPattern = Pattern.compile("\\{item[0-9]\\}");
    private final static Pattern itemInHandPattern = Pattern.compile("\\{itemInHand[0-9]\\}");
    private final static Pattern vehiclePattern = Pattern.compile("\\{vehicle[0-9]\\}");
    private final static Pattern causePattern = Pattern.compile("\\{cause[0-9]\\}");
    private final static Pattern colorPattern = Pattern.compile("\\{color[0-9]\\}");
    private final static Pattern mobPattern = Pattern.compile("\\{mob[0-9]\\}");

    public Challenge() {

    }

    public Challenge(String nameChallenge, ArrayList<String> blocks, ArrayList<String> blocksOnPlaced, String typeChallenge,
                     ArrayList<String> rewards, ArrayList<String> title, ArrayList<String> items, ArrayList<String> itemsInHand,
                     ArrayList<String> mobs, double force, double power, ArrayList<String> colors, ArrayList<String> causes,
                     int point, int pointsBoost, int multiplier, int boostMinutes, int number, int time,
                     ArrayList<String> vehicles, String sneaking, String onGround, int pointsBoostSinglePlayer,
                     int multiplierSinglePlayer, int minutesSinglePlayer, String endTimeChallenge, String challengeName,
                     ArrayList<String> quests, int minutes, String startTimeChallenge, boolean keepInventory,
                     boolean deathInLand, ArrayList<String> worlds, String itemChallenge) {
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
        this.time = time;
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
        timeChallenge();
    }

    private void timeChallenge() {
        String[] startSplit = startTimeChallenge.split(":");
        String[] endSplit = endTimeChallenge.split(":");
        timeChallenge = Integer.parseInt(endSplit[0]) - Integer.parseInt(startSplit[0]);
    }

    public HashMap<String, Long> getBoostSinglePlayers() {
        return boostSinglePlayers;
    }

    public void setBoostSinglePlayers(HashMap<String, Long> boostSinglePlayers) {
        this.boostSinglePlayers = boostSinglePlayers;
    }

    public int getPointsBoostSinglePlayer() {
        return pointsBoostSinglePlayer;
    }

    public void setPointsBoostSinglePlayer(int pointsBoostSinglePlayer) {
        this.pointsBoostSinglePlayer = pointsBoostSinglePlayer;
    }

    public int getMultiplierSinglePlayer() {
        return multiplierSinglePlayer;
    }

    public void setMultiplierSinglePlayer(int multiplierSinglePlayer) {
        this.multiplierSinglePlayer = multiplierSinglePlayer;
    }

    public int getMinutesSinglePlayer() {
        return minutesSinglePlayer;
    }

    public void setMinutesSinglePlayer(int minutesSinglePlayer) {
        this.minutesSinglePlayer = minutesSinglePlayer;
    }

    public HashMap<String, Long> getMin10PlayersPoints() {
        return min10PlayersPoints;
    }

    public void setMin10PlayersPoints(HashMap<String, Long> min10PlayersPoints) {
        this.min10PlayersPoints = min10PlayersPoints;
    }

    public ArrayList<String> getCauses() {
        return causes;
    }

    public void setCauses(ArrayList<String> causes) {
        this.causes = causes;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> color) {
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

    public ArrayList<String> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<String> blocks) {
        this.blocks = blocks;
    }

    public ArrayList<String> getBlocksOnPlace() {
        return blocksOnPlaced;
    }

    public void setBlocksOnPlace(ArrayList<String> blocksOnPlaced) {
        this.blocksOnPlaced = blocksOnPlaced;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public HashMap<String, Long> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Challenger> challengers) {
        while (!challengers.isEmpty()) {
            players.put(challengers.get(0).getNomePlayer(), challengers.get(0).getPoints());
            challengers.remove(0);
        }
    }

    public boolean isDeathInLand() {
        return deathInLand;
    }

    public void setDeathInLand(boolean deathInLand) {
        this.deathInLand = deathInLand;
    }

    public ArrayList<String> getQuests() {
        return quests;
    }

    public void setQuests(ArrayList<String> stringFormatter) {
        this.quests = quests;
    }

    public String getTypeChallenge() {
        return typeChallenge;
    }

    public void setTypeChallenge(String typeChallenge) {
        this.typeChallenge = typeChallenge;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getTimeChallenge() {
        return timeChallenge;
    }

    public void setTimeChallenge(int timeChallenge) {
        this.timeChallenge = timeChallenge;
    }

    public String getNameChallenge() {
        return nameChallenge;
    }

    public void setNameChallenge(String nameChallenge) {
        this.nameChallenge = nameChallenge;
    }

    public String getItemChallenge() {
        return itemChallenge;
    }

    public void setItemChallenge(String itemChallenge) {
        this.itemChallenge = itemChallenge;
    }

    public ArrayList<String> getBlocksOnPlaced() {
        return blocksOnPlaced;
    }

    public void setBlocksOnPlaced(ArrayList<String> blocksOnPlaced) {
        this.blocksOnPlaced = blocksOnPlaced;
    }

    public ArrayList<String> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<String> vehicles) {
        this.vehicles = vehicles;
    }

    public void incrementCommands(String playerName, long amount) {
        if (!Main.instance.getConfigGestion().getTasks().isChallengeStart()) {
            return;
        }
        if (Main.instance.getConfigGestion().getTasks().getIfTaskSaving("SavePoints")
                || Main.instance.getConfigGestion().getTasks().getIfTaskSaving("CheckDay")
                || Main.instance.getConfigGestion().getTasks().getIfTaskSaving("Broadcast")) {
            if (!tempPoints.containsKey(playerName)) {
                tempPoints.put(playerName, amount);
            } else {
                tempPoints.replace(playerName, tempPoints.get(playerName) + amount);
            }
            return;
        } else {
            if (!tempPoints.isEmpty()) {
                for (Map.Entry<String, Long> points : tempPoints.entrySet()) {
                    String playerTemp = points.getKey();
                    long pointTemp = points.getValue();
                    if (!players.containsKey(playerTemp)) {
                        players.put(playerTemp, pointTemp);
                    } else {
                        players.replace(playerTemp, players.get(playerTemp) + pointTemp);
                    }
                }
                tempPoints.clear();
            }
        }
        if (!players.containsKey(playerName)) {
            players.put(playerName, amount);
        } else {
            players.replace(playerName, players.get(playerName) + amount);
        }

    }

    public void increment(String playerName, long amount) {
        if (!Main.instance.getConfigGestion().getTasks().isChallengeStart()) {
            return;
        }
        if (Main.instance.getConfigGestion().getTasks().getIfTaskSaving("SavePoints")
                || Main.instance.getConfigGestion().getTasks().getIfTaskSaving("CheckDay")
                || Main.instance.getConfigGestion().getTasks().getIfTaskSaving("Broadcast")) {
            if (!tempPoints.containsKey(playerName)) {
                tempPoints.put(playerName, amount);
            } else {
                tempPoints.replace(playerName, tempPoints.get(playerName) + amount);
            }
            return;
        } else {
            if (!tempPoints.isEmpty()) {
                for (Map.Entry<String, Long> points : tempPoints.entrySet()) {
                    String playerTemp = points.getKey();
                    long pointTemp = points.getValue();
                    if (!startBoostSinglePlayers.containsKey(playerTemp)) {
                        startBoostSinglePlayers.put(playerTemp, false);
                    }
                    if (startBoost || startBoostSinglePlayers.get(playerTemp)) {
                        if (startBoost) {
                            if (!players.containsKey(playerTemp)) {
                                players.put(playerTemp, pointTemp * multiplier);
                            } else {
                                players.replace(playerTemp, players.get(playerTemp) + (pointTemp * multiplier));
                            }
                            if (!min10PlayersPoints.containsKey(playerTemp)) {
                                min10PlayersPoints.put(playerTemp, pointTemp * multiplier);
                            } else {
                                min10PlayersPoints.replace(playerTemp, min10PlayersPoints.get(playerTemp) + (pointTemp * multiplier));
                            }
                        }
                        if (startBoostSinglePlayers.get(playerTemp)) {
                            if (!players.containsKey(playerTemp)) {
                                players.put(playerTemp, pointTemp * multiplierSinglePlayer);
                            } else {
                                players.replace(playerTemp, players.get(playerTemp) + (pointTemp * multiplierSinglePlayer));
                            }
                            if (!min10PlayersPoints.containsKey(playerTemp)) {
                                min10PlayersPoints.put(playerTemp, pointTemp * multiplierSinglePlayer);
                            } else {
                                min10PlayersPoints.replace(playerTemp, min10PlayersPoints.get(playerTemp) + (pointTemp * multiplierSinglePlayer));
                            }
                        }
                    } else {
                        countPointsChallenge = countPointsChallenge + pointTemp;
                        if (!players.containsKey(playerTemp)) {
                            players.put(playerTemp, pointTemp);
                        } else {
                            players.replace(playerTemp, players.get(playerTemp) + pointTemp);
                        }
                        if (!min10PlayersPoints.containsKey(playerTemp)) {
                            min10PlayersPoints.put(playerTemp, pointTemp);
                        } else {
                            min10PlayersPoints.replace(playerTemp, min10PlayersPoints.get(playerTemp) + pointTemp);
                        }
                        if (!boostSinglePlayers.containsKey(playerTemp)) {
                            boostSinglePlayers.put(playerTemp, pointTemp);
                        } else {
                            boostSinglePlayers.replace(playerTemp, boostSinglePlayers.get(playerTemp) + pointTemp);
                        }
                        checkPointsMultiplier();
                        checkPointsMultiplierSinglePlayer(playerTemp);
                    }
                }
                tempPoints.clear();
            }
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
                if (!players.containsKey(playerName)) {
                    players.put(playerName, amount * multiplierSinglePlayer);
                } else {
                    players.replace(playerName, players.get(playerName) + (amount * multiplierSinglePlayer));
                }
                if (!min10PlayersPoints.containsKey(playerName)) {
                    min10PlayersPoints.put(playerName, amount * multiplierSinglePlayer);
                } else {
                    min10PlayersPoints.replace(playerName, min10PlayersPoints.get(playerName) + (amount * multiplierSinglePlayer));
                }
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

    public void stampaNumero(String playerName) {
        long blocchiPiazzati = players.get(playerName);
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + typeChallenge + ": " + blocchiPiazzati);
    }

    public void savePoints() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            Main.instance.getConfigGestion().getTasks().changeStatusExternalTasks("SavePoints");
            for (Map.Entry<String, Long> player : Collections.unmodifiableMap(players).entrySet()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
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
            Main.instance.getConfigGestion().getTasks().changeStatusExternalTasks("SavePoints");
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
        if (players.get(playerName) == null) {
            return 0;
        } else {
            return players.get(playerName);
        }
    }

    public ArrayList<String> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<String> rewards) {
        this.rewards = rewards;
    }

    public ArrayList<Challenger> getTopPlayers(int numberOfTops) {
        ArrayList<Challenger> topList = new ArrayList<>();
        try {
            for (int i = 0; i < numberOfTops; i++) {
                Challenger challenger = new Challenger();
                for (Map.Entry<String, Long> player : Collections.unmodifiableMap(players).entrySet()) {
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

    public boolean isMinimumPointsReached() {
        long count = 0;
        for (long number : Collections.unmodifiableMap(players).values()) {
            count = count + number;
        }
        return count >= Main.instance.getConfigGestion().getMinimumPoints();
    }

    public ArrayList<String> getTitle() {
        return title;
    }

    public void setTitle(ArrayList<String> title) {
        this.title = title;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public ArrayList<String> getItemsInHand() {
        return itemsInHand;
    }

    public void setItemsInHand(ArrayList<String> itemsInHand) {
        this.itemsInHand = itemsInHand;
    }

    public ArrayList<String> getMobs() {
        return mobs;
    }

    public void setMobs(ArrayList<String> mobs) {
        this.mobs = mobs;
    }

    public int getPointsBoost() {
        return pointsBoost;
    }

    public void setPointsBoost(int pointsBoost) {
        this.pointsBoost = pointsBoost;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
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

    public void setStartBoost(boolean startBoost) {
        this.startBoost = startBoost;
    }

    public long getCountPointsChallenge() {
        return countPointsChallenge;
    }

    public long getCountPointsChallengeSinglePlayer(String playerName) {
        if (boostSinglePlayers.containsKey(playerName)) {
            return boostSinglePlayers.get(playerName);
        } else {
            return 0;
        }
    }

    public void setCountPointsChallenge(long countPointsChallenge) {
        this.countPointsChallenge = countPointsChallenge;
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

    public ArrayList<String> getVehicle() {
        return vehicles;
    }

    public void setVehicle(ArrayList<String> vehicles) {
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

    public void setStartTimeChallenge(String startTimeChallenge) {
        this.startTimeChallenge = startTimeChallenge;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public ArrayList<String> getWorlds() {
        return worlds;
    }

    public void setWorlds(ArrayList<String> worlds) {
        this.worlds = worlds;
    }

    public String getEndTimeChallenge() {
        return endTimeChallenge;
    }

    public void setEndTimeChallenge(String endTimeChallenge) {
        this.endTimeChallenge = endTimeChallenge;
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
                ", time=" + time +
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
                ", tempPoints=" + tempPoints +
                '}';
    }

    private void checkPointsMultiplierSinglePlayer(String playerName) {
        if (minutesSinglePlayer == 0 && multiplierSinglePlayer == 1 && pointsBoostSinglePlayer == 0) {
            return;
        }
        if (boostSinglePlayers.get(playerName) >= pointsBoostSinglePlayer) {
            boostSinglePlayers.remove(playerName);
            startBoostSinglePlayers.replace(playerName, startBoostSinglePlayers.get(playerName), !startBoostSinglePlayers.get(playerName));
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
                    p.sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("BoostMessageSinglePlayer").replace("{number}", multiplierSinglePlayer + "").replace("{minutes}", number + "")));
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
        if (countPointsChallenge >= pointsBoost && !startBoost) {
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
                    p.sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("BoostMessage").replace("{number}", multiplier + "").replace("{minutes}", number + "")));
                }
            }
        }, 0, timeNumber);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(boostingTask, "GlobalBoost", false);
    }

    public void message(CommandSender sender) {
        ArrayList<String> title = new ArrayList<>(this.title);
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {blockOnPlaced" + number + "}"));
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {block" + number + "}"));
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {world" + number + "}"));
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {item" + number + "}"));
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {itemInHand" + number + "}"));
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {vehicle" + number + "}"));
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {cause" + number + "}"));
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {color" + number + "}"));
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
                    sender.sendMessage(ColorUtils.applyColor("&c&o&lERROR WITH PLACEHOLDER {mob" + number + "}"));
                }
            }
        }
        for (String s : title) {
            sender.sendMessage(ColorUtils.applyColor(s
                    .replace("{hours}", Main.dailyChallenge.getTimeChallenge() + "")
                    .replace("{points}", point + "")
                    .replace("{slots}", number + "")
                    .replace("{minutes}", minutes + "")
                    .replace("{challengeName}", nameChallenge)
                    .replace("{sneaking}", sneaking)
                    .replace("{force}", force + "")
                    .replace("{power}", power + "")
                    .replace("{onGround}", onGround)
                    .replace("{keepInventory}", keepInventory + "")
            ));
        }
    }
}
