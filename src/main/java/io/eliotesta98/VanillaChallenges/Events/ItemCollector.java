package io.eliotesta98.VanillaChallenges.Events;

import de.tr7zw.nbtapi.NBTItem;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class ItemCollector implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ItemCollector");
    private String item = Main.dailyChallenge.getItem();
    private int point = Main.dailyChallenge.getPoint();
    private String errorAlreadyPlacedChest = Main.instance.getConfigGestion().getMessages().get("Errors.AlreadyPlacedChest");

    // timer del controllo punti
    int number = 20 * 60 * 2;

    private HashMap<String, Location> chestLocation = new HashMap<>();

    public ItemCollector() {
        controlChest();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        long tempo = System.currentTimeMillis();
        final Inventory playerInventory = e.getPlayer().getInventory();
        final String playerName = e.getPlayer().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (chestLocation.get(playerName) == null && playerInventory.firstEmpty() != -1) {
                    if (debugActive) {
                        debugUtils.addLine("ItemCollector PlayerJoinName = " + playerName);
                    }
                    chestLocation.put(playerName, new Location(Bukkit.getWorld("world"), 0, -100, 0));
                    playerInventory.addItem(Main.instance.getConfigGestion().getChestCollection());
                }
                if (debugActive) {
                    debugUtils.addLine("ItemCollector execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemCollector");
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getBlockPlaced().getType() == Main.instance.getConfigGestion().getChestCollection().getType()) {
            if (debugActive) {
                debugUtils.addLine("ItemCollector Player Place Block Name = " + e.getPlayer().getName());
            }
            NBTItem nbtItem = new NBTItem(e.getItemInHand());
            if (nbtItem.getBoolean("vc.chest")) {
                if (debugActive) {
                    debugUtils.addLine("ItemCollector Location Place Chest = " + e.getBlockPlaced().getLocation());
                    debugUtils.addLine("Item Collector Item in Hand = " + e.getItemInHand().getType());
                }
                if (chestLocation.get(e.getPlayer().getName()) == null) {
                    chestLocation.put(e.getPlayer().getName(), e.getBlockPlaced().getLocation());
                } else {
                    if (chestLocation.get(e.getPlayer().getName()).getBlockY() == -100) {
                        chestLocation.replace(e.getPlayer().getName(), chestLocation.get(e.getPlayer().getName()), e.getBlockPlaced().getLocation());
                    } else {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ColorUtils.applyColor(errorAlreadyPlacedChest));
                    }
                }
            }
        }
        if (debugActive) {
            debugUtils.addLine("ItemCollector execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ItemCollector");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final Block block = e.getBlock();
        if (block.getType() == Main.instance.getConfigGestion().getChestCollection().getType()) {
            if (chestLocation.get(e.getPlayer().getName()) != null) {
                Location loc = chestLocation.get(e.getPlayer().getName());
                if (debugActive) {
                    debugUtils.addLine("ItemCollector location chest= " + loc);
                }
                if (block.getX() == loc.getBlockX()
                        && block.getY() == loc.getBlockY()
                        && block.getZ() == loc.getBlockZ()
                        && block.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
                    if (debugActive) {
                        debugUtils.addLine("ItemCollector location block = " + block.getLocation());
                    }
                    e.setDropItems(false);
                    chestLocation.replace(e.getPlayer().getName(), loc, new Location(Bukkit.getWorld("world"), 0, -100, 0));
                    e.getPlayer().getInventory().addItem(Main.instance.getConfigGestion().getChestCollection());
                }
            }
        }
        if (debugActive) {
            debugUtils.addLine("ItemCollector execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.addLine("ItemCollector break Block Type = " + block.getType());
            debugUtils.addLine("ItemCollector break Block Type Config = " + Main.instance.getConfigGestion().getChestCollection().getType());
            debugUtils.addLine("ItemCollector Player Name = " + e.getPlayer().getName());
            debugUtils.debug("ItemCollector");
        }
    }

    public void controlChest() {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                long tempo = System.currentTimeMillis();
                if (!chestLocation.isEmpty()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (debugActive) {
                            debugUtils.addLine("ItemCollector Player Name = " + player.getName());
                        }
                        if (chestLocation.get(player.getName()) != null) {
                            Location location = chestLocation.get(player.getName());
                            if (location.getBlock().getType() == Main.instance.getConfigGestion().getChestCollection().getType()) {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                                    @Override
                                    public void run() {
                                        Chest chest = (Chest) location.getBlock().getState();
                                        if (debugActive) {
                                            debugUtils.addLine("ItemCollector Chest Location = " + location);
                                        }
                                        for (int i = 0; i < chest.getInventory().getSize(); i++) {
                                            ItemStack itemInv = chest.getInventory().getItem(i);
                                            if (itemInv != null) {
                                                int amount = itemInv.getAmount();
                                                if (debugActive) {
                                                    debugUtils.addLine("ItemCollector Amount Item = " + amount);
                                                }
                                                if (item.equalsIgnoreCase("ALL")) {
                                                    Main.dailyChallenge.increment(player.getName(), (long) point * amount);
                                                    itemInv.setAmount(0);
                                                } else {
                                                    if (item.equalsIgnoreCase(itemInv.getType().toString())) {
                                                        Main.dailyChallenge.increment(player.getName(), (long) point * amount);
                                                        itemInv.setAmount(0);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("ItemCollector Item in chest destroyed");
                    debugUtils.addLine("ItemCollector execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemCollector");
                }
            }
        }, 0, number);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(task, "ItemCollector", false);
    }

}
