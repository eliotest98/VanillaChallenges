package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class VehicleMoveEvent implements Listener {

    private HashMap<String, Double> distances = new HashMap<String, Double>();
    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("VehicleMoveEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final String vehicle = Main.dailyChallenge.getVehicle();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getTo() == null) {
            if (debugActive) {
                debugUtils.addLine("VehicleMoveEvent EndLocation= null");
                debugUtils.addLine("VehicleMoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("VehicleMoveEvent");
            }
            return;
        }
        final String playerName = e.getPlayer().getName();
        final Location from = e.getFrom();
        final Location to = e.getTo();
        final Entity playerVehicle = e.getPlayer().getVehicle();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("VehicleMoveEvent PlayerMoving= " + playerName);
            }

            if (playerVehicle != null) {
                if(!vehicle.equalsIgnoreCase("ALL") && !vehicle.equalsIgnoreCase(playerVehicle.getType().toString())) {
                    if (debugActive) {
                        debugUtils.addLine("VehicleMoveEvent VehicleConfig= " + vehicle);
                        debugUtils.addLine("VehicleMoveEvent VehiclePlayer= " + playerVehicle.getType());
                        debugUtils.addLine("VehicleMoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("VehicleMoveEvent");
                    }
                    return;
                }
                if (distances.get(playerName) == null) {
                    distances.put(playerName, from.distance(to));
                }
                double old = distances.get(playerName);
                double newDouble = old + from.distance(to);
                if (newDouble > 1.0) {
                    Main.dailyChallenge.increment(playerName, point);
                    distances.replace(playerName, newDouble - 1.0);
                } else {
                    distances.replace(playerName, newDouble);
                }

                if (debugActive) {
                    debugUtils.addLine("VehicleMoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("VehicleMoveEvent");
                }
            }
        });
    }
}

