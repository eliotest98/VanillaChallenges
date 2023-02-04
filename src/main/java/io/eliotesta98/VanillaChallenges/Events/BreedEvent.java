package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class BreedEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BreedEvent");
    private final String mobBreed = Main.dailyChallenge.getMob();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreedAnimals(org.bukkit.event.entity.EntityBreedEvent e) {
        long tempo = System.currentTimeMillis();
        String playerName = "";
        String worldName = "";
        if (e.getBreeder() != null) {
            playerName = e.getBreeder().getName();
            worldName = e.getBreeder().getWorld().getName();
        } else {
            if (debugActive) {
                debugUtils.addLine("BreedEvent Breeder= null");
                debugUtils.addLine("BreedEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BreedEvent");
            }
            return;
        }
        final String pName = playerName;
        final String mobBreded = e.getEntity().getName();
        String finalWorldName = worldName;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("BreedEvent PlayerBreeding= " + pName);
            }
            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(finalWorldName)) {
                if (debugActive) {
                    debugUtils.addLine("BreedEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("BreedEvent PlayerWorld= " + finalWorldName);
                    debugUtils.addLine("BreedEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BreedEvent");
                }
                return;
            }
            if(!mobBreed.equalsIgnoreCase("ALL") && !mobBreed.equalsIgnoreCase(mobBreded)) {
                if (debugActive) {
                    debugUtils.addLine("BreedEvent MobBreedConfig= " + mobBreed);
                    debugUtils.addLine("BreedEvent MobBreded= " + mobBreed);
                    debugUtils.addLine("BreedEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BreedEvent");
                }
                return;
            }
            Main.dailyChallenge.increment(pName, point);
            if (debugActive) {
                debugUtils.addLine("BreedEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BreedEvent");
            }
        });
    }
}

