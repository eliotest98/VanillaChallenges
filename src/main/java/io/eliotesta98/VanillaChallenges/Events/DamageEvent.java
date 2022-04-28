package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DamageEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("DamageEvent");
    private String cause = Main.dailyChallenge.getCause();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.entity.EntityDamageEvent e) {
        long tempo = System.currentTimeMillis();
        final String causePlayer = e.getCause().toString();
        final String playerName = e.getEntity().getName();
        final double finalDamage = e.getFinalDamage();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent PlayerDamaging= " + playerName);
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (playerName.equalsIgnoreCase(player.getName())) {
                        if (cause.equalsIgnoreCase("ALL")) {
                            if (debugActive) {
                                debugUtils.addLine("DamageEvent Conditions= 0");
                            }
                            Main.dailyChallenge.increment(playerName, (long) finalDamage * point);
                        } else {
                            if (debugActive) {
                                debugUtils.addLine("DamageEvent CausePlayer= " + causePlayer);
                                debugUtils.addLine("DamageEvent CauseConfig= " + cause);
                            }
                            if (cause.equalsIgnoreCase(causePlayer)) {
                                if (debugActive) {
                                    debugUtils.addLine("DamageEvent Conditions= 1");
                                }
                                Main.dailyChallenge.increment(playerName, (long) finalDamage * point);
                            }
                        }
                        break;
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("DamageEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("DamageEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}

