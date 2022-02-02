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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.isCancelled()) {
            if (debugActive) {
                debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("BlockPlaceEvent");
            }
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (block.equalsIgnoreCase("ALL")) {
                    if (blockOnPlaced.equalsIgnoreCase("ALL")) {
                        Main.instance.getDailyChallenge().increment(e.getPlayer().getName());
                    } else {
                        if (blockOnPlaced.equalsIgnoreCase(e.getBlockAgainst().getType().toString())) {
                            Main.instance.getDailyChallenge().increment(e.getPlayer().getName());
                        } else {
                            if (debugActive) {
                                debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockPlaceEvent");
                            }
                            return;
                        }
                    }
                } else {
                    if (block.equalsIgnoreCase(e.getBlockPlaced().getType().toString())) {
                        if (blockOnPlaced.equalsIgnoreCase("ALL")) {
                            Main.instance.getDailyChallenge().increment(e.getPlayer().getName());
                        } else {
                            if (blockOnPlaced.equalsIgnoreCase(e.getBlockAgainst().getType().toString())) {
                                Main.instance.getDailyChallenge().increment(e.getPlayer().getName());
                            } else {
                                if (debugActive) {
                                    debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("BlockPlaceEvent");
                                }
                                return;
                            }
                        }
                    } else {
                        if (debugActive) {
                            debugUtils.addLine("BlockPlaceEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("BlockPlaceEvent");
                        }
                        return;
                    }
                }
                Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
            }
        });
    }

}
