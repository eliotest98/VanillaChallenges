package io.eliotesta98.VanillaChallenges.Events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.eliotesta98.Tombs.Database.Player;
import io.eliotesta98.Tombs.Utils.ExpUtils;
import io.eliotesta98.Tombs.Utils.ItemStacks;
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
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;

public class BlockBreakEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockBreakEvent");
    private String block = Main.instance.getDailyChallenge().getBlock();
    private String itemInHand = Main.instance.getDailyChallenge().getItemInHand();
    private int point = Main.dailyChallenge.getPoint();
    private String sneaking = Main.dailyChallenge.getSneaking();
    private boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private boolean worldGuardEnabled = Main.instance.getConfigGestion().getHooks().get("WorldGuard");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final String blockBreaking = e.getBlock().getType().toString();
        final ItemStack itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
        final String playerName = e.getPlayer().getName();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        final Location location = e.getPlayer().getLocation();
        final World world = e.getPlayer().getWorld();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent PlayerBreaking= " + playerName);
                }
                if (landsEnabled) {
                    boolean playerTrusted = false;
                    Land land = Main.landsIntegration.getLand(location);
                    if (land != null) {
                        for (UUID p : land.getTrustedPlayers()) {// scorro la lista dei player membri
                            OfflinePlayer player = Bukkit.getOfflinePlayer(p);// prendo il player
                            if (player.getName().equalsIgnoreCase(playerName)) {// controllo il nome
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent Player is trusted at Land");
                                }
                                playerTrusted = true;
                                break;
                            }
                        }
                        if(!playerTrusted) {
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
                            if(region.getValue().contains(BlockVector3.at(location.getBlockX(),location.getBlockY(),location.getBlockZ()))) {
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
                if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                    if (debugActive) {
                        debugUtils.addLine("BlockBreakEvent ConfigSneaking= " + sneaking);
                        debugUtils.addLine("BlockBreakEvent PlayerSneaking= " + sneakingPlayer);
                        debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("BlockBreakEvent");
                    }
                    return;
                }
                if(!block.equalsIgnoreCase("ALL") && !block.equalsIgnoreCase(blockBreaking)) {
                    if (debugActive) {
                        debugUtils.addLine("BlockBreakEvent BlockConfig= " + block);
                        debugUtils.addLine("BlockBreakEvent BlockBreaking= " + blockBreaking);
                        debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("BlockBreakEvent");
                    }
                    return;
                }
                if (!itemInHand.equalsIgnoreCase("ALL") &&
                        !itemInMainHand.getType().toString().equalsIgnoreCase(itemInHand)) {
                    if (debugActive) {
                        debugUtils.addLine("BlockBreakEvent ItemInHandConfig= " + itemInHand);
                        debugUtils.addLine("BlockBreakEvent ItemInHandPlayer= " + itemInMainHand.getType());
                        debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("BlockBreakEvent");
                    }
                    return;
                }
                Main.instance.getDailyChallenge().increment(playerName, point);
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
            }
        });
    }
}
