package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class MoveEvent implements Listener {

    private HashMap<String, Double> distances = new HashMap<String, Double>();
    private DebugUtils debugUtils = new DebugUtils();
    private String block = Main.dailyChallenge.getBlock();
    private String item = Main.dailyChallenge.getItem();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("MoveEvent");
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getTo() == null) {
            if (debugActive) {
                debugUtils.addLine("MoveEvent EndLocation= null");
                debugUtils.addLine("MoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("MoveEvent");
            }
            return;
        }

        final String playerName = e.getPlayer().getName();
        final Location from = e.getFrom();
        final Location to = e.getTo();
        final String materialFrom = from.getBlock().getLocation().subtract(0,1,0).getBlock().getType().toString();
        final String materialTo = to.getBlock().getLocation().subtract(0,1,0).getBlock().getType().toString();
        final String playerMaterialInHand = e.getPlayer().getItemInHand().getType().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("MoveEvent PlayerMoving= " + playerName);
                    debugUtils.addLine("MoveEvent BlockToStepOn= " + materialTo);
                    debugUtils.addLine("MoveEvent BlockFromStepOn= " + materialFrom);
                    debugUtils.addLine("MoveEvent BlockStepOnConfig= " + block);
                    debugUtils.addLine("MoveEvent PlayerMaterialInHand= " + playerMaterialInHand);
                    debugUtils.addLine("MoveEvent PlayerMaterialInHand= " + item);
                }
                if(item.equalsIgnoreCase("ALL")) {
                    if(block.equalsIgnoreCase("ALL")) {
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
                        if(materialFrom.equalsIgnoreCase(block) && materialTo.equalsIgnoreCase(block)) {
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
                } else {
                    if(item.equalsIgnoreCase(playerMaterialInHand)) {
                        if(block.equalsIgnoreCase("ALL")) {
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
                            if(materialFrom.equalsIgnoreCase(block) && materialTo.equalsIgnoreCase(block)) {
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
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("MoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("MoveEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}

