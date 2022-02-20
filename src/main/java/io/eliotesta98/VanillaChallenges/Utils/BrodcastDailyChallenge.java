package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Challenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class BrodcastDailyChallenge {

    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private BukkitTask task;
    private final ArrayList<String> brodcastMessageTitle = Main.dailyChallenge.getTitle();
    private final String actuallyInTop = Main.instance.getConfigGestion().getMessages().get("actuallyInTop");
    private final String pointsEveryMinutes = Main.instance.getConfigGestion().getMessages().get("pointsEveryMinutes");
    private final String pointsRemainForBoosting = Main.instance.getConfigGestion().getMessages().get("pointsRemainForBoosting");

    public void start(long time) {
        execute(time);
    }

    public void stop() {
        task.cancel();
    }


    public void execute(long time) {
        task = scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                int timeResume = (Main.currentlyChallengeDB.getTimeResume() / 60) / 60;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    for (int i = 0; i < brodcastMessageTitle.size(); i++) {
                        p.sendMessage(ColorUtils.applyColor(brodcastMessageTitle.get(i).replace("{hours}", timeResume + "")));
                    }
                    ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(3);
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
                        p.sendMessage(ColorUtils.applyColor(pointsRemainForBoosting.replace("{points}", pointsRemain + "")));
                    }
                }
                Main.dailyChallenge.getMin10PlayersPoints().clear();
            }
        }, 0, time);
    }

}
