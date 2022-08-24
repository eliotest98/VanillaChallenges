package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import io.eliotesta98.VanillaChallenges.Core.Main;

import java.util.ArrayList;

public class CheckDay {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private BukkitTask task;
    private boolean resetPoints = Main.instance.getConfigGestion().isResetPointsAtNewChallenge();

    public void start(long time) {
        execute(time);
    }

    public void stop() {
        task.cancel();
    }

    public void execute(long time) {
        task = scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            boolean firstTime = true;

            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VanillaChallenges] Backup challenge: " + Main.dailyChallenge.getTypeChallenge());
                if (!firstTime) {
                    Main.dailyChallenge.setTimeChallenge(Main.dailyChallenge.getTimeChallenge() - 1);
                } else {
                    firstTime = false;
                }
                if (Main.dailyChallenge.getTimeChallenge() <= 0) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                            Main.db.deleteChallengeWithName(Main.dailyChallenge.getChallengeName());
                            Main.db.removeTopYesterday();
                            Main.db.saveTopYesterday(topPlayers);
                            if (Main.instance.getConfigGestion().isBackupEnabled()) {
                                Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                            }
                            int number = Main.db.lastDailyWinnerId();
                            while (!topPlayers.isEmpty()) {
                                number++;
                                DailyWinner dailyWinner = new DailyWinner();
                                dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                                dailyWinner.setNomeChallenge(Main.dailyChallenge.getChallengeName());
                                for (int i = 0; i < Main.dailyChallenge.getRewards().size(); i++) {
                                    dailyWinner.setId(number);
                                    dailyWinner.setReward(Main.dailyChallenge.getRewards().get(i));
                                    Main.db.insertDailyWinner(dailyWinner);
                                    number++;
                                }
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
                            Main.db.updateChallenge(Main.dailyChallenge.getChallengeName(), Main.dailyChallenge.getTimeChallenge());
                        }
                    });
                }
            }
        }, 0, time);
    }

}
