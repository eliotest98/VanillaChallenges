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
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShootEvent(org.bukkit.event.entity.EntityShootBowEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getEntity().getName();
        final double forceShoot = e.getForce();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                debugUtils.addLine("ShootArrowEvent PlayerShooting= " + playerName);
                if (force == 0.0) {
                    debugUtils.addLine("ShootArrowEvent Conditions= 0");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().equalsIgnoreCase(playerName)) {
                            Main.dailyChallenge.increment(playerName, point);
                            break;
                        }
                    }
                } else {
                    debugUtils.addLine("ShootArrowEvent ForceShootByPlayer= " + forceShoot);
                    debugUtils.addLine("ShootArrowEvent ForceShootConfig= " + force);
                    if (forceShoot >= force) {
                        debugUtils.addLine("ShootArrowEvent Conditions= 1");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getName().equalsIgnoreCase(playerName)) {
                                Main.dailyChallenge.increment(playerName, point);
                                break;
                            }
                        }
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("ShootArrowEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ShootArrowEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}
