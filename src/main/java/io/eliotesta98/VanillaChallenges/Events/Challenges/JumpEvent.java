package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class JumpEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("JumpEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getTo() == null) {
            if (debugActive) {
                debugUtils.addLine("JumpEvent EndLocation= null");
                debugUtils.addLine("JumpEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("JumpEvent");
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
                debugUtils.addLine("JumpEvent PlayerMoving= " + playerName);
                debugUtils.addLine("JumpEvent JumpHeight= " + number);
                debugUtils.addLine("JumpEvent CalculationHeight= " + to.getBlockY() + " " + from.getBlockY());
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("JumpEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("JumpEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("JumpEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("JumpEvent");
                }
                return;
            }

            if (number > 0) {
                Main.dailyChallenge.increment(playerName, (long) point * number);
            }
            if (debugActive) {
                debugUtils.addLine("JumpEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("JumpEvent");
            }
        });
    }
}

