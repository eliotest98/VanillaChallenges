package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class JumpEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils("JumpEvent");
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("JumpEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getTo() == null) {
            if (debugActive) {
                debugUtils.addLine("EndLocation= null");
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
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
                debugUtils.addLine("PlayerMoving= " + playerName);
                debugUtils.addLine("JumpHeight= " + number);
                debugUtils.addLine("CalculationHeight= " + to.getBlockY() + " " + from.getBlockY());
            }

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(playerName))) {
                    if (debugActive) {
                        debugUtils.addLine("Player is inside his own island");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("Player isn't inside his own island");
                        debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if(!Controls.hasPermission(playerName)) {
                return;
            }

            if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if (number > 0) {
                Main.instance.getDailyChallenge().increment(playerName, (long) point * number);
            }
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}

