package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
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

import java.util.ArrayList;

public class BlockPlaceEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockPlaceEvent");
    private final ArrayList<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private final ArrayList<String> blocksOnPlaced = Main.instance.getDailyChallenge().getBlocksOnPlace();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final boolean worldGuardEnabled = Main.instance.getConfigGestion().getHooks().get("WorldGuard");
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private final boolean griefPreventionEnabled = Main.instance.getConfigGestion().getHooks().get("GriefPrevention");
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");
    private boolean ok = false;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final Player player = e.getPlayer();
        final String materialBlockOnPlaced = e.getBlockAgainst().getType().toString();
        final String materialBlockPlaced = e.getBlockPlaced().getType().toString();
        final Block block = e.getBlockPlaced();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        final Location location = e.getPlayer().getLocation();
        final World world = e.getPlayer().getWorld();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerPlacing= " + player.getName());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
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

            if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(world.getName())) {
                if (debugActive) {
                    debugUtils.addLine("WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("PlayerWorld= " + world.getName());
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
            if (!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("ConfigSneaking= " + sneaking);
                    debugUtils.addLine("PlayerSneaking= " + sneakingPlayer);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
            if (!blocks.isEmpty() && !blocks.contains(materialBlockPlaced)) {
                if (debugActive) {
                    debugUtils.addLine("BlockConfig= " + blocks);
                    debugUtils.addLine("BlockPlacing= " + materialBlockPlaced);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
            if (!blocksOnPlaced.isEmpty() && !blocksOnPlaced.contains(materialBlockOnPlaced)) {
                if (debugActive) {
                    debugUtils.addLine("BlockOnPlacedConfig= " + blocksOnPlaced);
                    debugUtils.addLine("BlockOnPlaced= " + materialBlockOnPlaced);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
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
