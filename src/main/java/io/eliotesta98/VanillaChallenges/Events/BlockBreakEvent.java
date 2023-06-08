package io.eliotesta98.VanillaChallenges.Events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import me.angeschossen.lands.api.land.Land;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

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
        if(Main.version113) {
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
                    String reason = GriefPrevention.instance.allowBreak(player, block, block.getLocation(), e);
                    if (reason != null) {
                        this.ok = true;
                    }
                }
            });
            if(ok) {
                ok = false;
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent Player is not trusted at Claim");
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
            }
            if (landsEnabled) {
                boolean playerTrusted = false;
                Land land = Main.landsIntegration.getArea(location).getLand();
                if (land != null) {
                    for (UUID p : land.getTrustedPlayers()) {// scorro la lista dei player membri
                        OfflinePlayer playerOff = Bukkit.getOfflinePlayer(p);// prendo il player
                        if (playerOff.getName().equalsIgnoreCase(player.getName())) {// controllo il nome
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent Player is trusted at Land");
                            }
                            playerTrusted = true;
                            break;
                        }
                    }
                    if (!playerTrusted) {
                        if (debugActive) {
                            debugUtils.addLine("BlockBreakEvent Player is not trusted at Land");
                            debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("BlockBreakEvent");
                        }
                        return;
                    }
                }
            }
            if (worldGuardEnabled) {
                // prendo il container della regione
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                // prendo le regions del mondo
                RegionManager regions = container.get(BukkitAdapter.adapt(world));
                if (regions != null) {
                    // controllo se c'è quella region
                    for (Map.Entry<String, ProtectedRegion> region : regions.getRegions().entrySet()) {
                        if (region.getValue().contains(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))) {
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent Player is in a region");
                                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockBreakEvent");
                            }
                            return;
                        }
                    }
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
