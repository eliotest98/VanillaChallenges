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
        if (e.getEntity().getOwner() == null) {
            return;
        }
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (power == 0.0) {
                    Main.dailyChallenge.increment(e.getEntity().getOwner().getName(), point);
                } else {
                    if (e.getPower() >= power) {
                        Main.dailyChallenge.increment(e.getEntity().getOwner().getName(), point);
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("JumpHorseEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("JumpHorseEvent");
        }
        return;
    }
}
