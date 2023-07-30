package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.ArrayList;

public class DropperEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("DropperEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getTo() == null) {
            if (debugActive) {
                debugUtils.addLine("DropperEvent EndLocation= null");
                debugUtils.addLine("DropperEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("DropperEvent");
            }
            return;
        }
        final String playerName = e.getPlayer().getName();
        final String worldName = e.getPlayer().getWorld().getName();
        final Location from = e.getFrom();
        final Location to = e.getTo();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            int number = to.getBlockY() - from.getBlockY();
            if (debugActive) {
                debugUtils.addLine("DropperEvent PlayerMoving= " + playerName);
                debugUtils.addLine("DropperEvent JumpHeight= " + number);
                debugUtils.addLine("DropperEvent CalculationHeight= " + to.getBlockY() + " " + from.getBlockY());
            }

            if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("DropperEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("DropperEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("DropperEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("DropperEvent");
                }
                return;
            }

            if (number < 0) {
                Main.dailyChallenge.increment(playerName, (long) Math.negateExact(point) * number);
            }
            if (debugActive) {
                debugUtils.addLine("DropperEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("DropperEvent");
            }
        });
    }
}
