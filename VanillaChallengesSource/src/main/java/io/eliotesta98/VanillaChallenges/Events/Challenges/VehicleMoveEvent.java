package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VehicleMoveEvent implements Listener {

    private final HashMap<String, Double> distances = new HashMap<>();
    private final DebugUtils debugUtils = new DebugUtils("VehicleMoveEvent");
    private final boolean debugActive = Main.instance.getConfigGesture().getDebug().get("VehicleMoveEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGesture().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.vehicle.VehicleMoveEvent e) {
        long tempo = System.currentTimeMillis();
        List<String> players = new ArrayList<>();
        if (Main.version113) {
            for (Entity entity : e.getVehicle().getPassengers()) {
                if (entity instanceof Player) {
                    players.add(entity.getName());
                }
            }
        } else {
            Entity entity = e.getVehicle().getPassenger();
            if (entity instanceof Player) {
                players.add(entity.getName());
            }
        }
        final Location from = e.getFrom();
        final Location to = e.getTo();
        final Entity playerVehicle = e.getVehicle();
        final String worldName = playerVehicle.getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayersMoving= " + Arrays.toString(players.toArray()));
            }
            for (String playerName : players) {
                if (superiorSkyBlock2Enabled) {
                    if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(playerName))) {
                        if (debugActive) {
                            debugUtils.addLine("Player is inside his own island");
                        }
                    } else {
                        if (debugActive) {
                            debugUtils.addLine("Player isn't inside his own island");
                            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug();
                        }
                        return;
                    }
                }

                if(!Controls.hasPermission(playerName)) {
                    return;
                }

                if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                    return;
                }

                if (!Controls.isVehicle(playerVehicle.getType().toString(), debugActive, debugUtils, tempo)) {
                    return;
                }

                if (distances.get(playerName) == null) {
                    distances.put(playerName, from.distance(to));
                }
                double old = distances.get(playerName);
                double newDouble = old + from.distance(to);
                if (newDouble > 1.0) {
                    Main.instance.getDailyChallenge().increment(playerName, point);
                    distances.replace(playerName, newDouble - 1.0);
                } else {
                    distances.replace(playerName, newDouble);
                }
            }

            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}

