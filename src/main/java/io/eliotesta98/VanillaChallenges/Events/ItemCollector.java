package io.eliotesta98.VanillaChallenges.Events;

import de.tr7zw.nbtapi.NBTItem;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;

public class ItemCollector implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ItemCollector");
    private String item = Main.dailyChallenge.getItem();
    private int point = Main.dailyChallenge.getPoint();

    // timer del controllo punti
    int number = 20 * 60 * 2;
    private static BukkitTask task;

    private HashMap<String, Location> chestLocation = new HashMap<>();

    public ItemCollector() {
        controlChest();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (chestLocation.get(e.getPlayer().getName()) == null && e.getPlayer().getInventory().firstEmpty() != -1) {
                    debugUtils.addLine("ItemCollector Player Join Name = " + e.getPlayer().getName());
                    chestLocation.put(e.getPlayer().getName(), null);
                    e.getPlayer().getInventory().addItem(Main.instance.getConfigGestion().getChestCollection());
                }
            }
        });
        if (debugActive) {
            debugUtils.addLine("ItemCollector execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ItemCollector");
        }
        return;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (e.getBlockPlaced().getType() == Main.instance.getConfigGestion().getChestCollection().getType()) {
                    debugUtils.addLine("ItemCollector Player Place Block Name = " + e.getPlayer().getName());
                    NBTItem nbtItem = new NBTItem(e.getItemInHand());
                    if (nbtItem.hasNBTData()) {
                        debugUtils.addLine("ItemCollector Location Place Chest = " + e.getBlockPlaced().getLocation());
                        chestLocation.replace(e.getPlayer().getName(), chestLocation.get(e.getPlayer().getName()), e.getBlockPlaced().getLocation());
                    }
                }
            }
        });
        if (debugActive) {
            debugUtils.addLine("ItemCollector execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ItemCollector");
        }
        return;
    }

    public void controlChest() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {

            }
        }, 0, number);
    }

    public static void stop() {
        task.cancel();
    }

}
