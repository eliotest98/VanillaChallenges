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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRaidFinishEvent(org.bukkit.event.raid.RaidFinishEvent e) {
        if(e.getWinners().isEmpty()) {
            return;
        }
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                for(Player winner : e.getWinners()) {
                    Main.dailyChallenge.increment(winner.getName(),e.getRaid().getTotalWaves() * 100L);
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("RaidEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("RaidEvent");
        }
        return;
    }
}
