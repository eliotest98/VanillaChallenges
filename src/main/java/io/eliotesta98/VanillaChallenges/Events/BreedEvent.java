package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BreedEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BreedEvent");
    private String mobBreed = Main.dailyChallenge.getMob();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreedAnimals(org.bukkit.event.entity.EntityBreedEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if(mobBreed.equalsIgnoreCase("ALL")) {
                    if(e.getBreeder() != null) {
                        Main.dailyChallenge.increment(e.getBreeder().getName());
                    }
                } else {
                    if(mobBreed.equalsIgnoreCase(e.getEntity().getName())) {
                        if(e.getBreeder() != null) {
                            Main.dailyChallenge.increment(e.getBreeder().getName());
                        }
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("BreedEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("BreedEvent");
        }
        return;
    }
}

