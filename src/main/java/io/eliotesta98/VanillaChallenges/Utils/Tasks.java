package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Tasks {

    private ArrayList<BukkitTask> tasks = new ArrayList<>();
    private HashMap<String, Boolean> saving = new HashMap<>();

    public void stopAllTasks() {
        for (BukkitTask task : tasks) {
            if (task != null) {
                task.cancel();
            }
        }
    }

    public void broadcast(long time, ArrayList<String> brodcastMessageTitle, String actuallyInTop, String pointsEveryMinutes, String pointsRemainForBoosting, String pointsRemainForBoostingSinglePlayer) {
        saving.put("Broadcast", false);
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            saving.replace("Broadcast", true);
            int timeResume = Main.dailyChallenge.getTimeChallenge();
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (String s : brodcastMessageTitle) {
                    p.sendMessage(ColorUtils.applyColor(s.replace("{hours}", timeResume + "")));
                }
                ArrayList<Challenger> top;
                if (!Main.instance.getConfigGestion().isYesterdayTop()) {
                    top = Main.dailyChallenge.getTopPlayers(3);
                } else {
                    top = Main.db.getAllChallengersTopYesterday();
                }
                if (!top.isEmpty()) {
                    p.sendMessage(ColorUtils.applyColor(actuallyInTop));
                }
                int i = 1;
                while (!top.isEmpty()) {
                    p.sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + MoneyUtils.transform(top.get(0).getPoints()))));
                    top.remove(0);
                    i++;
                }
                if (Main.dailyChallenge.getMin10PlayersPoints().get(p.getName()) != null) {
                    String minutes = ((time / 60) / 20) + "";
                    p.sendMessage(ColorUtils.applyColor(pointsEveryMinutes.replace("{points}", MoneyUtils.transform(Main.dailyChallenge.getMin10PlayersPoints().get(p.getName()))).replace("{minutes}", minutes)));
                }
                if (!Main.dailyChallenge.isActive()) {
                    long pointsRemain = Main.dailyChallenge.getPointsBoost() - Main.dailyChallenge.getCountPointsChallenge();
                    if (pointsRemain > 0) {
                        p.sendMessage(ColorUtils.applyColor(pointsRemainForBoosting.replace("{points}", pointsRemain + "")));
                    }
                }
                if (!Main.dailyChallenge.isActiveSingleBoost(p.getName())) {
                    long pointsRemain = Main.dailyChallenge.getPointsBoostSinglePlayer() - Main.dailyChallenge.getCountPointsChallengeSinglePlayer(p.getName());
                    if (pointsRemain > 0) {
                        p.sendMessage(ColorUtils.applyColor(pointsRemainForBoostingSinglePlayer.replace("{points}", pointsRemain + "")));
                    }
                }
            }
            Main.dailyChallenge.getMin10PlayersPoints().clear();
            saving.replace("Broadcast", false);
        }, 0, time);
        tasks.add(task);
    }

    public void checkDay(long time, boolean resetPoints) {
        saving.put("CheckDay", false);
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            boolean firstTime = true;

            @Override
            public void run() {
                saving.replace("CheckDay", true);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VanillaChallenges] Backup challenge: " + Main.dailyChallenge.getTypeChallenge());
                if (!firstTime) {
                    Main.dailyChallenge.setTimeChallenge(Main.dailyChallenge.getTimeChallenge() - 1);
                } else {
                    firstTime = false;
                }
                if (Main.dailyChallenge.getTimeChallenge() <= 0) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
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
                    });
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> Main.db.updateChallenge(Main.dailyChallenge.getChallengeName(), Main.dailyChallenge.getTimeChallenge()));
                }
                saving.replace("CheckDay", false);
            }
        }, 0, time);
        tasks.add(task);
    }

    public void onlinePoints(int minutes, int point) {
        saving.put("OnlinePoints", false);
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                saving.replace("OnlinePoints", true);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Main.dailyChallenge.increment(p.getName(), point);
                }
                saving.replace("OnlinePoints", false);
            }
        }, 0, (long) minutes * 60 * 20);
        tasks.add(task);
    }

    public void addExternalTasks(BukkitTask task, String savingName, boolean save) {
        tasks.add(task);
        saving.put(savingName, save);
    }

    public void changeStatusExternalTasks(String savingName) {
        saving.replace(savingName, !saving.get(savingName));
    }

    public boolean getIfTaskSaving(String nameTask) {
        return this.saving.get(nameTask);
    }
}
