package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Tasks {

    private ArrayList<BukkitTask> tasks = new ArrayList<>();
    private HashMap<String, Boolean> saving = new HashMap<>();
    private BukkitTask checkStart = null;
    private boolean challengeStart = false;
    private HashMap<String, Integer> minutesOnlinePlayer = new HashMap<>();

    public void stopAllTasks() {
        for (BukkitTask task : tasks) {
            if (task != null) {
                task.cancel();
            }
        }
    }

    public boolean isChallengeStart() {
        return challengeStart;
    }

    public void broadcast(long time, ArrayList<String> brodcastMessageTitle,
                          String actuallyInTop, String pointsEveryMinutes, String pointsRemainForBoosting,
                          String pointsRemainForBoostingSinglePlayer, int numberOfTop) {
        saving.put("Broadcast", false);
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            saving.replace("Broadcast", true);
            int timeResume = Main.dailyChallenge.getTimeChallenge();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!Main.instance.getConfigGestion().getTasks().isChallengeStart()) {
                    break;
                }
                for (String s : brodcastMessageTitle) {
                    p.sendMessage(ColorUtils.applyColor(s.replace("{hours}", timeResume + "")));
                }
                ArrayList<Challenger> top;
                if (!Main.instance.getConfigGestion().isYesterdayTop()) {
                    top = Main.dailyChallenge.getTopPlayers(numberOfTop);
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

    public void checkStartDay() {
        saving.put("CheckStartDay", false);
        this.checkStart = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            String startChallenge = Main.dailyChallenge.getStartTimeChallenge();
            String[] startSplit = startChallenge.split(":");
            int startHour = Integer.parseInt(startSplit[0]);
            int startMinutes = Integer.parseInt(startSplit[1]);
            int time = Main.dailyChallenge.getTimeChallenge();

            @Override
            public void run() {
                saving.replace("CheckStartDay", true);
                SimpleDateFormat sdf = new SimpleDateFormat("ss.mm.HH.dd.MM.yyyy");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String data = sdf.format(timestamp);
                String[] dataSplit = data.split(Pattern.quote("."));
                int hour = Integer.parseInt(dataSplit[2]);
                int minutes = Integer.parseInt(dataSplit[1]);
                if (hour > startHour) {
                    Main.instance.getConfigGestion().getTasks().checkDay(20 * 60 * 60,
                            Main.instance.getConfigGestion().isResetPointsAtNewChallenge(),
                            Main.instance.getConfigGestion().isRankingReward(),
                            Main.instance.getConfigGestion().getNumberOfTop());
                    challengeStart = true;
                } else if (hour == startHour && minutes >= startMinutes) {
                    Main.instance.getConfigGestion().getTasks().checkDay(20 * 60 * 60,
                            Main.instance.getConfigGestion().isResetPointsAtNewChallenge(),
                            Main.instance.getConfigGestion().isRankingReward(),
                            Main.instance.getConfigGestion().getNumberOfTop());
                    challengeStart = true;
                } else {
                    if (time < Main.instance.getConfigGestion().getChallenges().get(
                            Main.dailyChallenge.getChallengeName()).getTimeChallenge()) {
                        if (hour < startHour) {
                            Main.instance.getConfigGestion().getTasks().checkDay(20 * 60 * 60,
                                    Main.instance.getConfigGestion().isResetPointsAtNewChallenge(),
                                    Main.instance.getConfigGestion().isRankingReward(),
                                    Main.instance.getConfigGestion().getNumberOfTop());
                            challengeStart = true;
                        } else if (hour == startHour && minutes <= startMinutes) {
                            Main.instance.getConfigGestion().getTasks().checkDay(20 * 60 * 60,
                                    Main.instance.getConfigGestion().isResetPointsAtNewChallenge(),
                                    Main.instance.getConfigGestion().isRankingReward(),
                                    Main.instance.getConfigGestion().getNumberOfTop());
                            challengeStart = true;
                        }
                    } else {
                        challengeStart = false;
                    }
                }
                saving.replace("CheckStartDay", false);
            }
        }, 0, (long) 60 * 20);
        tasks.add(checkStart);
    }

    public void checkDay(long time, boolean resetPoints, boolean rankingReward, int numberOfTop) {
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
                    checkStart.cancel();
                }
                if (Main.dailyChallenge.getTimeChallenge() <= 0) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(numberOfTop);
                        Main.db.deleteChallengeWithName(Main.dailyChallenge.getChallengeName());
                        Main.db.removeTopYesterday();
                        Main.db.saveTopYesterday(topPlayers);
                        if (Main.instance.getConfigGestion().isBackupEnabled()) {
                            Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                        }
                        int number = Main.db.lastDailyWinnerId();
                        for (int z = 0; z < topPlayers.size(); z++) {
                            int placeInTop = z;
                            if (z >= Main.dailyChallenge.getRewards().size()) {
                                placeInTop = Main.dailyChallenge.getRewards().size() - 1;
                            }
                            number++;
                            DailyWinner dailyWinner = new DailyWinner();
                            dailyWinner.setPlayerName(topPlayers.get(z).getNomePlayer());
                            dailyWinner.setNomeChallenge(Main.dailyChallenge.getChallengeName());
                            if (rankingReward) {
                                dailyWinner.setId(number);
                                dailyWinner.setReward(Main.dailyChallenge.getRewards().get(placeInTop));
                                Main.db.insertDailyWinner(dailyWinner);
                            } else {
                                for (int i = 0; i < Main.dailyChallenge.getRewards().size(); i++) {
                                    dailyWinner.setId(number);
                                    dailyWinner.setReward(Main.dailyChallenge.getRewards().get(i));
                                    Main.db.insertDailyWinner(dailyWinner);
                                    number++;
                                }
                            }
                        }
                        if (resetPoints) {
                            Main.db.clearChallengers();
                            Main.dailyChallenge.clearPlayers();
                        }
                        ReloadUtils.reload();
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
                    if (minutesOnlinePlayer.get(p.getName()) == null) {
                        minutesOnlinePlayer.put(p.getName(), 0);
                    } else {
                        if (minutesOnlinePlayer.get(p.getName()) == minutes) {
                            Main.dailyChallenge.increment(p.getName(), point);
                            minutesOnlinePlayer.replace(p.getName(), 0);
                        } else {
                            minutesOnlinePlayer.replace(p.getName(), minutesOnlinePlayer.get(p.getName()) + 1);
                        }
                    }
                }
                saving.replace("OnlinePoints", false);
            }
        }, 0, (long) 60 * 20);
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
