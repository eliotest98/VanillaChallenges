package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BlockBreakEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockBreakEvent");
    private String block = Main.instance.getDailyChallenge().getBlock();
    private String itemInHand = Main.instance.getDailyChallenge().getItemInHand();
    private int point = Main.dailyChallenge.getPoint();
    private String sneaking = Main.dailyChallenge.getSneaking();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final String blockBreaking = e.getBlock().getType().toString();
        final ItemStack itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
        final String playerName = e.getPlayer().getName();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent PlayerBreaking= " + playerName);
                    debugUtils.addLine("BlockBreakEvent BlockConfig= " + block);
                    debugUtils.addLine("BlockBreakEvent BlockBreaking= " + blockBreaking);
                    debugUtils.addLine("BlockBreakEvent PlayerSneaking= " + sneakingPlayer);
                    debugUtils.addLine("BlockBreakEvent ConfigSneaking= " + sneaking);
                }
                if (sneaking.equalsIgnoreCase("NOBODY")) {
                    if (block.equalsIgnoreCase(blockBreaking)) {
                        if (debugActive) {
                            debugUtils.addLine("BlockBreakEvent ItemInHandConfig= " + itemInHand);
                            debugUtils.addLine("BlockBreakEvent ItemInHandBreaking= " + itemInMainHand.getType());
                        }
                        if (itemInMainHand.getType().toString().equalsIgnoreCase(itemInHand)) {
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent Conditions= 1 - 1");
                            }
                            // 1 - 1
                            Main.instance.getDailyChallenge().increment(playerName, point);
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockBreakEvent");
                            }
                            return;
                        } else {
                            // 1 - 0
                            if (itemInHand.equalsIgnoreCase("ALL")) {
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent Conditions= 1 - 0");
                                }
                                Main.instance.getDailyChallenge().increment(playerName, point);
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockBreakEvent");
                                }
                                return;
                            }
                        }
                    } else {
                        if (block.equalsIgnoreCase("ALL")) {
                            if (debugActive) {
                                // 0 - 1
                                debugUtils.addLine("BlockBreakEvent ItemInHandConfig= " + itemInHand);
                                debugUtils.addLine("BlockBreakEvent ItemInHandBreaking= " + itemInMainHand.getType());
                            }
                            if (itemInMainHand.getType().toString().equalsIgnoreCase(itemInHand)) {
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent Conditions= 0 - 1");
                                }
                                Main.instance.getDailyChallenge().increment(playerName, point);
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockBreakEvent");
                                }
                                return;
                            } else {
                                // 0 - 0
                                if (itemInHand.equalsIgnoreCase("ALL")) {
                                    if (debugActive) {
                                        debugUtils.addLine("BlockBreakEvent Conditions= 0 - 0");
                                    }
                                    Main.instance.getDailyChallenge().increment(playerName, point);
                                    if (debugActive) {
                                        debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                        debugUtils.debug("BlockBreakEvent");
                                    }
                                    return;
                                }
                            }
                        }
                    }
                } else {
                    if (Boolean.parseBoolean(sneaking) == sneakingPlayer) {
                        if (block.equalsIgnoreCase(blockBreaking)) {
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent ItemInHandConfig= " + itemInHand);
                                debugUtils.addLine("BlockBreakEvent ItemInHandBreaking= " + itemInMainHand.getType());
                            }
                            if (itemInMainHand.getType().toString().equalsIgnoreCase(itemInHand)) {
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent Conditions= 1 - 1");
                                }
                                // 1 - 1
                                Main.instance.getDailyChallenge().increment(playerName, point);
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockBreakEvent");
                                }
                                return;
                            } else {
                                // 1 - 0
                                if (itemInHand.equalsIgnoreCase("ALL")) {
                                    if (debugActive) {
                                        debugUtils.addLine("BlockBreakEvent Conditions= 1 - 0");
                                    }
                                    Main.instance.getDailyChallenge().increment(playerName, point);
                                    if (debugActive) {
                                        debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                        debugUtils.debug("BlockBreakEvent");
                                    }
                                    return;
                                }
                            }
                        } else {
                            if (block.equalsIgnoreCase("ALL")) {
                                if (debugActive) {
                                    // 0 - 1
                                    debugUtils.addLine("BlockBreakEvent ItemInHandConfig= " + itemInHand);
                                    debugUtils.addLine("BlockBreakEvent ItemInHandBreaking= " + itemInMainHand.getType());
                                }
                                if (itemInMainHand.getType().toString().equalsIgnoreCase(itemInHand)) {
                                    if (debugActive) {
                                        debugUtils.addLine("BlockBreakEvent Conditions= 0 - 1");
                                    }
                                    Main.instance.getDailyChallenge().increment(playerName, point);
                                    if (debugActive) {
                                        debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                        debugUtils.debug("BlockBreakEvent");
                                    }
                                    return;
                                } else {
                                    // 0 - 0
                                    if (itemInHand.equalsIgnoreCase("ALL")) {
                                        if (debugActive) {
                                            debugUtils.addLine("BlockBreakEvent Conditions= 0 - 0");
                                        }
                                        Main.instance.getDailyChallenge().increment(playerName, point);
                                        if (debugActive) {
                                            debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                            debugUtils.debug("BlockBreakEvent");
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("BlockBreakEvent");
                }
                return;
                //Main.instance.getDailyChallenge().stampaNumero(playerName);
            }
        });
    }
}
