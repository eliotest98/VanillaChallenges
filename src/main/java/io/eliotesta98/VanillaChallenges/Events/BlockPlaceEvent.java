package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class BlockPlaceEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockPlaceEvent");
    private String block = Main.instance.getDailyChallenge().getBlock();
    private String blockOnPlaced = Main.instance.getDailyChallenge().getBlockOnPlace();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String materialBlockOnPlaced = e.getBlockAgainst().getType().toString();
        final String materialBlockPlaced = e.getBlockPlaced().getType().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                debugUtils.addLine("BlockBreakEvent PlayerPlacing= " + playerName);
                if (block.equalsIgnoreCase("ALL")) {
                    if (blockOnPlaced.equalsIgnoreCase("ALL")) {
                        // 0 - 0
                        debugUtils.addLine("BlockPlaceEvent Conditions= 0 - 0");
                        Main.instance.getDailyChallenge().increment(playerName, point);
                        if (debugActive) {
                            debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("BlockPlaceEvent");
                        }
                        return;
                    } else {
                        debugUtils.addLine("BlockPlaceEvent BlockOnPlacedByPlayer= " + materialBlockOnPlaced);
                        debugUtils.addLine("BlockPlaceEvent BlockOnPlacedConfig= " + blockOnPlaced);
                        if (blockOnPlaced.equalsIgnoreCase(materialBlockOnPlaced)) {
                            debugUtils.addLine("BlockPlaceEvent Conditions= 0 - 1");
                            // 0 - 1
                            Main.instance.getDailyChallenge().increment(playerName, point);
                            if (debugActive) {
                                debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockPlaceEvent");
                            }
                            return;
                        }
                    }
                } else {
                    debugUtils.addLine("BlockPlaceEvent BlockPlacedByPlayer= " + materialBlockPlaced);
                    debugUtils.addLine("BlockPlaceEvent BlockPlacedConfig= " + block);
                    if (block.equalsIgnoreCase(materialBlockPlaced)) {
                        if (blockOnPlaced.equalsIgnoreCase("ALL")) {
                            // 1 - 0
                            debugUtils.addLine("BlockPlaceEvent Conditions= 1 - 0");
                            Main.instance.getDailyChallenge().increment(playerName, point);
                            if (debugActive) {
                                debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockPlaceEvent");
                            }
                            return;
                        } else {
                            debugUtils.addLine("BlockPlaceEvent BlockOnPlacedByPlayer= " + materialBlockOnPlaced);
                            debugUtils.addLine("BlockPlaceEvent BlockOnPlacedConfig= " + blockOnPlaced);
                            if (blockOnPlaced.equalsIgnoreCase(materialBlockOnPlaced)) {
                                // 1 - 1
                                debugUtils.addLine("BlockPlaceEvent Conditions= 1 - 1");
                                Main.instance.getDailyChallenge().increment(playerName, point);
                                if (debugActive) {
                                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockPlaceEvent");
                                }
                                return;
                            }
                        }
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockPlaceEvent");
                }
                return;
                //Main.instance.getDailyChallenge().stampaNumero(playerName);
            }
        });
    }

}
