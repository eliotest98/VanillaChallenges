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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BlockBreakEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockBreakEvent");
    private final ArrayList<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private final ArrayList<String> itemsInHand = Main.instance.getDailyChallenge().getItemsInHand();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final boolean worldGuardEnabled = Main.instance.getConfigGestion().getHooks().get("WorldGuard");
    private final boolean griefPreventionEnabled = Main.instance.getConfigGestion().getHooks().get("GriefPrevention");
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private boolean ok = false;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final String blockBreaking = e.getBlock().getType().toString();
        final ItemStack itemInMainHand;
        if (Main.version113) {
            itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
        } else {
            itemInMainHand = e.getPlayer().getInventory().getItemInHand();
        }
        final Player player = e.getPlayer();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        final Location location = e.getPlayer().getLocation();
        final World world = e.getPlayer().getWorld();
        final Block block = e.getBlock();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("BlockBreakEvent PlayerBreaking= " + player.getName());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                if (griefPreventionEnabled) {
                    ok = GriefPreventionUtils.isReasonOk(player, block, e);
                }
            });
            if (ok) {
                ok = false;
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent Player is not trusted at Claim");
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
            }
            if (landsEnabled) {
                if (LandsUtils.isTrusted(location, player.getName())) {
                    if (debugActive) {
                        debugUtils.addLine("BlockBreakEvent Player is trusted at Land");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("BlockBreakEvent Player is not trusted at Land");
                        debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("BlockBreakEvent");
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
                    debugUtils.addLine("BlockBreakEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("BlockBreakEvent PlayerWorld= " + world.getName());
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
            }

            if (!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent ConfigSneaking= " + sneaking);
                    debugUtils.addLine("BlockBreakEvent PlayerSneaking= " + sneakingPlayer);
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
            }

            if (!blocks.isEmpty() && !blocks.contains(blockBreaking)) {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent BlockConfig= " + blocks);
                    debugUtils.addLine("BlockBreakEvent BlockBreaking= " + blockBreaking);
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
            }

            if (!itemsInHand.isEmpty() && !itemsInHand.contains(itemInMainHand.getType().toString())) {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent ItemInHandConfig= " + itemsInHand);
                    debugUtils.addLine("BlockBreakEvent ItemInHandPlayer= " + itemInMainHand.getType());
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
            }

            Main.instance.getDailyChallenge().increment(player.getName(), point);
            if (debugActive) {
                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BlockBreakEvent");
            }
        });
    }
}
