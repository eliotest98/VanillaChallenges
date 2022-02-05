package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ExpCollector implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ExpCollectorEvent");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPickUpExp(org.bukkit.event.player.PlayerExpChangeEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                Main.dailyChallenge.increment(e.getPlayer().getName(),e.getAmount());
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("ExpCollectorEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ExpCollectorEvent");
        }
        return;
    }
}

