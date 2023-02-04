package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.CubeGenerator.api.CubeGeneratorAPI;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CubeGeneratorEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockBreakEvent");
    private final ArrayList<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private final ArrayList<String> itemsInHand = Main.instance.getDailyChallenge().getItemsInHand();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final String blockBreaking = e.getBlock().getType().toString();
        final Location blockLocation = e.getBlock().getLocation();
        final ItemStack itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
        final String playerName = e.getPlayer().getName();
        final String worldName = e.getPlayer().getWorld().getName();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            int idCube = CubeGeneratorAPI.getGeneratorIdFromLocation(blockLocation);
            if (debugActive) {
                debugUtils.addLine("BlockBreakEvent PlayerBreaking= " + playerName);
            }
            if(idCube == -1) {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent IdGeneratorInvalid= " + idCube);
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
            }
            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("BlockBreakEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
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
            if(!blocks.isEmpty() && !blocks.contains(blockBreaking)) {
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
            Main.instance.getDailyChallenge().increment(playerName, point);
            if (debugActive) {
                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BlockBreakEvent");
            }
        });
    }
}
