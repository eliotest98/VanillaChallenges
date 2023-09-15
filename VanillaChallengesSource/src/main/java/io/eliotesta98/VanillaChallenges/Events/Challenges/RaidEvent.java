package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RaidEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("RaidEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRaidFinishEvent(org.bukkit.event.raid.RaidFinishEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        if (e.getWinners().isEmpty()) {
            if (debugActive) {
                debugUtils.addLine("Winners= empty");
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return;
        }
        final int totalWaves = e.getRaid().getTotalWaves();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for (Player winner : e.getWinners()) {
                String worldName = winner.getWorld().getName();
                if (debugActive) {
                    debugUtils.addLine("RaidWinner= " + winner.getName());
                }

                if (superiorSkyBlock2Enabled) {
                    if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(winner.getName()))) {
                        if (debugActive) {
                            debugUtils.addLine("Player is inside his own island");
                        }
                    } else {
                        if (debugActive) {
                            debugUtils.addLine("Player isn't inside his own island");
                            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug();
                        }
                        continue;
                    }
                }

                if (!Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                    return;
                }

                Main.dailyChallenge.increment(winner.getName(), (long) totalWaves * point);
            }
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
