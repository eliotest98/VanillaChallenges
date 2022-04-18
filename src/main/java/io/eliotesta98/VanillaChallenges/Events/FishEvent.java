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
        long tempo = System.currentTimeMillis();
        if (e.getCaught() == null) {
            if (debugActive) {
                debugUtils.addLine("FishEvent Caugh= null");
                debugUtils.addLine("FishEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("FishEvent");
            }
            return;
        }
        final String playerName = e.getPlayer().getName();
        final String fishCaugh = e.getCaught().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                debugUtils.addLine("FishEvent PlayerFishing= " + playerName);
                if (fish.equalsIgnoreCase("ALL")) {
                    debugUtils.addLine("FishEvent Conditions= 0");
                    Main.dailyChallenge.increment(playerName, point);
                } else {
                    debugUtils.addLine("FishEvent FishCaughByPlayer= " + fishCaugh);
                    debugUtils.addLine("FishEvent FishCaughConfig= " + fish);
                    if (fish.equalsIgnoreCase(fishCaugh)) {
                        debugUtils.addLine("FishEvent Conditions= 1");
                        Main.dailyChallenge.increment(playerName, point);
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("FishEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("FishEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}
