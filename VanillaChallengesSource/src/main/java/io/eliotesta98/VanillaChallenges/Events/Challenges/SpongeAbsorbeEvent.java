package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class SpongeAbsorbeEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGesture().getDebug().get("SpongeAbsorbEvent");
    private final HashMap<String, String> players = new HashMap<>();
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGesture().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType().toString().equalsIgnoreCase("SPONGE")) {
            if (players.get(e.getBlockPlaced().getLocation().toString()) == null) {
                players.put(e.getBlockPlaced().getLocation().toString(), e.getPlayer().getName());
            } else {
                players.replace(e.getBlockPlaced().getLocation().toString(), e.getPlayer().getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAbsorb(org.bukkit.event.block.SpongeAbsorbEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final Block spongeBlock = e.getBlock();
        final int amount = e.getBlocks().size();
        final String worldName = e.getBlock().getWorld().getName();
        final String playerName = players.get(spongeBlock.getLocation().toString());
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerAbsorbing= " + players.get(spongeBlock.getLocation().toString()));
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

            if(!Controls.hasPermission(playerName)) {
                return;
            }

            if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if (players.get(spongeBlock.getLocation().toString()) != null) {
                Main.instance.getDailyChallenge().increment(players.get(spongeBlock.getLocation().toString()), (long) amount * point);
                players.remove(playerName);
            }
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}

