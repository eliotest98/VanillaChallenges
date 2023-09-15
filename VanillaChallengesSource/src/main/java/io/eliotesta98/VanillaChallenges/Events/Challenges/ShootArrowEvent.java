package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShootArrowEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ShootArrowEvent");
    private final double force = Main.dailyChallenge.getForce();
    private final String onGround = Main.dailyChallenge.getOnGround();
    private final int point = Main.dailyChallenge.getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShootEvent(org.bukkit.event.entity.EntityShootBowEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final String playerName = e.getEntity().getName();
        final String worldName = e.getEntity().getWorld().getName();
        final double forceShoot = e.getForce();
        final boolean onGroundPlayer = e.getEntity().isOnGround();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerShooting= " + playerName);
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

            if (!Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if (!onGround.equalsIgnoreCase("NOBODY") && Boolean.getBoolean(onGround) != onGroundPlayer) {
                if (debugActive) {
                    debugUtils.addLine("OnGroundConfig= " + onGround);
                    debugUtils.addLine("OnGroundPlayer= " + onGroundPlayer);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (force != 0.0 && forceShoot < force) {
                if (debugActive) {
                    debugUtils.addLine("ForceShootByPlayer= " + forceShoot);
                    debugUtils.addLine("ForceShootConfig= " + force);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            Player p = Bukkit.getPlayer(playerName);
            if (p != null) {
                Main.dailyChallenge.increment(playerName, point);
            }

            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
