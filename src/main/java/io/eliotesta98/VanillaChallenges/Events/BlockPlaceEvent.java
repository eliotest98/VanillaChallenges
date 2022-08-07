package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BlockPlaceEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockPlaceEvent");
    private String block = Main.instance.getDailyChallenge().getBlock();
    private String blockOnPlaced = Main.instance.getDailyChallenge().getBlockOnPlace();
    private int point = Main.dailyChallenge.getPoint();
    private String sneaking = Main.dailyChallenge.getSneaking();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String materialBlockOnPlaced = e.getBlockAgainst().getType().toString();
        final String materialBlockPlaced = e.getBlockPlaced().getType().toString();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent PlayerPlacing= " + playerName);
                    debugUtils.addLine("BlockBreakEvent ConfigSneaking= " + sneaking);
                    debugUtils.addLine("BlockBreakEvent PlayerPlacing= " + sneakingPlayer);
                }
                if (sneaking.equalsIgnoreCase("NOBODY")) {
                    if (block.equalsIgnoreCase("ALL")) {
                        if (blockOnPlaced.equalsIgnoreCase("ALL")) {
                            if (debugActive) {
                                // 0 - 0
                                debugUtils.addLine("BlockPlaceEvent Conditions= 0 - 0");
                            }
                            Main.instance.getDailyChallenge().increment(playerName, point);
                            if (debugActive) {
                                debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockPlaceEvent");
                            }
                            return;
                        } else {
                            if (debugActive) {
                                debugUtils.addLine("BlockPlaceEvent BlockOnPlacedByPlayer= " + materialBlockOnPlaced);
                                debugUtils.addLine("BlockPlaceEvent BlockOnPlacedConfig= " + blockOnPlaced);
                            }
                            if (blockOnPlaced.equalsIgnoreCase(materialBlockOnPlaced)) {
                                if (debugActive) {
                                    debugUtils.addLine("BlockPlaceEvent Conditions= 0 - 1");
                                }
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
                        if (debugActive) {
                            debugUtils.addLine("BlockPlaceEvent BlockPlacedByPlayer= " + materialBlockPlaced);
                            debugUtils.addLine("BlockPlaceEvent BlockPlacedConfig= " + block);
                        }
                        if (block.equalsIgnoreCase(materialBlockPlaced)) {
                            if (blockOnPlaced.equalsIgnoreCase("ALL")) {
                                if (debugActive) {
                                    // 1 - 0
                                    debugUtils.addLine("BlockPlaceEvent Conditions= 1 - 0");
                                }
                                Main.instance.getDailyChallenge().increment(playerName, point);
                                if (debugActive) {
                                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockPlaceEvent");
                                }
                                return;
                            } else {
                                if (debugActive) {
                                    debugUtils.addLine("BlockPlaceEvent BlockOnPlacedByPlayer= " + materialBlockOnPlaced);
                                    debugUtils.addLine("BlockPlaceEvent BlockOnPlacedConfig= " + blockOnPlaced);
                                }
                                if (blockOnPlaced.equalsIgnoreCase(materialBlockOnPlaced)) {
                                    // 1 - 1
                                    if (debugActive) {
                                        debugUtils.addLine("BlockPlaceEvent Conditions= 1 - 1");
                                    }
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
                } else {
                    if (Boolean.parseBoolean(sneaking) == sneakingPlayer) {
                        if (block.equalsIgnoreCase("ALL")) {
                            if (blockOnPlaced.equalsIgnoreCase("ALL")) {
                                if (debugActive) {
                                    // 0 - 0
                                    debugUtils.addLine("BlockPlaceEvent Conditions= 0 - 0");
                                }
                                Main.instance.getDailyChallenge().increment(playerName, point);
                                if (debugActive) {
                                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockPlaceEvent");
                                }
                                return;
                            } else {
                                if (debugActive) {
                                    debugUtils.addLine("BlockPlaceEvent BlockOnPlacedByPlayer= " + materialBlockOnPlaced);
                                    debugUtils.addLine("BlockPlaceEvent BlockOnPlacedConfig= " + blockOnPlaced);
                                }
                                if (blockOnPlaced.equalsIgnoreCase(materialBlockOnPlaced)) {
                                    if (debugActive) {
                                        debugUtils.addLine("BlockPlaceEvent Conditions= 0 - 1");
                                    }
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
                            if (debugActive) {
                                debugUtils.addLine("BlockPlaceEvent BlockPlacedByPlayer= " + materialBlockPlaced);
                                debugUtils.addLine("BlockPlaceEvent BlockPlacedConfig= " + block);
                            }
                            if (block.equalsIgnoreCase(materialBlockPlaced)) {
                                if (blockOnPlaced.equalsIgnoreCase("ALL")) {
                                    if (debugActive) {
                                        // 1 - 0
                                        debugUtils.addLine("BlockPlaceEvent Conditions= 1 - 0");
                                    }
                                    Main.instance.getDailyChallenge().increment(playerName, point);
                                    if (debugActive) {
                                        debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                        debugUtils.debug("BlockPlaceEvent");
                                    }
                                    return;
                                } else {
                                    if (debugActive) {
                                        debugUtils.addLine("BlockPlaceEvent BlockOnPlacedByPlayer= " + materialBlockOnPlaced);
                                        debugUtils.addLine("BlockPlaceEvent BlockOnPlacedConfig= " + blockOnPlaced);
                                    }
                                    if (blockOnPlaced.equalsIgnoreCase(materialBlockOnPlaced)) {
                                        // 1 - 1
                                        if (debugActive) {
                                            debugUtils.addLine("BlockPlaceEvent Conditions= 1 - 1");
                                        }
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
