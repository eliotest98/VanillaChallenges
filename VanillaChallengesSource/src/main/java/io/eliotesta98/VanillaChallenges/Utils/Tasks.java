package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Events.ApiEvents.ChallengeChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Tasks {

    private final ArrayList<BukkitTask> tasks = new ArrayList<>();
    private final HashMap<String, Boolean> saving = new HashMap<>();
    private BukkitTask checkStart = null;
    private boolean challengeStart = false;
    private final HashMap<String, Integer> minutesOnlinePlayer = new HashMap<>();

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

    private final static Pattern hexPattern = Pattern.compile("\\{block[0-9]\\}");

    public void broadcast(long time, String actuallyInTop, String pointsEveryMinutes, String pointsRemainForBoosting,
                          String pointsRemainForBoostingSinglePlayer, int numberOfTop, String pointsRemainForReward) {
        saving.put("Broadcast", false);
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            saving.replace("Broadcast", true);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!Main.instance.getConfigGestion().getTasks().isChallengeStart()) {
                    break;
                }
                Main.instance.getDailyChallenge().message(p);
                ArrayList<Challenger> top;
                if (!Main.instance.getConfigGestion().isYesterdayTop()) {
                    top = Main.instance.getDailyChallenge().getTopPlayers(numberOfTop);
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
                if (Main.instance.getConfigGestion().getMinimumPoints() != -1) {
                    if (!Main.instance.getDailyChallenge().isMinimumPointsReached()) {
                        p.sendMessage(ColorUtils.applyColor(pointsRemainForReward.replace("{points}", Main.instance.getDailyChallenge().getPointsRemain() + "")));
                    } else {
                        p.sendMessage(ColorUtils.applyColor(pointsRemainForReward.replace("{points}", "0")));
                    }
                }
                if (Main.instance.getDailyChallenge().getMin10PlayersPoints().get(p.getName()) != null) {
                    String minutes = ((time / 60) / 20) + "";
                    p.sendMessage(ColorUtils.applyColor(pointsEveryMinutes.replace("{points}", MoneyUtils.transform(Main.instance.getDailyChallenge().getMin10PlayersPoints().get(p.getName()))).replace("{minutes}", minutes)));
                }
                if (!Main.instance.getDailyChallenge().isActive()) {
                    long pointsRemain = Main.instance.getDailyChallenge().getPointsBoost() - Main.instance.getDailyChallenge().getCountPointsChallenge();
                    if (pointsRemain > 0) {
                        p.sendMessage(ColorUtils.applyColor(pointsRemainForBoosting.replace("{points}", pointsRemain + "")));
                    }
                }
                if (!Main.instance.getDailyChallenge().isActiveSingleBoost(p.getName())) {
                    long pointsRemain = Main.instance.getDailyChallenge().getPointsBoostSinglePlayer() - Main.instance.getDailyChallenge().getCountPointsChallengeSinglePlayer(p.getName());
                    if (pointsRemain > 0) {
                        p.sendMessage(ColorUtils.applyColor(pointsRemainForBoostingSinglePlayer.replace("{points}", pointsRemain + "")));
                    }
                }
            }
            Main.instance.getDailyChallenge().getMin10PlayersPoints().clear();
            saving.replace("Broadcast", false);
        }, 0, time);
        tasks.add(task);
    }

    public void checkStartDay() {
        saving.put("CheckStartDay", false);
        this.checkStart = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            final String startChallenge = Main.instance.getDailyChallenge().getStartTimeChallenge();
            final String endChallenge = Main.instance.getDailyChallenge().getEndTimeChallenge();
            final String[] startSplit = startChallenge.split(":");
            final String[] endSplit = endChallenge.split(":");
            final int startHour = Integer.parseInt(startSplit[0]);
            final int startMinutes = Integer.parseInt(startSplit[1]);
            final int endHour = Integer.parseInt(endSplit[0]);
            final int endMinutes = Integer.parseInt(endSplit[1]);

            @Override
            public void run() {
                saving.replace("CheckStartDay", true);
                SimpleDateFormat sdf = new SimpleDateFormat("ss.mm.HH.dd.MM.yyyy");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String data = sdf.format(timestamp);
                try {
                    Date now = sdf.parse(data);
                    String[] dataSplit = data.split(Pattern.quote("."));
                    String endData = "00." + endMinutes + "." + endHour + "." + dataSplit[3] + "." + dataSplit[4] + "." + dataSplit[5];
                    String startData = "00." + startMinutes + "." + startHour + "." + dataSplit[3] + "." + dataSplit[4] + "." + dataSplit[5];
                    Date end = sdf.parse(endData);
                    Date start = sdf.parse(startData);
                    if (now.compareTo(start) > 0 && now.compareTo(end) < 0) {
                        Main.instance.getConfigGestion().getTasks().checkDay(20 * 60 * 60,
                                Main.instance.getConfigGestion().isResetPointsAtNewChallenge(),
                                Main.instance.getConfigGestion().isRankingReward(),
                                Main.instance.getConfigGestion().isRandomReward(),
                                Main.instance.getConfigGestion().getNumberOfRewardPlayer());
                        challengeStart = true;
                    } else {
                        challengeStart = false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                saving.replace("CheckStartDay", false);
            }
        }, 0, (long) 60 * 20);
        tasks.add(checkStart);
    }

    public void checkDay(long time, boolean resetPoints, boolean rankingReward, boolean randomReward, int numberOfRewardedPlayer) {
        saving.put("CheckDay", false);
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            boolean firstTime = true;

            @Override
            public void run() {
                saving.replace("CheckDay", true);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VanillaChallenges] Backup challenge: " + Main.instance.getDailyChallenge().getTypeChallenge());
                if (!firstTime) {
                    Main.instance.getDailyChallenge().setTimeChallenge(Main.instance.getDailyChallenge().getTimeChallenge() - 1);
                } else {
                    firstTime = false;
                    checkStart.cancel();
                }
                if (Main.instance.getDailyChallenge().getTimeChallenge() <= 0) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {

                        ChallengeChangeEvent challengeChangeEvent = new ChallengeChangeEvent("Challenge Time Finished", Main.instance.getDailyChallenge());
                        Bukkit.getPluginManager().callEvent(challengeChangeEvent);
                        if (challengeChangeEvent.isCancelled()) {
                            return;
                        }

                        ArrayList<Challenger> topPlayers = Main.instance.getDailyChallenge().getTopPlayers(numberOfRewardedPlayer);

                        Main.db.deleteChallengeWithName(Main.instance.getDailyChallenge().getChallengeName());
                        Main.db.removeTopYesterday();
                        Main.db.saveTopYesterday(topPlayers);
                        if (Main.instance.getConfigGestion().isBackupEnabled()) {
                            Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                        }
                        int number = Main.db.lastDailyWinnerId();
                        Random random = new Random();
                        if (Main.instance.getDailyChallenge().isMinimumPointsReached()) {
                            for (int z = 0; z < topPlayers.size(); z++) {
                                int placeInTop = z;
                                int rewardsSize = Main.instance.getDailyChallenge().getRewards().size();
                                if (z >= rewardsSize) {
                                    placeInTop = rewardsSize - 1;
                                }

                                // Player Stat section
                                if (Main.db.isPlayerHaveStats(topPlayers.get(z).getNomePlayer())) {
                                    PlayerStats playerStats = Main.db.getStatsPlayer(topPlayers.get(z).getNomePlayer());
                                    playerStats.setNumberOfVictories(playerStats.getNumberOfVictories() + 1);
                                    if (z == 0) {
                                        playerStats.setNumberOfFirstPlace(playerStats.getNumberOfFirstPlace() + 1);
                                    } else if (z == 1) {
                                        playerStats.setNumberOfSecondPlace(playerStats.getNumberOfSecondPlace() + 1);
                                    } else if (z == 2) {
                                        playerStats.setNumberOfThirdPlace(playerStats.getNumberOfThirdPlace() + 1);
                                    }
                                    Main.db.updatePlayerStat(playerStats);
                                } else {
                                    PlayerStats playerStats = new PlayerStats();
                                    playerStats.setPlayerName(topPlayers.get(z).getNomePlayer());
                                    playerStats.setNumberOfVictories(1);
                                    if (z == 0) {
                                        playerStats.setNumberOfFirstPlace(1);
                                    } else if (z == 1) {
                                        playerStats.setNumberOfSecondPlace(1);
                                    } else if (z == 2) {
                                        playerStats.setNumberOfThirdPlace(1);
                                    }
                                    Main.db.insertPlayerStat(playerStats);
                                }
                                number++;

                                //Register Winner
                                DailyWinner dailyWinner = new DailyWinner();
                                dailyWinner.setPlayerName(topPlayers.get(z).getNomePlayer());
                                dailyWinner.setNomeChallenge(Main.instance.getDailyChallenge().getChallengeName());
                                if (rankingReward) {
                                    dailyWinner.setId(number);
                                    dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(placeInTop));
                                    Main.db.insertDailyWinner(dailyWinner);
                                } else {
                                    if (randomReward) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(random.nextInt(rewardsSize)));
                                        Main.db.insertDailyWinner(dailyWinner);
                                    } else {
                                        for (int i = 0; i < rewardsSize; i++) {
                                            dailyWinner.setId(number);
                                            dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(i));
                                            Main.db.insertDailyWinner(dailyWinner);
                                            number++;
                                        }
                                    }
                                }
                            }
                        }
                        if (resetPoints) {
                            Main.db.clearChallengers();
                            Main.instance.getDailyChallenge().clearPlayers();
                        }
                        ReloadUtils.reload();
                    });
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> Main.db.updateChallenge(Main.instance.getDailyChallenge().getChallengeName(), Main.instance.getDailyChallenge().getTimeChallenge()));
                }
                saving.replace("CheckDay", false);
            }
        }, 0, time);
        tasks.add(task);
    }

    public void onlinePoints(int minutes, int point) {
        saving.put("OnlinePoints", false);
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            saving.replace("OnlinePoints", true);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (minutesOnlinePlayer.get(p.getName()) == null) {
                    minutesOnlinePlayer.put(p.getName(), 0);
                } else {
                    if (minutesOnlinePlayer.get(p.getName()) == minutes) {
                        Main.instance.getDailyChallenge().increment(p.getName(), point);
                        minutesOnlinePlayer.replace(p.getName(), 0);
                    } else {
                        minutesOnlinePlayer.replace(p.getName(), minutesOnlinePlayer.get(p.getName()) + 1);
                    }
                }
            }
            saving.replace("OnlinePoints", false);
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
