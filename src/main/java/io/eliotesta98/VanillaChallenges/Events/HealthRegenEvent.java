package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class HealthRegenEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("HealthRegenEvent");
    private final ArrayList<String> causes = Main.dailyChallenge.getCauses();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private final HashMap<String, Double> playersRegen = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHealthRegen(org.bukkit.event.entity.EntityRegainHealthEvent e) {
        long tempo = System.currentTimeMillis();
        final EntityRegainHealthEvent.RegainReason reason = e.getRegainReason();
        final double amount = e.getAmount();
        Player player = null;
        if (e.getEntity() instanceof Player) {
            player = (Player) e.getEntity();
        }
        if (player == null) {
            if (debugActive) {
                debugUtils.addLine("HealthRegenEvent This is not a player");
            }
            return;
        }
        Player finalPlayer = player;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            String playerName = finalPlayer.getName();
            String worldName = finalPlayer.getWorld().getName();
            if (debugActive) {
                debugUtils.addLine("HealthRegenEvent PlayerRegen= " + playerName);
            }

            if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("HealthRegenEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("HealthRegenEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("HealthRegenEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("HealthRegenEvent");
                }
                return;
            }

            if (!causes.isEmpty() && !causes.contains(reason.toString())) {
                if (debugActive) {
                    debugUtils.addLine("HealthRegenEvent CausePlayer= " + reason);
                    debugUtils.addLine("HealthRegenEvent CauseConfig= " + causes);
                    debugUtils.addLine("HealthRegenEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("HealthRegenEvent");
                }
                return;
            }

            if (playersRegen.get(playerName) == null) {
                playersRegen.put(playerName, amount);
            } else {
                double oldAmount = playersRegen.get(playerName);
                oldAmount = oldAmount + amount;
                if (oldAmount > 1.0) {
                    double truncate = Math.floor(oldAmount);
                    Main.dailyChallenge.increment(playerName, (long) (truncate * point));
                    playersRegen.replace(playerName, oldAmount - truncate);
                } else {
                    playersRegen.replace(playerName, oldAmount);
                }
            }

            if (debugActive) {
                debugUtils.addLine("HealthRegenEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("HealthRegenEvent");
            }
        });
    }

}
