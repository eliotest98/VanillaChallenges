package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import io.eliotesta98.VanillaChallenges.Core.Main;

import java.util.ArrayList;

public class CheckDay {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private BukkitTask task;
    private int timeRemove = 0;

    public void start(long time) {
        execute(time);
        timeRemove = (int) (time / 20);
    }

    public void stop() {
        task.cancel();
    }


    public void execute(long time) {
        task = scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VanillaChallenges] Backup challenge: " + Main.currentlyChallengeDB.toString());
                Main.currentlyChallengeDB.setTimeResume(Main.currentlyChallengeDB.getTimeResume() - timeRemove);
                if (Main.currentlyChallengeDB.getTimeResume() <= 0) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                            if (Main.instance.getConfigGestion().getDatabase().equalsIgnoreCase("H2")) {
                                H2Database.instance.deleteChallengeWithName(Main.currentlyChallengeDB.getNomeChallenge());
                                H2Database.instance.clearTopYesterday();
                            } else {
                                Main.yamlDB.deleteChallengeWithName(Main.currentlyChallengeDB.getNomeChallenge());
                                Main.yamlDB.saveTopYesterday(topPlayers);
                            }
                            while (!topPlayers.isEmpty()) {
                                DailyWinner dailyWinner = new DailyWinner();
                                dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                                dailyWinner.setNomeChallenge(Main.currentlyChallengeDB.getNomeChallenge());
                                dailyWinner.setReward(Main.dailyChallenge.getReward());
                                if (Main.instance.getConfigGestion().getDatabase().equalsIgnoreCase("H2")) {
                                    H2Database.instance.updateDailyWinner(dailyWinner);
                                    H2Database.instance.insertChallengerTopYesterday(topPlayers.get(0).getNomePlayer(), topPlayers.get(0).getPoints());
                                } else {
                                    Main.yamlDB.updateDailyWinner(dailyWinner);
                                }
                                topPlayers.remove(0);
                            }
                            ReloadUtil.reload();
                        }
                    });
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            if (Main.instance.getConfigGestion().getDatabase().equalsIgnoreCase("H2")) {
                                H2Database.instance.updateChallenge(Main.currentlyChallengeDB.getNomeChallenge(), Main.currentlyChallengeDB.getTimeResume());
                            } else {
                                Main.yamlDB.updateChallenge(Main.currentlyChallengeDB.getNomeChallenge(), Main.currentlyChallengeDB.getTimeResume());
                            }
                        }
                    });
                }
            }
        }, 0, time);
    }

}
