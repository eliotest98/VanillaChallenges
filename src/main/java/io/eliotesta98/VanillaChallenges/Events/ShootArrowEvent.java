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
    private String onGround = Main.dailyChallenge.getOnGround();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShootEvent(org.bukkit.event.entity.EntityShootBowEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getEntity().getName();
        final double forceShoot = e.getForce();
        final boolean onGroundPlayer = e.getEntity().isOnGround();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("ShootArrowEvent PlayerShooting= " + playerName);
                    debugUtils.addLine("ShootArrowEvent ForceShootByPlayer= " + forceShoot);
                    debugUtils.addLine("ShootArrowEvent ForceShootConfig= " + force);
                    debugUtils.addLine("ShootArrowEvent OnGroundConfig= " + onGround);
                    debugUtils.addLine("ShootArrowEvent OnGroundPlayer= " + onGroundPlayer);
                }
                if (onGround.equalsIgnoreCase("NOBODY")) {
                    if (force == 0.0) {
                        Player p = Bukkit.getPlayer(playerName);
                        if (p != null) {
                            Main.dailyChallenge.increment(playerName, point);
                        }
                    } else {
                        if (forceShoot >= force) {
                            Player p = Bukkit.getPlayer(playerName);
                            if (p != null) {
                                Main.dailyChallenge.increment(playerName, point);
                            }
                        }
                    }
                } else {
                    if (onGroundPlayer == onGround.equalsIgnoreCase("true")) {
                        if (force == 0.0) {
                            Player p = Bukkit.getPlayer(playerName);
                            if (p != null) {
                                Main.dailyChallenge.increment(playerName, point);
                            }
                        } else {
                            if (forceShoot >= force) {
                                Player p = Bukkit.getPlayer(playerName);
                                if (p != null) {
                                    Main.dailyChallenge.increment(playerName, point);
                                }
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
