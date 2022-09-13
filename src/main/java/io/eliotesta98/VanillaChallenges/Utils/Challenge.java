package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Challenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Challenge {

    private HashMap<String, Long> players = new HashMap<String, Long>();
    private HashMap<String, Long> min10PlayersPoints = new HashMap<String, Long>();
    private HashMap<String, Long> boostSinglePlayers = new HashMap<String, Long>();
    private HashMap<String, Boolean> startBoostSinglePlayers = new HashMap<String, Boolean>();
    private HashMap<String, BukkitTask> tasksSinglePlayers = new HashMap<String, BukkitTask>();
    private String challengeName = "nessuna";
    private String block = "ALL";
    private String blockOnPlace = "ALL";
    private String typeChallenge = "nessuna";
    private ArrayList<String> rewards = new ArrayList<>();
    private ArrayList<String> title = new ArrayList<>();
    private String item = "ALL";
    private String itemInHand = "ALL";
    private String mob = "ALL";
    private String color = "ALL";
    private String cause = "ALL";
    private String vehicle = "ALL";
    private double force = 0.0;
    private double power = 0.0;
    private int number = 0;
    private int time = 1;
    private String onGround = "NOBODY";
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
    private long countPointsChallenge = 0;
    private boolean startBoost = false;
    private String sneaking = "NOBODY";
    private String stringFormatter = "abcdefghijklmnopqrstuvz";
    int minutes = 1;

    private HashMap<String, Long> tempPoints = new HashMap<>();

    public Challenge() {

    }

    public Challenge(String block, String blockOnPlace, String typeChallenge, ArrayList<String> rewards, ArrayList<String> title, String item, String itemInHand, String mob, double force, double power, String color, String cause, int point, int pointsBoost, int multiplier, int boostMinutes, int number, int time, String vehicle, String sneaking, String onGround, int pointsBoostSinglePlayer, int multiplierSinglePlayer, int minutesSinglePlayer, int timeChallenge, String challengeName, String stringFormatter, int minutes) {
        this.block = block;
        this.blockOnPlace = blockOnPlace;
        this.typeChallenge = typeChallenge;
        this.rewards = rewards;
        this.title = title;
        this.item = item;
        this.itemInHand = itemInHand;
        this.mob = mob;
        this.force = force;
        this.power = power;
        this.color = color;
        this.cause = cause;
        this.point = point;
        this.pointsBoost = pointsBoost;
        this.multiplier = multiplier;
        this.boostMinutes = boostMinutes;
        this.number = number;
        this.time = time;
        this.vehicle = vehicle;
        this.sneaking = sneaking;
        this.onGround = onGround;
        this.pointsBoostSinglePlayer = pointsBoostSinglePlayer;
        this.minutesSinglePlayer = minutesSinglePlayer;
        this.multiplierSinglePlayer = multiplierSinglePlayer;
        this.timeChallenge = timeChallenge;
        this.challengeName = challengeName;
        this.stringFormatter = stringFormatter;
        this.minutes = minutes;
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

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBlockOnPlace() {
        return blockOnPlace;
    }

    public void setBlockOnPlace(String blockOnPlace) {
        this.blockOnPlace = blockOnPlace;
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

    public String getStringFormatter() {
        return stringFormatter;
    }

    public void setStringFormatter(String stringFormatter) {
        this.stringFormatter = stringFormatter;
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

    public void incrementCommands(String playerName, long amount) {
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
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                Main.instance.getConfigGestion().getTasks().changeStatusExternalTasks("SavePoints");
                //Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Vanilla Challenges] Start Backup player points");
                HashMap<String, Long> copyMap = new HashMap<String, Long>(players);
                for (Map.Entry<String, Long> player : copyMap.entrySet()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
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
                        }
                    });
                }
                if (startBoost && (boostingTask == null || boostingTask.isCancelled())) {
                    startBoosting();
                }
                Main.instance.getConfigGestion().getTasks().changeStatusExternalTasks("SavePoints");
                //Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Vanilla Challenges] End Backup player points");
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

    public ArrayList<String> getTitle() {
        return title;
    }

    public void setTitle(ArrayList<String> title) {
        this.title = title;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemInHand() {
        return itemInHand;
    }

    public void setItemInHand(String itemInHand) {
        this.itemInHand = itemInHand;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
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

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
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

    @Override
    public String toString() {
        return "Challenge{" +
                "players=" + players +
                ", min10PlayersPoints=" + min10PlayersPoints +
                ", block='" + block + '\'' +
                ", blockOnPlace='" + blockOnPlace + '\'' +
                ", typeChallenge='" + typeChallenge + '\'' +
                ", reward='" + rewards + '\'' +
                ", title=" + title +
                ", item='" + item + '\'' +
                ", itemInHand='" + itemInHand + '\'' +
                ", mob='" + mob + '\'' +
                ", color='" + color + '\'' +
                ", cause='" + cause + '\'' +
                ", force=" + force +
                ", power=" + power +
                ", timeNumber=" + timeNumber +
                ", task=" + task +
                ", boostingTask=" + boostingTask +
                ", point=" + point +
                ", pointsBoost=" + pointsBoost +
                ", multiplier=" + multiplier +
                ", minutes=" + boostMinutes +
                ", countPointsChallenge=" + countPointsChallenge +
                ", startBoost=" + startBoost +
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
}
