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

    private HashMap<String, Integer> players = new HashMap<String, Integer>();
    private String block = "ALL";
    private String blockOnPlace = "ALL";
    private String typeChallenge = "nessuna";
    private String reward = "nessuno";
    private String title = "nessuno";
    private String subTitle = "nessuno";
    private String item = "ALL";
    private String itemInHand = "ALL";
    // timer del salvataggio punti
    int number = 20*60*10;
    private BukkitTask task;

    public Challenge() {

    }

    public Challenge(String block, String blockOnPlace,String typeChallenge, String reward, String title, String subTitle, String item, String itemInHand) {
        this.block = block;
        this.blockOnPlace = blockOnPlace;
        this.typeChallenge = typeChallenge;
        this.reward = reward;
        this.title = title;
        this.subTitle = subTitle;
        this.item = item;
        this.itemInHand = itemInHand;
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

    public HashMap<String, Integer> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Challenger> challengers) {
        while(!challengers.isEmpty()) {
            players.put(challengers.get(0).getNomePlayer(),challengers.get(0).getPoints());
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
            players.put(playerName, 1);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                @Override
                public void run() {
                    H2Database.instance.insertChallenger(playerName, 1);
                }
            });
        } else {
            players.replace(playerName, players.get(playerName) + 1);
        }
    }

    public void clearPlayers() {
        stopTask();
        players.clear();
    }

    public void stampaNumero(String playerName) {
        int blocchiPiazzati = players.get(playerName);
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + typeChallenge + ": " + blocchiPiazzati);
    }

    public void savePoints() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                //Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Vanilla Challenges] Start Backup player points");
                for (Map.Entry<String, Integer> player : players.entrySet()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            H2Database.instance.updateChallenger(player.getKey(), player.getValue());
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

    public int getPointFromPLayerName(String playerName) {
        if(players.get(playerName) == null) {
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

    public ArrayList<Challenger> getTopPlayers() {
        Challenger primo = new Challenger();
        Challenger secondo = new Challenger();
        Challenger terzo = new Challenger();
        ArrayList<Challenger> top = new ArrayList<>();
        int i = 1;
        for (Map.Entry<String, Integer> player : players.entrySet()) {
            if(i == 1) {
                primo = new Challenger(player.getKey(),player.getValue());
                top.add(primo);
                i++;
            } else if(i == 2) {
                secondo = new Challenger(player.getKey(),player.getValue());
                top.add(secondo);
                i++;
            } else if(i == 3) {
                terzo = new Challenger(player.getKey(),player.getValue());
                top.add(terzo);
                i++;
            } else {
                if(player.getValue() > primo.getPoints()) {
                    primo.setNomePlayer(player.getKey());
                    primo.setPoints(player.getValue());
                } else if(player.getValue() > secondo.getPoints()) {
                    secondo.setNomePlayer(player.getKey());
                    secondo.setPoints(player.getValue());
                } else if(player.getValue() > terzo.getPoints()) {
                    terzo.setNomePlayer(player.getKey());
                    terzo.setPoints(player.getValue());
                }
            }
        }
        return top;
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
