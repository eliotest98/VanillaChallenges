package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class JumpHorseEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("JumpHorseEvent");
    private double power = Main.dailyChallenge.getPower();
    private int point = Main.dailyChallenge.getPoint();

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
        final double powerJump = e.getPower();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("JumpHorseEvent PlayerJumping= " + playerName);
                }
                if(power != 0.0 && powerJump < power) {
                    if (debugActive) {
                        debugUtils.addLine("JumpHorseEvent PowerJumpByPlayer= " + powerJump);
                        debugUtils.addLine("JumpHorseEvent PowerJumpConfig= " + power);
                        debugUtils.addLine("JumpHorseEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("JumpHorseEvent");
                    }
                }
                Main.dailyChallenge.increment(playerName, point);
                if (debugActive) {
                    debugUtils.addLine("JumpHorseEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("JumpHorseEvent");
                }
            }
        });
    }
}
