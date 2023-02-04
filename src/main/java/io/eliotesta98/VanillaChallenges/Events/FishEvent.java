package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class FishEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("FishEvent");
    private final String fish = Main.dailyChallenge.getItem();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerFishEvent(org.bukkit.event.player.PlayerFishEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getCaught() == null) {
            if (debugActive) {
                debugUtils.addLine("FishEvent Caugh= null");
                debugUtils.addLine("FishEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("FishEvent");
            }
            return;
        }
        final String playerName = e.getPlayer().getName();
        final String fishCaugh = e.getCaught().getName();
        final String worldName = e.getPlayer().getWorld().getName();
        final boolean playerSneaking = e.getPlayer().isSneaking();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("FishEvent PlayerFishing= " + playerName);
                }

                if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                    if (debugActive) {
                        debugUtils.addLine("FishEvent WorldsConfig= " + worldsEnabled);
                        debugUtils.addLine("FishEvent PlayerWorld= " + worldName);
                        debugUtils.addLine("FishEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("FishEvent");
                    }
                    return;
                }

                if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != playerSneaking) {
                    if (debugActive) {
                        debugUtils.addLine("FishEvent ConfigSneaking= " + sneaking);
                        debugUtils.addLine("FishEvent PlayerSneaking= " + playerSneaking);
                        debugUtils.addLine("FishEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("FishEvent");
                    }
                    return;
                }
                if(!fish.equalsIgnoreCase("ALL") && !fish.equalsIgnoreCase(fishCaugh)) {
                    if (debugActive) {
                        debugUtils.addLine("FishEvent FishCaughByPlayer= " + fishCaugh);
                        debugUtils.addLine("FishEvent FishCaughConfig= " + fish);
                        debugUtils.addLine("FishEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("FishEvent");
                    }
                    return;
                }
                Main.dailyChallenge.increment(playerName, point);
                if (debugActive) {
                    debugUtils.addLine("FishEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("FishEvent");
                }
                return;
            }
        });
    }
}
