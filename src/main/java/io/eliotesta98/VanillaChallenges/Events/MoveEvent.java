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

public class MoveEvent implements Listener {

    private final HashMap<String, Double> distances = new HashMap<>();
    private final DebugUtils debugUtils = new DebugUtils();
    private final ArrayList<String> blocks = Main.dailyChallenge.getBlocks();
    private final ArrayList<String> items = Main.dailyChallenge.getItems();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("MoveEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

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
        final String worldName = e.getPlayer().getWorld().getName();
        final Location from = e.getFrom();
        final Location to = e.getTo();
        final String materialFrom = from.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType().toString();
        final String materialTo = to.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType().toString();
        final String playerMaterialInHand = e.getPlayer().getInventory().getItemInMainHand().getType().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("MoveEvent PlayerMoving= " + playerName);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("MoveEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("MoveEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("MoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("MoveEvent");
                }
                return;
            }

            if (!items.isEmpty() && !items.contains(playerMaterialInHand)) {
                if (debugActive) {
                    debugUtils.addLine("MoveEvent PlayerMaterialInHand= " + playerMaterialInHand);
                    debugUtils.addLine("MoveEvent ConfigMaterialInHand= " + items);
                    debugUtils.addLine("MoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("MoveEvent");
                }
                return;
            }

            if (!blocks.isEmpty() && !blocks.contains(materialFrom) && !blocks.contains(materialTo)) {
                if (debugActive) {
                    debugUtils.addLine("MoveEvent BlockToStepOn= " + materialTo);
                    debugUtils.addLine("MoveEvent BlockFromStepOn= " + materialFrom);
                    debugUtils.addLine("MoveEvent BlockStepOnConfig= " + blocks);
                    debugUtils.debug("MoveEvent");
                }
                return;
            }

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
            if (debugActive) {
                debugUtils.addLine("MoveEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("MoveEvent");
            }
        });
    }
}

