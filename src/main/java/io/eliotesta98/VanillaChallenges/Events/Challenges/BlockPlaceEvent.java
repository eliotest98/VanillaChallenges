package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Modules.GriefPrevention.GriefPreventionUtils;
import io.eliotesta98.VanillaChallenges.Modules.Lands.LandsUtils;
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

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockPlaceEvent");
    private final ArrayList<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private final ArrayList<String> blocksOnPlaced = Main.instance.getDailyChallenge().getBlocksOnPlace();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final boolean worldGuardEnabled = Main.instance.getConfigGestion().getHooks().get("WorldGuard");
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private final boolean griefPreventionEnabled = Main.instance.getConfigGestion().getHooks().get("GriefPrevention");
    private boolean ok = false;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
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
                debugUtils.addLine("BlockPlaceEvent PlayerPlacing= " + player.getName());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                if (griefPreventionEnabled) {
                    ok = GriefPreventionUtils.isReasonOk(player, block, e);
                }
            });
            if (ok) {
                ok = false;
                if (debugActive) {
                    debugUtils.addLine("BlockPlaceEvent Player is not trusted at Claim");
                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockPlaceEvent");
                }
                return;
            }
            if (landsEnabled) {
                if (LandsUtils.isTrusted(location, player.getName())) {
                    if (debugActive) {
                        debugUtils.addLine("BlockPlaceEvent Player is trusted at Land");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("BlockPlaceEvent Player is not trusted at Land");
                        debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("BlockPlaceEvent");
                    }
                    return;
                }
            }
            if (worldGuardEnabled) {
                if (WorldGuardUtils.isARagion(world, location)) {
                    if (debugActive) {
                        debugUtils.addLine("BlockPlaceEvent Player is in a region");
                        debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("BlockPlaceEvent");
                    }
                    return;
                }
            }
            if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(world.getName())) {
                if (debugActive) {
                    debugUtils.addLine("BlockPlaceEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("BlockPlaceEvent PlayerWorld= " + world.getName());
                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockPlaceEvent");
                }
                return;
            }
            if (!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("BlockPlaceEvent ConfigSneaking= " + sneaking);
                    debugUtils.addLine("BlockPlaceEvent PlayerSneaking= " + sneakingPlayer);
                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockPlaceEvent");
                }
                return;
            }
            if (!blocks.isEmpty() && !blocks.contains(materialBlockPlaced)) {
                if (debugActive) {
                    debugUtils.addLine("BlockPlaceEvent BlockConfig= " + blocks);
                    debugUtils.addLine("BlockPlaceEvent BlockPlacing= " + materialBlockPlaced);
                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockPlaceEvent");
                }
                return;
            }
            if (!blocksOnPlaced.isEmpty() && !blocksOnPlaced.contains(materialBlockOnPlaced)) {
                if (debugActive) {
                    debugUtils.addLine("BlockPlaceEvent BlockOnPlacedConfig= " + blocksOnPlaced);
                    debugUtils.addLine("BlockPlaceEvent BlockOnPlaced= " + materialBlockOnPlaced);
                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockPlaceEvent");
                }
                return;
            }
            Main.instance.getDailyChallenge().increment(player.getName(), point);
            if (debugActive) {
                debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BlockPlaceEvent");
            }
        });
    }

}
