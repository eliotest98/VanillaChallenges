package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class BreedEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGesture().getDebug().get("BreedEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGesture().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreedAnimals(EntityBreedEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        String playerName;
        String worldName;
        if (e.getBreeder() != null) {
            playerName = e.getBreeder().getName();
            worldName = e.getBreeder().getWorld().getName();
        } else {
            if (debugActive) {
                debugUtils.addLine("Breeder= null");
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return;
        }
        final String pName = playerName;
        final String mobBreded = e.getEntity().getName();
        String finalWorldName = worldName;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerBreeding= " + pName);
            }

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(pName))) {
                    if (debugActive) {
                        debugUtils.addLine("BlockBreakEvent Player is inside his own island");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("BlockBreakEvent Player isn't inside his own island");
                        debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if(!Controls.hasPermission(playerName)) {
                return;
            }

            if (Controls.isWorldEnable(finalWorldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if (Controls.isMob(mobBreded, debugActive, debugUtils, tempo)) {
                return;
            }

            Main.instance.getDailyChallenge().increment(pName, point);
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}

