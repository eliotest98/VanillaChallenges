package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
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
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("SprintEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("SprintEvent");
        }
        return;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        if (e.getTo() == null) {
            return;
        }
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (players.get(e.getPlayer().getName()) != null) {
                    if (players.get(e.getPlayer().getName())) {
                        if (distances.get(e.getPlayer().getName()) == null) {
                            distances.put(e.getPlayer().getName(), e.getFrom().distance(e.getTo()));
                        } else {
                            double old = distances.get(e.getPlayer().getName());
                            double newDouble = old + e.getFrom().distance(e.getTo());
                            if (newDouble > 1.0) {
                                Main.dailyChallenge.increment(e.getPlayer().getName(), point);
                                distances.replace(e.getPlayer().getName(), newDouble - 1.0);
                            } else {
                                distances.replace(e.getPlayer().getName(), newDouble);
                            }
                        }
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("MoveEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("MoveEvent");
        }
        return;
    }
}