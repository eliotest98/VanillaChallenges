package io.eliotesta98.VanillaChallenges.Events.Challenges.ItemCollector;

import com.HeroxWar.HeroxCore.MessageGesture;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemCollector implements Listener {

    private static Database db;
    private final DebugUtils debugUtils = new DebugUtils("ItemCollector");
    private final boolean debugActive = Main.instance.getConfigGesture().getDebug().get("ItemCollector");
    private final String caseBroken = Main.instance.getConfigGesture().getMessages().get("Errors.CaseBroken");
    private final List<String> items = Main.instance.getDailyChallenge().getItems();
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final String errorAlreadyPlacedChest = Main.instance.getConfigGesture().getMessages().get("Errors.AlreadyPlacedChest");
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGesture().getHooks().get("SuperiorSkyblock2");

    // timer del controllo punti
    final int number = 20 * 60 * 2;

    private final HashMap<String, Location> chestLocation = new HashMap<>();

    public ItemCollector() {
        controlChest();
        db = new Database();
        db.loadChests(chestLocation);
        saveChests();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        long tempo = System.currentTimeMillis();
        final Inventory playerInventory = e.getPlayer().getInventory();
        final String playerName = e.getPlayer().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (chestLocation.get(playerName) == null && playerInventory.firstEmpty() != -1 && Controls.hasPermission(playerName)) {
                if (debugActive) {
                    debugUtils.addLine("PlayerJoinName = " + playerName);
                }
                chestLocation.put(playerName, new Location(Bukkit.getWorld("world"), 0, -100, 0));
                playerInventory.addItem(Main.instance.getConfigGesture().getChestCollection());
            }
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getBlockPlaced().getType() == Main.instance.getConfigGesture().getChestCollection().getType()) {
            if (debugActive) {
                debugUtils.addLine("Player Place Block Name = " + e.getPlayer().getName());
            }
            NBTItem nbtItem = new NBTItem(e.getItemInHand());
            if (nbtItem.getBoolean("vc.chest")) {
                if (debugActive) {
                    debugUtils.addLine("Location Place Chest = " + e.getBlockPlaced().getLocation());
                    debugUtils.addLine("Item in Hand = " + e.getItemInHand().getType());
                }
                if (chestLocation.get(e.getPlayer().getName()) == null) {
                    chestLocation.put(e.getPlayer().getName(), e.getBlockPlaced().getLocation());
                } else {
                    if (chestLocation.get(e.getPlayer().getName()).getBlockY() == -100) {
                        chestLocation.replace(e.getPlayer().getName(), chestLocation.get(e.getPlayer().getName()), e.getBlockPlaced().getLocation());
                    } else {
                        e.setCancelled(true);
                        MessageGesture.sendMessage(e.getPlayer(), errorAlreadyPlacedChest);
                    }
                }
            }
        }
        if (debugActive) {
            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final Block block = e.getBlock();
        if (block.getType() == Main.instance.getConfigGesture().getChestCollection().getType()) {
            if (chestLocation.get(e.getPlayer().getName()) != null) {
                Location loc = chestLocation.get(e.getPlayer().getName());
                if (debugActive) {
                    debugUtils.addLine("location chest= " + loc);
                }
                if (block.getX() == loc.getBlockX()
                        && block.getY() == loc.getBlockY()
                        && block.getZ() == loc.getBlockZ()
                        && block.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
                    if (debugActive) {
                        debugUtils.addLine("location block = " + block.getLocation());
                    }
                    //TODO da testare se in > 1.13 funziona l'else in modo da mettere tutto su un rigo
                    if (!Main.version.isInRange(8, 12)) {
                        e.setDropItems(false);
                    } else {
                        e.setCancelled(true);
                        block.setType(Material.AIR);
                    }
                    chestLocation.replace(e.getPlayer().getName(), loc, new Location(Bukkit.getWorld("world"), 0, -100, 0));
                    e.getPlayer().getInventory().addItem(Main.instance.getConfigGesture().getChestCollection());
                }
            }
        }
        if (debugActive) {
            debugUtils.addLine("Break Block Type = " + block.getType());
            debugUtils.addLine("Break Block Type Config = " + Main.instance.getConfigGesture().getChestCollection().getType());
            debugUtils.addLine("Player Name = " + e.getPlayer().getName());
            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug();
        }
    }

    public void controlChest() {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            long tempo = System.currentTimeMillis();
            if (!chestLocation.isEmpty()) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (debugActive) {
                        debugUtils.addLine("Player Name = " + player.getName());
                    }
                    if (Main.instance.getConfigGesture().getTasks().isChallengeStart()) {
                        return;
                    }
                    if (chestLocation.get(player.getName()) != null) {
                        Location location = chestLocation.get(player.getName());
                        if (location.getWorld() != null && location.getBlock().getType() == Main.instance.getConfigGesture().getChestCollection().getType()) {
                            final String worldName = player.getWorld().getName();
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                                Chest chest = (Chest) location.getBlock().getState();
                                if (debugActive) {
                                    debugUtils.addLine("Chest Location = " + location);
                                }
                                for (int i = 0; i < chest.getInventory().getSize(); i++) {
                                    ItemStack itemInv = chest.getInventory().getItem(i);
                                    if (itemInv != null) {
                                        int amount = itemInv.getAmount();
                                        if (debugActive) {
                                            debugUtils.addLine("Amount Item = " + amount);
                                        }

                                        if (superiorSkyBlock2Enabled) {
                                            if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(player.getName()))) {
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

                                        if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                                            return;
                                        }

                                        if (items.isEmpty()) {
                                            Main.instance.getDailyChallenge().increment(player.getName(), (long) point * amount);
                                            //TODO da testare se in > 1.13 funziona l'else in modo da mettere tutto su un rigo
                                            if (!Main.version.isInRange(8, 12)) {
                                                itemInv.setAmount(0);
                                            } else {
                                                chest.getInventory().removeItem(itemInv);
                                            }
                                        } else {
                                            if (items.contains(itemInv.getType().toString())) {
                                                Main.instance.getDailyChallenge().increment(player.getName(), (long) point * amount);
                                                //TODO da testare se in > 1.13 funziona l'else in modo da mettere tutto su un rigo
                                                if (!Main.version.isInRange(8, 12)) {
                                                    itemInv.setAmount(0);
                                                } else {
                                                    chest.getInventory().removeItem(itemInv);
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
                debugUtils.addLine("Item in chest destroyed");
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        }, 0, number);
        Main.instance.getConfigGesture().getTasks().addExternalTasks(task, "ItemCollector", false);
    }

    public void saveChests() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {
            ArrayList<String> chestBroken = new ArrayList<>();
            for (Map.Entry<String, Location> location : chestLocation.entrySet()) {
                if (location.getValue().getWorld() == null) {
                    Player player = Bukkit.getPlayer(location.getKey());
                    if (player != null && player.isOnline()) {
                        MessageGesture.sendMessage(player, caseBroken);
                        db.deleteChest(player.getName());
                        chestBroken.add(location.getKey());
                        player.getInventory().addItem(Main.instance.getConfigGesture().getChestCollection());
                    }
                } else {
                    db.updateChest(location.getKey(), location.getValue());
                }
            }
            if (!chestBroken.isEmpty()) {
                for (String playerName : chestBroken) {
                    chestLocation.remove(playerName);
                }
            }
        }, 0, 1200L);
    }

    public static void deleteDb() {
        db.deleteFile();
    }
}
