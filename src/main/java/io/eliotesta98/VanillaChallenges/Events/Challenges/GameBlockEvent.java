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
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GameBlockEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockReceiveGameEvent");
    private final ArrayList<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private final ArrayList<String> itemsInHand = Main.instance.getDailyChallenge().getItemsInHand();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> causes = Main.instance.getDailyChallenge().getCauses();
    private final boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final boolean worldGuardEnabled = Main.instance.getConfigGestion().getHooks().get("WorldGuard");
    private final boolean griefPreventionEnabled = Main.instance.getConfigGestion().getHooks().get("GriefPrevention");
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private boolean ok = false;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockGame(BlockReceiveGameEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();

        if (!(e.getEntity() instanceof Player)) {
            if (debugActive) {
                debugUtils.addLine("This Entity is: " + e.getEntity().getName());
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return;
        }

        final Player player = (Player) e.getEntity();
        final String blockBreaking = e.getBlock().getType().toString();
        final ItemStack itemInMainHand;
        final String detection = e.getEvent().getKey().getKey();
        if (Main.version113) {
            itemInMainHand = player.getInventory().getItemInMainHand();
        } else {
            itemInMainHand = player.getInventory().getItemInHand();
        }
        final boolean sneakingPlayer = player.isSneaking();
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final Block block = e.getBlock();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {

            if (debugActive) {
                debugUtils.addLine("Player= " + player.getName());
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

            if (!causes.isEmpty() && !causes.contains(detection)) {
                if (debugActive) {
                    debugUtils.addLine("CausePlayer= " + detection);
                    debugUtils.addLine("CauseConfig= " + causes);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (!blocks.isEmpty() && !blocks.contains(blockBreaking)) {
                if (debugActive) {
                    debugUtils.addLine("BlockConfig= " + blocks);
                    debugUtils.addLine("BlockBreaking= " + blockBreaking);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (!itemsInHand.isEmpty() && !itemsInHand.contains(itemInMainHand.getType().toString())) {
                if (debugActive) {
                    debugUtils.addLine("ItemInHandConfig= " + itemsInHand);
                    debugUtils.addLine("ItemInHandPlayer= " + itemInMainHand.getType());
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
