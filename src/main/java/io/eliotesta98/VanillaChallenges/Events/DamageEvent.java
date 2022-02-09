package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class DamageEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("DamageEvent");
    private String cause = Main.dailyChallenge.getCause();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.entity.EntityDamageEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (e.getEntity().getName().equalsIgnoreCase(player.getName())) {
                        if(cause.equalsIgnoreCase("ALL")) {
                            Main.dailyChallenge.increment(e.getEntity().getName(), (int) e.getFinalDamage());
                        } else {
                            if(cause.equalsIgnoreCase(e.getCause().toString())) {
                                Main.dailyChallenge.increment(e.getEntity().getName(), (int) e.getFinalDamage());
                            }
                        }
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("DamageEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("DamageEvent");
        }
        return;
    }
}

