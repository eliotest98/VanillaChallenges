package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Database.H2Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import io.eliotesta98.VanillaChallenges.Core.Main;

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
                if (Main.currentlyChallengeDB.getTimeResume() <= 0) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            H2Database.instance.deleteChallengeWithName(Main.currentlyChallengeDB.getNomeChallenge());
                            ReloadUtil.reload();
                        }
                    });
                } else {
                    Main.currentlyChallengeDB.setTimeResume(Main.currentlyChallengeDB.getTimeResume() - timeRemove);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            H2Database.instance.updateChallenge(Main.currentlyChallengeDB.getNomeChallenge(), Main.currentlyChallengeDB.getTimeResume());
                        }
                    });
                }
            }
        }, 0, time);
    }

}
