package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class JumpHorseEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("JumpHorseEvent");
    private final double power = Main.dailyChallenge.getPower();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHorseJumpEvent(org.bukkit.event.entity.HorseJumpEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getEntity().getOwner() == null) {
            if (debugActive) {
                debugUtils.addLine("JumpHorseEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("JumpHorseEvent");
            }
            return;
        }
        final String playerName = e.getEntity().getOwner().getName();
        final String worldName = e.getEntity().getWorld().getName();
        final double powerJump = e.getPower();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("JumpHorseEvent PlayerJumping= " + playerName);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("JumpHorseEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("JumpHorseEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("JumpHorseEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("JumpHorseEvent");
                }
                return;
            }

            if(power != 0.0 && powerJump < power) {
                if (debugActive) {
                    debugUtils.addLine("JumpHorseEvent PowerJumpByPlayer= " + powerJump);
                    debugUtils.addLine("JumpHorseEvent PowerJumpConfig= " + power);
                    debugUtils.addLine("JumpHorseEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("JumpHorseEvent");
                }
                return;
            }
            Main.dailyChallenge.increment(playerName, point);
            if (debugActive) {
                debugUtils.addLine("JumpHorseEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("JumpHorseEvent");
            }
        });
    }
}
