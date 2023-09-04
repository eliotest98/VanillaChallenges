package io.eliotesta98.VanillaChallenges.Modules.CubeGenerator;

import io.eliotesta98.CubeGenerator.api.CubeGeneratorAPI;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CubeGeneratorEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockBreakEvent");
    private final ArrayList<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private final ArrayList<String> itemsInHand = Main.instance.getDailyChallenge().getItemsInHand();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockBreakEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final String blockBreaking = e.getBlock().getType().toString();
        final Location blockLocation = e.getBlock().getLocation();
        final String itemInMainHand = e.getPlayer().getInventory().getItemInMainHand().getType().toString();
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
                    debugUtils.debug();
                }
                return;
            }

            if (!Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if (!Controls.isSneaking(sneakingPlayer, debugActive, debugUtils, tempo)) {
                return;
            }

            if(!blocks.isEmpty() && !blocks.contains(blockBreaking)) {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent BlockConfig= " + blocks);
                    debugUtils.addLine("BlockBreakEvent BlockBreaking= " + blockBreaking);
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (!Controls.isItemInHand(itemInMainHand, debugActive, debugUtils, tempo)) {
                return;
            }

            Main.instance.getDailyChallenge().increment(playerName, point);
            if (debugActive) {
                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
