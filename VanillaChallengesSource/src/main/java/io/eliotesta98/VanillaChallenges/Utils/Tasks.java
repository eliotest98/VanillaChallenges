package io.eliotesta98.VanillaChallenges.Utils;

import com.HeroxWar.HeroxCore.MessageGesture;
import com.HeroxWar.HeroxCore.TimeGesture.Date.Date;
import com.HeroxWar.HeroxCore.TimeGesture.Time;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Tasks {

    private final List<BukkitTask> tasks = new ArrayList<>();
    private com.HeroxWar.HeroxCore.TimeGesture.Date.Date now = new com.HeroxWar.HeroxCore.TimeGesture.Date.Date();
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

    public com.HeroxWar.HeroxCore.TimeGesture.Date.Date getNow() {
        return now;
    }

    public void setNow(com.HeroxWar.HeroxCore.TimeGesture.Date.Date now) {
        this.now = now;
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
                    Main.messageGesturePaper.sendMessage(p, actuallyInTop);
                }
                int i = 1;
                for (Challenger challenger : top) {
                    Main.messageGesturePaper.sendMessage(p, Main.instance.getConfigGestion().getMessages().get("TopPlayers" + i).replace("{number}", "" + i).replace("{player}", challenger.getNomePlayer()).replace("{points}", MoneyUtils.transform(challenger.getPoints())));
                    i++;
                }
                if (Main.instance.getConfigGestion().getMinimumPoints() != -1) {
                    if (!Main.instance.getDailyChallenge().isMinimumPointsReached()) {
                        Main.messageGesturePaper.sendMessage(p, pointsRemainForReward.replace("{points}", Main.instance.getDailyChallenge().getPointsRemain() + ""));
                    } else {
                        Main.messageGesturePaper.sendMessage(p, pointsRemainForReward.replace("{points}", "0"));
                    }
                }
                if (Main.instance.getDailyChallenge().getMin10PlayersPoints().get(p.getName()) != null) {
                    String minutes = ((time / 60) / 20) + "";
                    Main.messageGesturePaper.sendMessage(p, pointsEveryMinutes.replace("{points}", MoneyUtils.transform(Main.instance.getDailyChallenge().getMin10PlayersPoints().get(p.getName()))).replace("{minutes}", minutes));
                }
                if (!Main.instance.getDailyChallenge().isActive()) {
                    long pointsRemain = Main.instance.getDailyChallenge().getPointsBoost() - Main.instance.getDailyChallenge().getCountPointsChallenge();
                    if (pointsRemain > 0) {
                        Main.messageGesturePaper.sendMessage(p, pointsRemainForBoosting.replace("{points}", pointsRemain + ""));
                    }
                }
                if (!Main.instance.getDailyChallenge().isActiveSingleBoost(p.getName())) {
                    long pointsRemain = Main.instance.getDailyChallenge().getPointsBoostSinglePlayer() - Main.instance.getDailyChallenge().getCountPointsChallengeSinglePlayer(p.getName());
                    if (pointsRemain > 0) {
                        Main.messageGesturePaper.sendMessage(p, pointsRemainForBoostingSinglePlayer.replace("{points}", pointsRemain + ""));
                    }
                }
            }
            Main.instance.getDailyChallenge().getMin10PlayersPoints().clear();
        }, 0, time);
        tasks.add(task);
    }

    public void checkStartDay() {
        this.checkStart = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                String startChallenge = Main.instance.getDailyChallenge().getStartTimeChallenge();
                String endChallenge = Main.instance.getDailyChallenge().getEndTimeChallenge();
                String[] startSplit = startChallenge.split(":");
                String[] endSplit = endChallenge.split(":");
                int startHour = Integer.parseInt(startSplit[0]);
                int startMinutes = Integer.parseInt(startSplit[1]);
                int endHour = Integer.parseInt(endSplit[0]);
                int endMinutes = Integer.parseInt(endSplit[1]);

                Date end = now.cloneDate();
                end.setDate(now.getYear() + "." + now.getMonth() + "." + now.getDay() + "." + endHour + "." + endMinutes + ".00");

                Date start = now.cloneDate();
                start.setDate(now.getYear() + "." + now.getMonth() + "." + now.getDay() + "." + startHour + "." + startMinutes + ".00");

                if (start.getMilliseconds() < now.getMilliseconds() && end.getMilliseconds() > now.getMilliseconds()) {
                    Main.instance.getConfigGestion().getTasks().checkDay(
                            Main.instance.getConfigGestion().isResetPointsAtNewChallenge(),
                            Main.instance.getConfigGestion().isRankingReward(),
                            Main.instance.getConfigGestion().isRandomReward(),
                            Main.instance.getConfigGestion().getNumberOfRewardPlayer(),
                            Main.instance.getConfigGestion().getNumberOfTop(),
                            Main.instance.getConfigGestion().isAdjustTime());
                    setChallengeStart(true);
                } else {
                    setChallengeStart(false);
                }
            }
        }, 0, 60 * 20L);
        tasks.add(checkStart);
    }

    public void checkDay(boolean resetPoints, boolean rankingReward, boolean randomReward, int numberOfRewardedPlayer, int numberOfTop, boolean adjust) {
        BukkitTask checkDay = Bukkit.getScheduler().runTaskTimer(Main.instance, new Runnable() {
            boolean firstTime = true;

            @Override
            public void run() {
                Time time = Main.instance.getDailyChallenge().getTimeChallenge();
                com.HeroxWar.HeroxCore.TimeGesture.Date.Date date = Main.instance.getDailyChallenge().getDate();

                if (firstTime) {
                    firstTime = false;
                    checkStart.cancel();
                }

                if (time.getMilliseconds() <= 0) {
                    System.out.println("NEXT TIME " + time.getMilliseconds());
                    Main.instance.getDailyChallenge().nextChallenge(resetPoints, rankingReward, randomReward, numberOfRewardedPlayer, numberOfTop, "Challenge Time Finished", false);
                } else {
                    if (time.getMilliseconds() % 10000 == 0) {
                        Main.db.updateChallenge(Main.instance.getDailyChallenge().getChallengeName(), time.getMilliseconds());
                    }
                    Main.instance.getDailyChallenge().setTimeChallenge(time.differenceBetween(new Time(0, 0, 0, 1, ':')));
                }

                if (adjust) {
                    com.HeroxWar.HeroxCore.TimeGesture.Date.Date now = new com.HeroxWar.HeroxCore.TimeGesture.Date.Date();
                    if (date.getDay() != now.getDay() || date.getMonth() != now.getMonth()) {
                        System.out.println("NEXT DAY");
                        Main.instance.getDailyChallenge().nextChallenge(resetPoints, rankingReward, randomReward, numberOfRewardedPlayer, numberOfTop, "Day is finished", false);
                    } else {
                        com.HeroxWar.HeroxCore.TimeGesture.Date.Date difference = date.differenceBetween(now);
                        Main.instance.getDailyChallenge().setTimeChallenge(new Time(difference.getMilliseconds(), ':'));
                    }
                }
            }
        }, 0, 20L);
        tasks.add(checkDay);
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
