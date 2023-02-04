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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class BlockPlaceEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockPlaceEvent");
    private final ArrayList<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private final String blockOnPlaced = Main.instance.getDailyChallenge().getBlockOnPlace();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final boolean worldGuardEnabled = Main.instance.getConfigGestion().getHooks().get("WorldGuard");
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String materialBlockOnPlaced = e.getBlockAgainst().getType().toString();
        final String materialBlockPlaced = e.getBlockPlaced().getType().toString();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        final Location location = e.getPlayer().getLocation();
        final World world = e.getPlayer().getWorld();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("BlockPlaceEvent PlayerPlacing= " + playerName);
            }
            if (landsEnabled) {
                boolean playerTrusted = false;
                Land land = Main.landsIntegration.getLand(location);
                if (land != null) {
                    for (UUID p : land.getTrustedPlayers()) {// scorro la lista dei player membri
                        OfflinePlayer player = Bukkit.getOfflinePlayer(p);// prendo il player
                        if (player.getName().equalsIgnoreCase(playerName)) {// controllo il nome
                            if (debugActive) {
                                debugUtils.addLine("BlockPlaceEvent Player is trusted at Land");
                            }
                            playerTrusted = true;
                            break;
                        }
                    }
                    if(!playerTrusted) {
                        if (debugActive) {
                            debugUtils.addLine("BlockPlaceEvent Player is not trusted at Land");
                            debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("BlockPlaceEvent");
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
                        if(region.getValue().contains(BlockVector3.at(location.getBlockX(),location.getBlockY(),location.getBlockZ()))) {
                            if (debugActive) {
                                debugUtils.addLine("BlockPlaceEvent Player is in a region");
                                debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockPlaceEvent");
                            }
                            return;
                        }
                    }
                }
            }
            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(world.getName())) {
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
            if (!blockOnPlaced.equalsIgnoreCase("ALL") && !blockOnPlaced.equalsIgnoreCase(materialBlockOnPlaced)) {
                if (debugActive) {
                    debugUtils.addLine("BlockPlaceEvent BlockOnPlacedConfig= " + blockOnPlaced);
                    debugUtils.addLine("BlockPlaceEvent BlockOnPlaced= " + materialBlockOnPlaced);
                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockPlaceEvent");
                }
                return;
            }
            Main.instance.getDailyChallenge().increment(playerName, point);
            if (debugActive) {
                debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BlockPlaceEvent");
            }
        });
    }

}
