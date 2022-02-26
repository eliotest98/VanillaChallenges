package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Challenge {

    private HashMap<String, Long> players = new HashMap<String, Long>();
    private HashMap<String, Long> min10PlayersPoints = new HashMap<String, Long>();
    private String block = "ALL";
    private String blockOnPlace = "ALL";
    private String typeChallenge = "nessuna";
    private String reward = "nessuno";
    private ArrayList<String> title = new ArrayList<>();
    private String item = "ALL";
    private String itemInHand = "ALL";
    private String mob = "ALL";
    private String color = "ALL";
    private String cause = "ALL";
    private double force = 0.0;
    private double power = 0.0;
    // timer del salvataggio punti
    int number = 20 * 60;
    private BukkitTask task, boostingTask;
    int point = 1;
    private int pointsBoost = 0;
    private int multiplier = 1;
    private int minutes = 0;
    long countPointsChallenge = 0;
    boolean startBoost = false;

    public Challenge() {

    }

    public Challenge(String block, String blockOnPlace, String typeChallenge, String reward, ArrayList<String> title, String item, String itemInHand, String mob, double force, double power, String color, String cause, int point, int pointsBoost, int multiplier, int minutes) {
        this.block = block;
        this.blockOnPlace = blockOnPlace;
        this.typeChallenge = typeChallenge;
        this.reward = reward;
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
        this.minutes = minutes;
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

    public HashMap<String, Long> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Challenger> challengers) {
        while (!challengers.isEmpty()) {
            players.put(challengers.get(0).getNomePlayer(), challengers.get(0).getPoints());
            challengers.remove(0);
        }
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

    public void increment(String playerName, long amount) {
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
            checkPointsMultiplier();
        }
    }

    public void clearPlayers() {
        stopTask();
        if (boostingTask != null) {
            stopTaskBoost();
        }
        players.clear();
        min10PlayersPoints.clear();
    }

    public void stampaNumero(String playerName) {
        long blocchiPiazzati = players.get(playerName);
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + typeChallenge + ": " + blocchiPiazzati);
    }

    public void savePoints() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                //Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Vanilla Challenges] Start Backup player points");
                for (Map.Entry<String, Long> player : players.entrySet()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (H2Database.instance.isPresent(player.getKey())) {
                                    H2Database.instance.updateChallenger(player.getKey(), player.getValue());
                                } else {
                                    H2Database.instance.insertChallenger(player.getKey(), player.getValue());
                                }
                            } catch (Exception ex) {
                                Bukkit.getServer().getConsoleSender().sendMessage(ex.getMessage());
                                ReloadUtil.reload();
                            }
                        }
                    });
                }
                ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                while (!topPlayers.isEmpty()) {
                    DailyWinner dailyWinner = new DailyWinner();
                    dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                    dailyWinner.setNomeChallenge(Main.currentlyChallengeDB.getNomeChallenge());
                    dailyWinner.setReward(Main.dailyChallenge.getReward());
                    H2Database.instance.updateDailyWinner(dailyWinner);
                    topPlayers.remove(0);
                }
                if (startBoost && (boostingTask == null || boostingTask.isCancelled())) {
                    startBoosting();
                }
                //Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Vanilla Challenges] End Backup player points");
            }
        }, 0, number);
    }

    public void savePointsYaml() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Vanilla Challenges] Start Backup player points");
                for (Map.Entry<String, Long> player : players.entrySet()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (Main.yamlDB.isPresent(player.getKey())) {
                                    Main.yamlDB.updateChallenger(player.getKey(), player.getValue());
                                } else {
                                    Main.yamlDB.insertChallenger(player.getKey(), player.getValue());
                                }
                            } catch (Exception ex) {
                                Bukkit.getServer().getConsoleSender().sendMessage(ex.getMessage());
                                ReloadUtil.reload();
                            }
                        }
                    });
                }
                ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                while (!topPlayers.isEmpty()) {
                    DailyWinner dailyWinner = new DailyWinner();
                    dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                    dailyWinner.setNomeChallenge(Main.currentlyChallengeDB.getNomeChallenge());
                    dailyWinner.setReward(Main.dailyChallenge.getReward());
                    topPlayers.remove(0);
                }
                if (startBoost && (boostingTask == null || boostingTask.isCancelled())) {
                    startBoosting();
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Vanilla Challenges] End Backup player points");
            }
        }, 0, number);
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

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public ArrayList<Challenger> getTopPlayers(int numberOfTops) {
        ArrayList<Challenger> topList = new ArrayList<>();
        for (int i = 0; i < numberOfTops; i++) {
            Challenger challenger = new Challenger();
            for (Map.Entry<String, Long> player : players.entrySet()) {
                if (player.getValue() > challenger.getPoints()) {
                    boolean trovato = false;
                    for (int x = 0; x < topList.size(); x++) {
                        if (topList.get(x).getNomePlayer().equalsIgnoreCase(player.getKey())) {
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

    public boolean isActive() {
        return startBoost || minutes == 0 || multiplier == 1 || pointsBoost == 0;
    }

    public void setStartBoost(boolean startBoost) {
        this.startBoost = startBoost;
    }

    public long getCountPointsChallenge() {
        return countPointsChallenge;
    }

    public void setCountPointsChallenge(long countPointsChallenge) {
        this.countPointsChallenge = countPointsChallenge;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "players=" + players +
                ", min10PlayersPoints=" + min10PlayersPoints +
                ", block='" + block + '\'' +
                ", blockOnPlace='" + blockOnPlace + '\'' +
                ", typeChallenge='" + typeChallenge + '\'' +
                ", reward='" + reward + '\'' +
                ", title=" + title +
                ", item='" + item + '\'' +
                ", itemInHand='" + itemInHand + '\'' +
                ", mob='" + mob + '\'' +
                ", color='" + color + '\'' +
                ", cause='" + cause + '\'' +
                ", force=" + force +
                ", power=" + power +
                ", number=" + number +
                ", task=" + task +
                ", boostingTask=" + boostingTask +
                ", point=" + point +
                ", pointsBoost=" + pointsBoost +
                ", multiplier=" + multiplier +
                ", minutes=" + minutes +
                ", countPointsChallenge=" + countPointsChallenge +
                ", startBoost=" + startBoost +
                '}';
    }

    private void checkPointsMultiplier() {
        if (minutes == 0 && multiplier == 1 && pointsBoost == 0) {
            return;
        }
        if (countPointsChallenge > pointsBoost && !startBoost) {
            countPointsChallenge = countPointsChallenge - pointsBoost;
            startBoost = true;
        }
    }

    private void startBoosting() {
        boostingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            int number = minutes;

            @Override
            public void run() {
                number--;
                if (number == 0) {
                    startBoost = false;
                    boostingTask.cancel();
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("boostMessage").replace("{number}", multiplier + "").replace("{minutes}", number + "")));
                }
            }
        }, 0, number);
    }
}
