package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShootArrowEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ShootArrowEvent");
    private double force = Main.dailyChallenge.getForce();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShootEvent(org.bukkit.event.entity.EntityShootBowEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if(force == 0.0) {
                    for(Player player: Bukkit.getOnlinePlayers()){
                        if(player.getName().equalsIgnoreCase(e.getEntity().getName())) {
                            Main.dailyChallenge.increment(e.getEntity().getName());
                            break;
                        }
                    }
                } else {
                    if(e.getForce() >= force) {
                        for(Player player: Bukkit.getOnlinePlayers()){
                            if(player.getName().equalsIgnoreCase(e.getEntity().getName())) {
                                Main.dailyChallenge.increment(e.getEntity().getName());
                                break;
                            }
                        }
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("ShootArrowEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ShootArrowEvent");
        }
        return;
    }
}
