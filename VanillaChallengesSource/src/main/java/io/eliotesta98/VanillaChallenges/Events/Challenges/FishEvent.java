package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class FishEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("FishEvent");
    private final ArrayList<String> fish = Main.instance.getDailyChallenge().getItems();
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerFishEvent(org.bukkit.event.player.PlayerFishEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        if (e.getCaught() == null) {
            if (debugActive) {
                debugUtils.addLine("Caugh= null");
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return;
        }
        final String playerName = e.getPlayer().getName();
        final String fishCaugh = e.getCaught().getName();
        final String worldName = e.getPlayer().getWorld().getName();
        final boolean playerSneaking = e.getPlayer().isSneaking();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerFishing= " + playerName);
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

            if (!Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if (!Controls.isSneaking(playerSneaking, debugActive, debugUtils, tempo)) {
                return;
            }

            if(!fish.isEmpty() && !fish.contains(fishCaugh)) {
                if (debugActive) {
                    debugUtils.addLine("FishCaughByPlayer= " + fishCaugh);
                    debugUtils.addLine("FishCaughConfig= " + fish);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
            Main.instance.getDailyChallenge().increment(playerName, point);
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
