package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class VehicleMoveEvent implements Listener {

    private final HashMap<String, Double> distances = new HashMap<>();
    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("VehicleMoveEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> vehicles = Main.dailyChallenge.getVehicle();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

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
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("VehicleMoveEvent PlayerMoving= " + playerName);
            }

            if (playerVehicle != null) {

                if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                    if (debugActive) {
                        debugUtils.addLine("ColorSheepEvent WorldsConfig= " + worldsEnabled);
                        debugUtils.addLine("ColorSheepEvent PlayerWorld= " + worldName);
                        debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("ColorSheepEvent");
                    }
                    return;
                }

                if(!vehicles.isEmpty() && !vehicles.contains(playerVehicle.getType().toString())) {
                    if (debugActive) {
                        debugUtils.addLine("VehicleMoveEvent VehicleConfig= " + vehicles);
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

