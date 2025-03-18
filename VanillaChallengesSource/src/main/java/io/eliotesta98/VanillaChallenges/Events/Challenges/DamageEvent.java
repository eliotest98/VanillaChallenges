package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DamageEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGesture().getDebug().get("DamageEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGesture().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(org.bukkit.event.entity.EntityDamageEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final String causePlayer = e.getCause().toString();
        final Entity entity = e.getEntity();
        final double finalDamage = e.getFinalDamage();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (entity instanceof Player) {
                String playerName = ((Player) entity).getPlayer().getName();
                String worldName = entity.getWorld().getName();
                if (debugActive) {
                    debugUtils.addLine("PlayerDamaging= " + playerName);
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

                if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                    return;
                }

                if (Controls.isCause(causePlayer, debugActive, debugUtils, tempo)) {
                    return;
                }

                double damage = finalDamage * point;
                if (damage > 0) {
                    Main.instance.getDailyChallenge().increment(playerName, (long) finalDamage * point);
                }
            }
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}

