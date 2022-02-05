package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KillMobEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ExpCollectorEvent");
    private String mobKill = Main.dailyChallenge.getMob();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPickUpExp(org.bukkit.event.entity.EntityDeathEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if(mobKill.equalsIgnoreCase("ALL")) {
                    Main.dailyChallenge.increment(e.getEntity().getKiller().getName());
                } else {
                    if(mobKill.equalsIgnoreCase(e.getEntity().getName())) {
                        Main.dailyChallenge.increment(e.getEntity().getKiller().getName());
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("ExpCollectorEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ExpCollectorEvent");
        }
        return;
    }
}
