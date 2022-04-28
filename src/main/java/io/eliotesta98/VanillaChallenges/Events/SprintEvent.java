package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class SprintEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("SprintEvent");
    private HashMap<String, Boolean> players = new HashMap<String, Boolean>();
    private HashMap<String, Double> distances = new HashMap<String, Double>();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSprint(org.bukkit.event.player.PlayerToggleSprintEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("SprintEvent ToggledSprintPlayer= " + e.getPlayer().getName());
                }
                if (players.get(e.getPlayer().getName()) != null) {
                    boolean sprint = players.get(e.getPlayer().getName());
                    if (sprint) {
                        players.replace(e.getPlayer().getName(), false);
                    } else {
                        players.replace(e.getPlayer().getName(), true);
                    }
                } else {
                    players.put(e.getPlayer().getName(), true);
                }
                if (debugActive) {
                    debugUtils.addLine("SprintEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("SprintEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getTo() == null) {
            if (debugActive) {
                debugUtils.addLine("SprintEvent EndLocation= null");
                debugUtils.addLine("SprintEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("SprintEvent");
            }
            return;
        }
        final String playerName = e.getPlayer().getName();
        final Location from = e.getFrom();
        final Location to = e.getTo();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("SprintEvent PlayerSprinting= " + playerName);
                }
                if (players.get(playerName) != null) {
                    if (players.get(playerName)) {
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
                    debugUtils.addLine("SprintEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("SprintEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}