package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.GriefPrevention.GriefPreventionUtils;
import io.eliotesta98.VanillaChallenges.Modules.Lands.LandsUtils;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Modules.WorldGuard.WorldGuardUtils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

public class LeashEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("LeashEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean griefPreventionEnabled = Main.instance.getConfigGestion().getHooks().get("GriefPrevention");
    private final boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");
    private final boolean worldGuardEnabled = Main.instance.getConfigGestion().getHooks().get("WorldGuard");
    private boolean ok = false;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeash(PlayerLeashEntityEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();

        final Player player = e.getPlayer();
        final boolean sneakingPlayer = player.isSneaking();
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final Block block = location.getBlock();
        final String mob = e.getEntity().getType().getKey().getKey();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerBreaking= " + player.getName());
            }
            Bukkit.getScheduler().runTask(Main.instance, () -> {
                if (griefPreventionEnabled) {
                    ok = GriefPreventionUtils.isReasonOk(player, block, e);
                }
            });
            if (ok) {
                ok = false;
                if (debugActive) {
                    debugUtils.addLine("Player is not trusted at Claim");
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (landsEnabled) {
                if (LandsUtils.isTrusted(location, player.getName())) {
                    if (debugActive) {
                        debugUtils.addLine("Player is trusted at Land");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("Player is not trusted at Land");
                        debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if (worldGuardEnabled) {
                if (WorldGuardUtils.isARagion(world, location)) {
                    if (debugActive) {
                        debugUtils.addLine("Player is in a region");
                        debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(player.getName()))) {
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

            if(!Controls.hasPermission(player.getName())) {
                return;
            }

            if (Controls.isWorldEnable(world.getName(), debugActive, debugUtils, tempo)) {
                return;
            }

            if (Controls.isSneaking(sneakingPlayer, debugActive, debugUtils, tempo)) {
                return;
            }

            if (Controls.isMob(mob, debugActive, debugUtils, tempo)) {
                return;
            }

            Main.instance.getDailyChallenge().increment(player.getName(), point);

            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
