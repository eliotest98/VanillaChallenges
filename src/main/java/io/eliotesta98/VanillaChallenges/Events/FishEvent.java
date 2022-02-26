package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FishEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("FishEvent");
    private String fish = Main.dailyChallenge.getItem();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerFishEvent(org.bukkit.event.player.PlayerFishEvent e) {
        if (e.getCaught() == null) {
            return;
        }
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (fish.equalsIgnoreCase("ALL")) {
                    Main.dailyChallenge.increment(e.getPlayer().getName(), point);
                } else {
                    if (fish.equalsIgnoreCase(e.getCaught().getName())) {
                        Main.dailyChallenge.increment(e.getPlayer().getName(), point);
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("FishEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("FishEvent");
        }
        return;
    }
}
