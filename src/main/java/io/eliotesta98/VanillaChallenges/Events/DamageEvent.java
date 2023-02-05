package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class DamageEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("DamageEvent");
    private final ArrayList<String> causes = Main.dailyChallenge.getCauses();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(org.bukkit.event.entity.EntityDamageEvent e) {
        long tempo = System.currentTimeMillis();
        final String causePlayer = e.getCause().toString();
        final Entity entity = e.getEntity();
        final double finalDamage = e.getFinalDamage();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if(entity instanceof Player) {
                String playerName = ((Player) entity).getPlayer().getName();
                String worldName = entity.getWorld().getName();
                if (debugActive) {
                    debugUtils.addLine("DamageEvent PlayerDamaging= " + playerName);
                }
                if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                    if (debugActive) {
                        debugUtils.addLine("DamageEvent WorldsConfig= " + worldsEnabled);
                        debugUtils.addLine("DamageEvent PlayerWorld= " + worldName);
                        debugUtils.addLine("DamageEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("DamageEvent");
                    }
                    return;
                }
                if(!causes.isEmpty() && !causes.contains(causePlayer)) {
                    if (debugActive) {
                        debugUtils.addLine("DamageEvent CausePlayer= " + causePlayer);
                        debugUtils.addLine("DamageEvent CauseConfig= " + causes);
                        debugUtils.addLine("DamageEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("DamageEvent");
                    }
                    return;
                }
                Main.dailyChallenge.increment(playerName, (long) finalDamage * point);
            }
            if (debugActive) {
                debugUtils.addLine("DamageEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("DamageEvent");
            }
        });
    }
}

