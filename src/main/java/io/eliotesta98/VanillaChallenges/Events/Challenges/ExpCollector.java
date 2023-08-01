package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.ArrayList;

public class ExpCollector implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ExpCollectorEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final boolean prevention = Main.dailyChallenge.isKeepInventory();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
        if (prevention)
            e.setKeepInventory(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPickUpExp(org.bukkit.event.player.PlayerExpChangeEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final int amount = e.getAmount();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerCollecting= " + playerName);
                debugUtils.addLine("PlayerSneaking= " + sneakingPlayer);
                debugUtils.addLine("ConfigSneaking= " + sneaking);
            }

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(playerName))) {
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

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("PlayerWorld= " + worldName);
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
            Main.dailyChallenge.increment(playerName, (long) amount * point);
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}

