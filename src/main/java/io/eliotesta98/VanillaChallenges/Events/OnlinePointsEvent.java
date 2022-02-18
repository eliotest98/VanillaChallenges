package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class OnlinePointsEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("RaidEvent");
    private int point = Main.instance.getConfigGestion().getPointsOnlinePoints();
    private int minutes = Main.instance.getConfigGestion().getMinutesOnlinePoints();
    HashMap<String, Integer> times = new HashMap<String, Integer>();
    private BukkitTask task;

    public OnlinePointsEvent() {
        check();
    }

    public void check() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (times.get(p.getName()) == null) {
                        times.put(p.getName(), 1);
                    } else {
                        times.replace(p.getName(), times.get(p.getName()) + 1);
                    }
                    if (times.get(p.getName()) >= minutes) {
                        Main.dailyChallenge.increment(p.getName(), point);
                        times.replace(p.getName(), times.get(p.getName()) - minutes);
                    }
                }
            }
        }, 0, (long) 60 * 20);
    }

    public void stop() {
        times.clear();
        task.cancel();
    }

}
