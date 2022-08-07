package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DyeEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("DyeEvent");
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
        long tempo = System.currentTimeMillis();
        String playerName = e.getEntity().getName();
        if (debugActive) {
            debugUtils.addLine("DyeEvent PlayerDye= " + playerName);
        }
        Main.dailyChallenge.increment(playerName, point);
        if (debugActive) {
            debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("DyeEvent");
        }
        return;
    }
}
