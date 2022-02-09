package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KillMobEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("KillEvent");
    private String mobKill = Main.dailyChallenge.getMob();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKillEvent(org.bukkit.event.entity.EntityDeathEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if(mobKill.equalsIgnoreCase("ALL")) {
                    if(e.getEntity().getKiller() != null) {
                        Main.dailyChallenge.increment(e.getEntity().getKiller().getName());
                    }
                } else {
                    if(mobKill.equalsIgnoreCase(e.getEntity().getName())) {
                        if(e.getEntity().getKiller() != null) {
                            Main.dailyChallenge.increment(e.getEntity().getKiller().getName());
                        }
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("KillEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("KillEvent");
        }
        return;
    }
}
