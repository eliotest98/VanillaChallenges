package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SprintEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGesture().getDebug().get("SprintEvent");
    private final HashMap<String, Boolean> players = new HashMap<>();
    private final HashMap<String, Double> distances = new HashMap<>();
    private final List<String> items = Main.instance.getDailyChallenge().getItems();
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGesture().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSprint(PlayerToggleSprintEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final String blockWalk = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType().toString();
        String itemInHand;
        if(Main.version113) {
            itemInHand = e.getPlayer().getInventory().getItemInMainHand().getType().toString();
        } else {
            itemInHand = e.getPlayer().getInventory().getItemInHand().getType().toString();
        }
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("ToggledSprintPlayer= " + e.getPlayer().getName());
                debugUtils.addLine("BlockStepOnPlayer= " + blockWalk);
                debugUtils.addLine("ItemInHandConfig= " + items);
                debugUtils.addLine("ItemInHandPlayer= " + itemInHand);
            }

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(e.getPlayer().getName()))) {
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

            if (!Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if(!items.isEmpty() && !items.contains(itemInHand)) {
                if (debugActive) {
                    debugUtils.addLine("ItemInHandConfig= " + items);
                    debugUtils.addLine("ItemInHandPlayer= " + itemInHand);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (!Controls.isBlock(blockWalk, debugActive, debugUtils, tempo)) {
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
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getTo() == null) {
            if (debugActive) {
                debugUtils.addLine("EndLocation= null");
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return;
        }
        final String playerName = e.getPlayer().getName();
        final Location from = e.getFrom();
        final Location to = e.getTo();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerSprinting= " + playerName);
            }
            if (players.get(playerName) != null) {
                if (players.get(playerName)) {
                    if (distances.get(playerName) == null) {
                        distances.put(playerName, from.distance(to));
                    } else {
                        double old = distances.get(playerName);
                        double newDouble = old + from.distance(to);
                        if (newDouble > 1.0) {
                            Main.instance.getDailyChallenge().increment(playerName, point);
                            distances.replace(playerName, newDouble - 1.0);
                        } else {
                            distances.replace(playerName, newDouble);
                        }
                    }
                }
            }
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}