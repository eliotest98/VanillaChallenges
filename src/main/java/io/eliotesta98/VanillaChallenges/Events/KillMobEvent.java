package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KillMobEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("KillEvent");
    private String mobKill = Main.dailyChallenge.getMob();
    private int point = Main.dailyChallenge.getPoint();
    private String sneaking = Main.dailyChallenge.getSneaking();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKillEvent(org.bukkit.event.entity.EntityDeathEvent e) {
        long tempo = System.currentTimeMillis();
        String pName = "";
        boolean sneakingPlayer = false;
        if (e.getEntity().getKiller() != null) {
            pName = e.getEntity().getKiller().getName();
            sneakingPlayer = e.getEntity().getKiller().isSneaking();
        } else {
            if (debugActive) {
                debugUtils.addLine("KillEvent PlayerKilling= null");
                debugUtils.addLine("KillEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("KillEvent");
            }
            return;
        }
        final String playerName = pName;
        final String mobKilled = e.getEntity().getName();
        boolean finalSneakingPlayer = sneakingPlayer;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("KillEvent PlayerKilling= " + playerName);
                }
                if (sneaking.equalsIgnoreCase("NOBODY")) {
                    if (mobKill.equalsIgnoreCase("ALL")) {
                        if (debugActive) {
                            debugUtils.addLine("KillEvent Conditions= 0");
                        }
                        Main.dailyChallenge.increment(playerName, point);
                    } else {
                        if (debugActive) {
                            debugUtils.addLine("KillEvent MobKillByPlayer= " + mobKilled);
                            debugUtils.addLine("KillEvent MobKillConfig= " + mobKill);
                        }
                        if (mobKill.equalsIgnoreCase(mobKilled)) {
                            if (debugActive) {
                                debugUtils.addLine("KillEvent Conditions= 1");
                            }
                            Main.dailyChallenge.increment(playerName, point);
                        }
                    }
                } else {
                    if (Boolean.parseBoolean(sneaking) == finalSneakingPlayer) {
                        if (mobKill.equalsIgnoreCase("ALL")) {
                            if (debugActive) {
                                debugUtils.addLine("KillEvent Conditions= 0");
                            }
                            Main.dailyChallenge.increment(playerName, point);
                        } else {
                            if (debugActive) {
                                debugUtils.addLine("KillEvent MobKillByPlayer= " + mobKilled);
                                debugUtils.addLine("KillEvent MobKillConfig= " + mobKill);
                            }
                            if (mobKill.equalsIgnoreCase(mobKilled)) {
                                if (debugActive) {
                                    debugUtils.addLine("KillEvent Conditions= 1");
                                }
                                Main.dailyChallenge.increment(playerName, point);
                            }
                        }
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("KillEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("KillEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}
