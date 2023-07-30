package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class RaidEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("RaidEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRaidFinishEvent(org.bukkit.event.raid.RaidFinishEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getWinners().isEmpty()) {
            if (debugActive) {
                debugUtils.addLine("RaidEvent Winners= empty");
                debugUtils.addLine("RaidEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("RaidEvent");
            }
            return;
        }
        final int totalWaves = e.getRaid().getTotalWaves();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for (Player winner : e.getWinners()) {
                String worldName = winner.getWorld().getName();
                if (debugActive) {
                    debugUtils.addLine("RaidEvent RaidWinner= " + winner.getName());
                }

                if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                    if (debugActive) {
                        debugUtils.addLine("RaidEvent WorldsConfig= " + worldsEnabled);
                        debugUtils.addLine("RaidEvent PlayerWorld= " + worldName);
                        debugUtils.addLine("RaidEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("RaidEvent");
                    }
                    continue;
                }

                Main.dailyChallenge.increment(winner.getName(), (long) totalWaves * point);
            }
            if (debugActive) {
                debugUtils.addLine("RaidEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("RaidEvent");
            }
        });
    }
}
