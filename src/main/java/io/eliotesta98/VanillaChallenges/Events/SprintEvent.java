package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class SprintEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("SprintEvent");
    private HashMap<String, Boolean> players = new HashMap<>();
    private HashMap<String, Double> distances = new HashMap<>();
    private final ArrayList<String> blocks = Main.dailyChallenge.getBlocks();
    private final String item = Main.dailyChallenge.getItem();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSprint(org.bukkit.event.player.PlayerToggleSprintEvent e) {
        long tempo = System.currentTimeMillis();
        final String blockWalk = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType().toString();
        final String itemInHand = e.getPlayer().getInventory().getItemInMainHand().getType().toString();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("SprintEvent ToggledSprintPlayer= " + e.getPlayer().getName());
                debugUtils.addLine("SprintEvent BlockStepOnPlayer= " + blockWalk);
                debugUtils.addLine("SprintEvent BlockStepOnConfig= " + blocks);
                debugUtils.addLine("SprintEvent ItemInHandConfig= " + item);
                debugUtils.addLine("SprintEvent ItemInHandPlayer= " + itemInHand);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("SprintEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("SprintEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("SprintEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("SprintEvent");
                }
                return;
            }

            if(!item.equalsIgnoreCase("ALL") && !item.equalsIgnoreCase(itemInHand)) {
                if (debugActive) {
                    debugUtils.addLine("SprintEvent ItemInHandConfig= " + item);
                    debugUtils.addLine("SprintEvent ItemInHandPlayer= " + itemInHand);
                    debugUtils.addLine("SprintEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("SprintEvent");
                }
                return;
            }

            if(!blocks.isEmpty() && !blocks.contains(blockWalk)) {
                if (debugActive) {
                    debugUtils.addLine("SprintEvent BlockStepOnPlayer= " + blockWalk);
                    debugUtils.addLine("SprintEvent BlockStepOnConfig= " + blocks);
                    debugUtils.addLine("SprintEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("SprintEvent");
                }
                return;
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
        });
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
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
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
        });
    }
}