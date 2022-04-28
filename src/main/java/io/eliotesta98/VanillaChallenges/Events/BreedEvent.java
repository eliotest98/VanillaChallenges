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
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreedAnimals(org.bukkit.event.entity.EntityBreedEvent e) {
        long tempo = System.currentTimeMillis();
        String playerName = "";
        if (e.getBreeder() != null) {
            playerName = e.getBreeder().getName();
        } else {
            if (debugActive) {
                debugUtils.addLine("BreedEvent Breeder= null");
                debugUtils.addLine("BreedEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BreedEvent");
            }
            return;
        }
        final String pName = playerName;
        final String mobBreeded = e.getEntity().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent PlayerBreeding= " + pName);
                }
                if (mobBreed.equalsIgnoreCase("ALL")) {
                    Main.dailyChallenge.increment(pName, point);
                    if (debugActive) {
                        debugUtils.addLine("BreedEvent Conditions= 0");
                        debugUtils.addLine("BreedEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("BreedEvent");
                    }
                    return;
                } else {
                    if (debugActive) {
                        debugUtils.addLine("BreedEvent MobBreedByPlayer= " + mobBreeded);
                        debugUtils.addLine("BreedEvent MobBreedConfig= " + mobBreed);
                    }
                    if (mobBreed.equalsIgnoreCase(mobBreeded)) {
                        if (debugActive) {
                            debugUtils.addLine("BreedEvent Conditions= 1");
                        }
                        Main.dailyChallenge.increment(pName, point);
                        if (debugActive) {
                            debugUtils.addLine("BreedEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("BreedEvent");
                        }
                        return;
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}

