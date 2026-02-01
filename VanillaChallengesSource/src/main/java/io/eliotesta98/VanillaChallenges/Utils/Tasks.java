package io.eliotesta98.VanillaChallenges.Utils;

import com.HeroxWar.HeroxCore.MessageGesture;
import com.HeroxWar.HeroxCore.TimeGesture.Time;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Tasks {

    private final List<BukkitTask> tasks = new ArrayList<>();
    private BukkitTask checkStart = null;
    private boolean challengeStart = false;
    private final Map<String, Integer> minutesOnlinePlayer = new ConcurrentHashMap<>();
    private BukkitTask peacefulTask = null;

    public void stopAllTasks() {
        for (BukkitTask task : new ArrayList<>(tasks)) {
            if (task != null) {
                task.cancel();
            }
        }
    }

    public boolean isChallengeStart() {
        return !challengeStart;
    }

    public void setChallengeStart(boolean challengeStart) {
        this.challengeStart = challengeStart;
    }

    public void broadcast(long time, String actuallyInTop, String pointsEveryMinutes, String pointsRemainForBoosting,
                          String pointsRemainForBoostingSinglePlayer, int numberOfTop, String pointsRemainForReward) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Main.instance.getConfigGestion().getTasks().isChallengeStart()) {
                    break;
                }
                Main.instance.getDailyChallenge().message(p);
                List<Challenger> top;
                if (Main.instance.getConfigGestion().isYesterdayTop()) {
                    top = new ArrayList<>(Main.db.getTopYesterday());
                } else {
                    top = new ArrayList<>(Main.instance.getDailyChallenge().getTopPlayers(numberOfTop));
                }
                if (!top.isEmpty()) {
                    MessageGesture.sendMessage(p, actuallyInTop);
                }
                int i = 1;
                for (Challenger challenger : top) {
                    MessageGesture.sendMessage(p, Main.instance.getConfigGestion().getMessages().get("TopPlayers" + i).replace("{number}", "" + i).replace("{player}", challenger.getNomePlayer()).replace("{points}", MoneyUtils.transform(challenger.getPoints())));
                    i++;
                }
                if (Main.instance.getConfigGestion().getMinimumPoints() != -1) {
                    if (!Main.instance.getDailyChallenge().isMinimumPointsReached()) {
                        MessageGesture.sendMessage(p, pointsRemainForReward.replace("{points}", Main.instance.getDailyChallenge().getPointsRemain() + ""));
                    } else {
                        MessageGesture.sendMessage(p, pointsRemainForReward.replace("{points}", "0"));
                    }
                }
                if (Main.instance.getDailyChallenge().getMin10PlayersPoints().get(p.getName()) != null) {
                    String minutes = ((time / 60) / 20) + "";
                    MessageGesture.sendMessage(p, pointsEveryMinutes.replace("{points}", MoneyUtils.transform(Main.instance.getDailyChallenge().getMin10PlayersPoints().get(p.getName()))).replace("{minutes}", minutes));
                }
                if (!Main.instance.getDailyChallenge().isActive()) {
                    long pointsRemain = Main.instance.getDailyChallenge().getPointsBoost() - Main.instance.getDailyChallenge().getCountPointsChallenge();
                    if (pointsRemain > 0) {
                        MessageGesture.sendMessage(p, pointsRemainForBoosting.replace("{points}", pointsRemain + ""));
                    }
                }
                if (!Main.instance.getDailyChallenge().isActiveSingleBoost(p.getName())) {
                    long pointsRemain = Main.instance.getDailyChallenge().getPointsBoostSinglePlayer() - Main.instance.getDailyChallenge().getCountPointsChallengeSinglePlayer(p.getName());
                    if (pointsRemain > 0) {
                        MessageGesture.sendMessage(p, pointsRemainForBoostingSinglePlayer.replace("{points}", pointsRemain + ""));
                    }
                }
            }
            Main.instance.getDailyChallenge().getMin10PlayersPoints().clear();
        }, 0, time);
        tasks.add(task);
    }

    public void checkStartDay() {
        this.checkStart = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            final String startChallenge = Main.instance.getDailyChallenge().getStartTimeChallenge();
            final String endChallenge = Main.instance.getDailyChallenge().getEndTimeChallenge();
            final String[] startSplit = startChallenge.split(":");
            final String[] endSplit = endChallenge.split(":");
            final int startHour = Integer.parseInt(startSplit[0]);
            final int startMinutes = Integer.parseInt(startSplit[1]);
            final int endHour = Integer.parseInt(endSplit[0]);
            final int endMinutes = Integer.parseInt(endSplit[1]);

            @SuppressWarnings("CallToPrintStackTrace")
            @Override
            public void run() {
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
                        Main.instance.getConfigGestion().getTasks().checkDay(
                                Main.instance.getConfigGestion().isResetPointsAtNewChallenge(),
                                Main.instance.getConfigGestion().isRankingReward(),
                                Main.instance.getConfigGestion().isRandomReward(),
                                Main.instance.getConfigGestion().getNumberOfRewardPlayer(),
                                Main.instance.getConfigGestion().getNumberOfTop());
                        setChallengeStart(true);
                    } else {
                        setChallengeStart(false);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60 * 20L);
        tasks.add(checkStart);
    }

    public void checkDay(boolean resetPoints, boolean rankingReward, boolean randomReward, int numberOfRewardedPlayer, int numberOfTop) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.instance, new Runnable() {
            boolean firstTime = true;

            @Override
            public void run() {
                Time time = Main.instance.getDailyChallenge().getTimeChallenge();
                if (firstTime) {
                    firstTime = false;
                    checkStart.cancel();
                }
                if (time.getMilliseconds() <= 0) {
                    Main.instance.getDailyChallenge().nextChallenge(resetPoints, rankingReward, randomReward, numberOfRewardedPlayer, numberOfTop, "Challenge Time Finished", false);
                } else {
                    if (time.getSeconds() == 0) {
                        Main.db.updateChallenge(Main.instance.getDailyChallenge().getChallengeName(), time.getMilliseconds());
                    }
                    Main.instance.getDailyChallenge().setTimeChallenge(time.differenceBetween(new Time(0, 0, 0, 1, ':')));
                }
            }
        }, 0, 20L);
        tasks.add(task);
    }

    public void peacefulTimeTask() {
        this.peacefulTask = Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {
            Time time = Main.db.getPeacefulTime();
            if (time.getMilliseconds() <= 0) {
                // go to next challenge
                Main.db.updatePeacefulTime(new Time(0, ':'));
                Main.instance.pluginStartingProcess();
                peacefulTask.cancel();
            } else {
                if (time.getSeconds() == 0) {
                    Main.db.updatePeacefulTime(time);
                }
                Main.db.setPeacefulTime(time.differenceBetween(new Time(0, 0, 0, 1, ':')));
            }
        }, 0, 20L);
        tasks.add(peacefulTask);
    }

    public void onlinePoints(int minutes, int point) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (minutesOnlinePlayer.get(p.getName()) == null) {
                    minutesOnlinePlayer.put(p.getName(), 0);
                } else {
                    if (minutesOnlinePlayer.get(p.getName()) == minutes) {
                        if (!Controls.hasPermission(p.getName())) {
                            return;
                        }
                        Main.instance.getDailyChallenge().increment(p.getName(), point);
                        minutesOnlinePlayer.replace(p.getName(), 0);
                    } else {
                        minutesOnlinePlayer.replace(p.getName(), minutesOnlinePlayer.get(p.getName()) + 1);
                    }
                }
            }
        }, 0, 60 * 20L);
        tasks.add(task);
    }

    public void addExternalTasks(BukkitTask task, String savingName, boolean save) {
        tasks.add(task);
    }
}
