package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BlockBreakEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("BlockBreakEvent");
    private String block = Main.instance.getDailyChallenge().getBlock();
    private String itemInHand = Main.instance.getDailyChallenge().getItemInHand();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockBreakEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.isCancelled()) {
            if (debugActive) {
                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BlockBreakEvent");
            }
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (block.equalsIgnoreCase(e.getBlock().getType().toString())) {
                    if(e.getPlayer().getInventory().getItemInMainHand().getType().toString().equalsIgnoreCase(itemInHand)){
                        Main.instance.getDailyChallenge().increment(e.getPlayer().getName());
                        if (debugActive) {
                            debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("BlockBreakEvent");
                        }
                        return;
                    } else {
                        if(itemInHand.equalsIgnoreCase("ALL")){
                            Main.instance.getDailyChallenge().increment(e.getPlayer().getName());
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockBreakEvent");
                            }
                            return;
                        } else {
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockBreakEvent");
                            }
                            return;
                        }
                    }
                } else {
                    if (block.equalsIgnoreCase("ALL")) {
                        if(e.getPlayer().getInventory().getItemInMainHand().getType().toString().equalsIgnoreCase(itemInHand)){
                            Main.instance.getDailyChallenge().increment(e.getPlayer().getName());
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockBreakEvent");
                            }
                            return;
                        } else {
                            if(itemInHand.equalsIgnoreCase("ALL")){
                                Main.instance.getDailyChallenge().increment(e.getPlayer().getName());
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockBreakEvent");
                                }
                                return;
                            } else {
                                if (debugActive) {
                                    debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockBreakEvent");
                                }
                                return;
                            }
                        }
                    } else {
                        if (debugActive) {
                            debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("BlockBreakEvent");
                        }
                        return;
                    }
                }
                //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
            }
        });
    }
}
