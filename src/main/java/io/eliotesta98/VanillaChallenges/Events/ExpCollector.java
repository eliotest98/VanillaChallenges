package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpCollector implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ExpCollectorEvent");
    private int point = Main.dailyChallenge.getPoint();
    private String sneaking = Main.dailyChallenge.getSneaking();
    private boolean prevention = Main.dailyChallenge.isKeepInventory();

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
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("ExpCollectorEvent PlayerCollecting= " + playerName);
                    debugUtils.addLine("ExpCollectorEvent PlayerSneaking= " + sneakingPlayer);
                    debugUtils.addLine("ExpCollectorEvent ConfigSneaking= " + sneaking);
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
                return;
            }
        });
    }
}

