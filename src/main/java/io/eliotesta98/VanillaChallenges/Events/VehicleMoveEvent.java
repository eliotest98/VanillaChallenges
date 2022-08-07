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
    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("VehicleMoveEvent");
    private int point = Main.dailyChallenge.getPoint();
    private String vehicle = Main.dailyChallenge.getVehicle();

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
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (playerVehicle != null) {
                    if (debugActive) {
                        debugUtils.addLine("VehicleMoveEvent PlayerMoving= " + playerName);
                        debugUtils.addLine("VehicleMoveEvent VehicleConfig= " + vehicle);
                        debugUtils.addLine("VehicleMoveEvent VehiclePlayer= " + playerVehicle.getType());
                    }
                    if (vehicle.equalsIgnoreCase("ALL")) {
                        if (distances.get(playerName) == null) {
                            distances.put(playerName, from.distance(to));
                        } else {
                            double old = distances.get(playerName);
                            double newDouble = old + from.distance(to);
                            if (newDouble > 1.0) {
                                Main.dailyChallenge.increment(playerName, point);
                                distances.replace(playerName, newDouble - 1.0);
                            } else {
                                distances.replace(playerName, newDouble);
                            }
                        }
                    } else {
                        if (vehicle.equalsIgnoreCase(playerVehicle.getType().toString())) {
                            if (distances.get(playerName) == null) {
                                distances.put(playerName, from.distance(to));
                            } else {
                                double old = distances.get(playerName);
                                double newDouble = old + from.distance(to);
                                if (newDouble > 1.0) {
                                    Main.dailyChallenge.increment(playerName, point);
                                    distances.replace(playerName, newDouble - 1.0);
                                } else {
                                    distances.replace(playerName, newDouble);
                                }
                            }
                        }
                    }
                    if (debugActive) {
                        debugUtils.addLine("VehicleMoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("VehicleMoveEvent");
                    }
                    return;
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}

