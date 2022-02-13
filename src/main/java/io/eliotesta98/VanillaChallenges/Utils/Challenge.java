package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Challenge {

    private HashMap<String, Long> players = new HashMap<String, Long>();
    private String block = "ALL";
    private String blockOnPlace = "ALL";
    private String typeChallenge = "nessuna";
    private String reward = "nessuno";
    private String title = "nessuno";
    private String subTitle = "nessuno";
    private String item = "ALL";
    private String itemInHand = "ALL";
    private String mob = "ALL";
    private String color = "ALL";
    private String cause = "ALL";
    private double force = 0.0;
    private double power = 0.0;
    // timer del salvataggio punti
    int number = 20 * 60;
    private BukkitTask task;

    public Challenge() {

    }

    public Challenge(String block, String blockOnPlace, String typeChallenge, String reward, String title, String subTitle, String item, String itemInHand, String mob, double force, double power, String color, String cause) {
        this.block = block;
        this.blockOnPlace = blockOnPlace;
        this.typeChallenge = typeChallenge;
        this.reward = reward;
        this.title = title;
        this.subTitle = subTitle;
        this.item = item;
        this.itemInHand = itemInHand;
        this.mob = mob;
        this.force = force;
        this.power = power;
        this.color = color;
        this.cause = cause;
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

    public void increment(String playerName) {
        if (!players.containsKey(playerName)) {
            players.put(playerName, 1L);
        } else {
            players.replace(playerName, players.get(playerName) + 1);
        }
    }

    public void increment(String playerName, long amount) {
        if (!players.containsKey(playerName)) {
            players.put(playerName, amount);
        } else {
            players.replace(playerName, players.get(playerName) + amount);
        }
    }

    public void clearPlayers() {
        stopTask();
        players.clear();
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
                            if (H2Database.instance.isPresent(player.getKey())) {
                                H2Database.instance.updateChallenger(player.getKey(), player.getValue());
                            } else {
                                H2Database.instance.insertChallenger(player.getKey(), player.getValue());
                            }
                        }
                    });
                }
                //Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Vanilla Challenges] End Backup player points");
            }
        }, 0, number);
    }

    public void stopTask() {
        task.cancel();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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

    @Override
    public String toString() {
        return "Challenge{" +
                "players=" + players +
                ", block='" + block + '\'' +
                ", blockOnPlace='" + blockOnPlace + '\'' +
                ", typeChallenge='" + typeChallenge + '\'' +
                '}';
    }
}
