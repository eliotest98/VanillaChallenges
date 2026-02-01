package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import java.util.HashMap;

public class HealthRegenEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("HealthRegenEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final HashMap<String, Double> playersRegen = new HashMap<>();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHealthRegen(org.bukkit.event.entity.EntityRegainHealthEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final EntityRegainHealthEvent.RegainReason reason = e.getRegainReason();
        final double amount = e.getAmount();
        Player player = null;
        if (e.getEntity() instanceof Player) {
            player = (Player) e.getEntity();
        }
        if (player == null) {
            if (debugActive) {
                debugUtils.addLine("This is not a player");
            }
            return;
        }
        Player finalPlayer = player;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            String playerName = finalPlayer.getName();
            String worldName = finalPlayer.getWorld().getName();
            if (debugActive) {
                debugUtils.addLine("PlayerRegen= " + playerName);
            }

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(playerName))) {
                    if (debugActive) {
                        debugUtils.addLine("Player is inside his own island");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("Player isn't inside his own island");
                        debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if(!Controls.hasPermission(playerName)) {
                return;
            }

            if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if (Controls.isCause(reason.toString(), debugActive, debugUtils, tempo)) {
                return;
            }

            if (playersRegen.get(playerName) == null) {
                playersRegen.put(playerName, amount);
            } else {
                double oldAmount = playersRegen.get(playerName);
                oldAmount = oldAmount + amount;
                if (oldAmount > 1.0) {
                    double truncate = Math.floor(oldAmount);
                    Main.instance.getDailyChallenge().increment(playerName, (long) (truncate * point));
                    playersRegen.replace(playerName, oldAmount - truncate);
                } else {
                    playersRegen.replace(playerName, oldAmount);
                }
            }

            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }

}
