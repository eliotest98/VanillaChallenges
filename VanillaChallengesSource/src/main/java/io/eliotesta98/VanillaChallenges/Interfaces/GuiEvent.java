package io.eliotesta98.VanillaChallenges.Interfaces;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class GuiEvent implements Listener {

    private ArrayList<Player> clicked = new ArrayList<>();
    private final boolean debugGui = Main.instance.getConfigGestion().getDebug().get("ClickGui");

    /**
     * In API versions 1.20.6 and earlier, InventoryView is a class.
     * In versions 1.21 and later, it is an interface.
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.
     *
     * @param inventoryView The generic InventoryView with an InventoryView to inspect.
     * @return The top Inventory object from the event's InventoryView.
     */
    public static Inventory getTopInventory(InventoryView inventoryView) {
        try {
            Method getTopInventory = ((Object) inventoryView).getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            return (Inventory) getTopInventory.invoke(inventoryView);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * In API versions 1.20.6 and earlier, InventoryView is a class.
     * In versions 1.21 and later, it is an interface.
     * This method uses reflection to get the OpenInventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.
     *
     * @param inventoryView The generic InventoryView with an InventoryView to inspect.
     * @return The Open Inventory object from the event's InventoryView.
     */
    public static InventoryView getOpenInventoryView(InventoryView inventoryView) {
        try {
            Method getPlayer = ((Object) inventoryView).getClass().getMethod("getPlayer");
            getPlayer.setAccessible(true);
            HumanEntity humanEntity = (HumanEntity) getPlayer.invoke(inventoryView);
            return humanEntity.getOpenInventory();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void InventoryDragEvent(final InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof VanillaChallengesInterfaceHolder) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void InventoryDragEvent(final InventoryMoveItemEvent e) {
        if (e.getInitiator().getHolder() instanceof VanillaChallengesInterfaceHolder) {
            e.setCancelled(true);
        } else if (e.getDestination().getHolder() instanceof VanillaChallengesInterfaceHolder) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent inventoryCloseEvent) {
        try {
            if (inventoryCloseEvent.getPlayer() instanceof Player) {
                this.clicked.remove(inventoryCloseEvent.getPlayer());
                if (inventoryCloseEvent.getInventory().getHolder() instanceof VanillaChallengesInterfaceHolder) {
                    int count = 0;
                    NBTItem nbtItem = null;
                    while (count < inventoryCloseEvent.getInventory().getSize()) {
                        if (inventoryCloseEvent.getInventory().getItem(count) != null) {
                            nbtItem = new NBTItem(inventoryCloseEvent.getInventory().getItem(count));
                            break;
                        }
                        count++;
                    }
                    if (nbtItem != null) {
                        Main.instance.getConfigGestion().getInterfaces().get(nbtItem.getString("vc.currentInterface")).removeInventory(inventoryCloseEvent.getPlayer().getName());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void cancelOnClick(final InventoryClickEvent event) {
        Inventory inventory = getTopInventory(getOpenInventoryView(event.getWhoClicked().getOpenInventory()));
        if (inventory != null) {
            if (inventory.getHolder() instanceof VanillaChallengesInterfaceHolder)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(final InventoryClickEvent inventoryClickEvent) {
        DebugUtils debug = new DebugUtils("Gui");
        long tempo = System.currentTimeMillis();
        if (inventoryClickEvent.getWhoClicked() instanceof Player) {
            final Player player = (Player) inventoryClickEvent.getWhoClicked();
            final Inventory inv = inventoryClickEvent.getClickedInventory();
            clicked.add(player);
            if (inv == null || !inv.getType().equals(InventoryType.CHEST)) {
                if (debugGui) {
                    debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debug.debug();
                }
                return;
            }
            // inventario lista player morti
            if (inv.getHolder() instanceof VanillaChallengesInterfaceHolder) {
                // primo inventario
                inventoryClickEvent.setCancelled(true);
                if (inv.getItem(inventoryClickEvent.getSlot()) == null
                        || inv.getItem(inventoryClickEvent.getSlot()).getType() == Material.AIR) {
                    // se lo slot che clicco è vuoto o aria
                    if (debugGui) {
                        debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                    return;
                }
                // prendo l'nbtItem
                NBTItem nbtItem = new NBTItem(inv.getItem(inventoryClickEvent.getSlot()));
                int pageNumber = nbtItem.getInteger("vc.numberPage");
                String typeInterface = nbtItem.getString("vc.currentInterface");
                int itemSlot = nbtItem.getInteger("vc.positionItem");
                ArrayList<String> slots = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getSlots();
                String returnInterface = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getNameInterfaceToReturn();
                String nameItemConfig = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getItemsConfig().get(slots.get(inventoryClickEvent.getSlot())).getNameItemConfig();
                String interfaceToOpen = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getNameInterfaceToOpen();
                if (typeInterface.equalsIgnoreCase("Challenges")) {
                    String currentItem = inventoryClickEvent.getCurrentItem().getType().toString();
                    short durability = inventoryClickEvent.getCurrentItem().getDurability();
                    String configItem = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getItemsConfig().get(slots.get(inventoryClickEvent.getSlot())).getType();
                    if (debugGui) {
                        debug.addLine("Config Item=" + configItem);
                        debug.addLine("Current Item=" + currentItem);
                        if (durability != 0) {
                            debug.addLine("Current Item Durability=" + durability);
                        }
                        debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                    boolean isItem113 = false;
                    if (!Main.version113) {
                        if (configItem.equalsIgnoreCase(currentItem) ||
                                configItem.equalsIgnoreCase(currentItem + "-" + durability)) {
                            isItem113 = true;
                        }
                    }
                    if (isItem113 || Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getItemsConfig().get(slots.get(inventoryClickEvent.getSlot())).getType().equalsIgnoreCase(inventoryClickEvent.getCurrentItem().getType().toString())) {
                        if (nameItemConfig.equalsIgnoreCase("LeftPage")) {
                            player.closeInventory();
                            ArrayList<Challenge> challenges = new ArrayList<>();
                            int limit = (pageNumber - 1) * Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getSizeModificableSlot();
                            int count = 1;
                            int number = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getSizeModificableSlot();
                            if (pageNumber != 1) {
                                for (Challenge entry : Main.db.getAllChallenges()) {
                                    if (limit < count && number > 0) {
                                        challenges.add(entry);
                                        number--;
                                    }
                                    count++;
                                }
                            } else {
                                for (Challenge entry : Main.db.getAllChallenges()) {
                                    if (number != 0) {
                                        challenges.add(entry);
                                    } else {
                                        break;
                                    }
                                    number--;
                                }
                            }
                            // apro l'interfaccia
                            Main.instance.getConfigGestion().getInterfaces().get(typeInterface).openInterface(challenges, player, pageNumber);
                            if (debugGui) {
                                debug.addLine("Left Page");
                                debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        } else if (nameItemConfig.equalsIgnoreCase("RightPage")) {
                            player.closeInventory();
                            ArrayList<Challenge> challenges = new ArrayList<>();
                            int limit = (pageNumber - 1) * Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getSizeModificableSlot();
                            int count = 1;
                            int number = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getSizeModificableSlot();
                            for (Challenge entry : Main.db.getAllChallenges()) {
                                if (limit < count && number > 0) {
                                    challenges.add(entry);
                                    number--;
                                }
                                count++;
                            }
                            Main.instance.getConfigGestion().getInterfaces().get(typeInterface).openInterface(challenges, player, pageNumber);
                            if (debugGui) {
                                debug.addLine("Right Page");
                                debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                    }
                }
                if (debugGui) {
                    debug.addLine("Challenges List");
                    debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debug.debug();
                }
                return;
            }
            if (debugGui) {
                debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debug.debug();
            }
        }
    }
}