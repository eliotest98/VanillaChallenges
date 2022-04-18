package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RaidEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("RaidEvent");
    private int point = Main.dailyChallenge.getPoint();

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
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                for (Player winner : e.getWinners()) {
                    debugUtils.addLine("RaidEvent RaidWinner= " + winner.getName());
                    Main.dailyChallenge.increment(winner.getName(), (long) totalWaves * point);
                }
                if (debugActive) {
                    debugUtils.addLine("RaidEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("RaidEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}
