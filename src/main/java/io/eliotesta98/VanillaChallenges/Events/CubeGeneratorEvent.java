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

public class CubeGeneratorEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockBreakEvent");
    private String block = Main.instance.getDailyChallenge().getBlock();
    private String itemInHand = Main.instance.getDailyChallenge().getItemInHand();
    private int point = Main.dailyChallenge.getPoint();
    private String sneaking = Main.dailyChallenge.getSneaking();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final String blockBreaking = e.getBlock().getType().toString();
        final Location blockLocation = e.getBlock().getLocation();
        final ItemStack itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
        final String playerName = e.getPlayer().getName();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }
}
