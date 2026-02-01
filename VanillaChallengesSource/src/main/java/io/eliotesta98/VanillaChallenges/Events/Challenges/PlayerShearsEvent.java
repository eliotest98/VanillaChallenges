package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class PlayerShearsEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("PlayerShearsEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShear(PlayerShearEntityEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final boolean playerSneaking = e.getPlayer().isSneaking();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {

            if (debugActive) {
                debugUtils.addLine("PlayerBreaking= " + playerName);
                debugUtils.addLine("PlayerSneaking= " + playerSneaking);
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

            if (Controls.isSneaking(playerSneaking, debugActive, debugUtils, tempo)) {
                return;
            }

            Main.instance.getDailyChallenge().increment(playerName, point);

            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }

}
