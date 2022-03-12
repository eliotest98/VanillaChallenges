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
    private boolean resetPoints = Main.instance.getConfigGestion().isResetPointsAtNewChallenge();

    public void start(long time) {
        execute(time);
        timeRemove = (int) (time / 20);
    }

    public void stop() {
        task.cancel();
    }

    public void execute(long time) {
        task = scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            boolean firstTime = true;

            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VanillaChallenges] Backup challenge: " + Main.currentlyChallengeDB.toString());
                if (!firstTime) {
                    Main.currentlyChallengeDB.setTimeResume(Main.currentlyChallengeDB.getTimeResume() - timeRemove);
                } else {
                    firstTime = false;
                }
                if (Main.currentlyChallengeDB.getTimeResume() <= 0) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                            Main.db.deleteChallengeWithName(Main.currentlyChallengeDB.getNomeChallenge());
                            Main.db.removeTopYesterday();
                            Main.db.saveTopYesterday(topPlayers);
                            if(Main.instance.getConfigGestion().isBackupEnabled()) {
                                Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                            }
                            int number = Main.db.lastDailyWinnerId();
                            while (!topPlayers.isEmpty()) {
                                number++;
                                DailyWinner dailyWinner = new DailyWinner();
                                dailyWinner.setId(number);
                                dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                                dailyWinner.setNomeChallenge(Main.currentlyChallengeDB.getNomeChallenge());
                                dailyWinner.setReward(Main.dailyChallenge.getReward());
                                Main.db.insertDailyWinner(dailyWinner);
                                topPlayers.remove(0);
                            }
                            if (resetPoints) {
                                Main.db.clearChallengers();
                                Main.dailyChallenge.clearPlayers();
                            }
                            ReloadUtil.reload();
                        }
                    });
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            Main.db.updateChallenge(Main.currentlyChallengeDB.getNomeChallenge(), Main.currentlyChallengeDB.getTimeResume());
                        }
                    });
                }
            }
        }, 0, time);
    }

}
