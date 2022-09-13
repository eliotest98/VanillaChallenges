package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BlockPlaceEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockPlaceEvent");
    private String block = Main.instance.getDailyChallenge().getBlock();
    private String blockOnPlaced = Main.instance.getDailyChallenge().getBlockOnPlace();
    private int point = Main.dailyChallenge.getPoint();
    private String sneaking = Main.dailyChallenge.getSneaking();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String materialBlockOnPlaced = e.getBlockAgainst().getType().toString();
        final String materialBlockPlaced = e.getBlockPlaced().getType().toString();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("BlockPlaceEvent PlayerPlacing= " + playerName);
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
                if (!block.equalsIgnoreCase("ALL") && !block.equalsIgnoreCase(materialBlockPlaced)) {
                    if (debugActive) {
                        debugUtils.addLine("BlockPlaceEvent BlockConfig= " + block);
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
                return;
            }
        });
    }

}
