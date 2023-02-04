package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.ArrayList;

public class ExpCollector implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ExpCollectorEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final boolean prevention = Main.dailyChallenge.isKeepInventory();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
        if (prevention)
            e.setKeepInventory(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPickUpExp(org.bukkit.event.player.PlayerExpChangeEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final int amount = e.getAmount();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("ExpCollectorEvent PlayerCollecting= " + playerName);
                debugUtils.addLine("ExpCollectorEvent PlayerSneaking= " + sneakingPlayer);
                debugUtils.addLine("ExpCollectorEvent ConfigSneaking= " + sneaking);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("ExpCollectorEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("ExpCollectorEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("ExpCollectorEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ExpCollectorEvent");
                }
                return;
            }

            if (!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("ExpCollectorEvent ConfigSneaking= " + sneaking);
                    debugUtils.addLine("ExpCollectorEvent PlayerSneaking= " + sneakingPlayer);
                    debugUtils.addLine("ExpCollectorEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ExpCollectorEvent");
                }
                return;
            }
            Main.dailyChallenge.increment(playerName, (long) amount * point);
            if (debugActive) {
                debugUtils.addLine("ExpCollectorEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("ExpCollectorEvent");
            }
        });
    }
}

