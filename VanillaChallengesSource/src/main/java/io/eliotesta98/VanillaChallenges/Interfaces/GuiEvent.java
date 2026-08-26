package io.eliotesta98.VanillaChallenges.Interfaces;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Interfaces.CallbackActions.*;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiEvent implements Listener {

    private final boolean debugGui = Main.instance.getConfigGestion().getDebug().get("ClickGui");
    private final CallbackActions callbackActions;

    public GuiEvent() {
        callbackActions = new CallbackActions(Main.messageGesturePaper);
        callbackActions.registerCallback("RIGHT_PAGE", new RightPage());
        callbackActions.registerCallback("LEFT_PAGE", new LeftPage());
        callbackActions.registerCallback("OPEN_INTERFACE", new OpenInterface());
        callbackActions.registerCallback("BACK_INTERFACE", new BackInterface());
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

    @SuppressWarnings({"CallToPrintStackTrace", "deprecation"})
    @EventHandler
    public void onClose(final InventoryCloseEvent inventoryCloseEvent) {
        try {
            if (inventoryCloseEvent.getPlayer() instanceof Player) {
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
                        Main.instance.getConfigGestion().getInterfaces().get(nbtItem.getString("{currentInterface}")).removeInventory(inventoryCloseEvent.getPlayer().getName());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void cancelOnClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof VanillaChallengesInterfaceHolder) {
            try {
                Inventory inventory = event.getWhoClicked().getOpenInventory().getTopInventory();
                if (inventory.getHolder() instanceof VanillaChallengesInterfaceHolder)
                    event.setCancelled(true);
            } catch (IncompatibleClassChangeError ignore) {
                // This is for not spam in versions < 1.21
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onClick(final InventoryClickEvent inventoryClickEvent) {
        DebugUtils debug = new DebugUtils("Gui");
        long tempo = System.currentTimeMillis();
        if (inventoryClickEvent.getWhoClicked() instanceof Player) {
            final Inventory inv = inventoryClickEvent.getClickedInventory();
            if (inv == null || !inv.getType().equals(InventoryType.CHEST)) {
                if (debugGui) {
                    debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debug.debug();
                }
                return;
            }
            // inventario lista player morti
            if (inv.getHolder() instanceof VanillaChallengesInterfaceHolder) {
                inventoryClickEvent.setCancelled(true);
                if (inv.getItem(inventoryClickEvent.getSlot()) == null
                        || inv.getItem(inventoryClickEvent.getSlot()).getType() == Material.AIR) {
                    return;
                }
                NBTItem nbtItem = new NBTItem(inventoryClickEvent.getCurrentItem());
                if (!nbtItem.hasTag("{currentInterface}")) {
                    return;
                }
                String typeInterface = nbtItem.getString("{currentInterface}");
                ClickType clickType = inventoryClickEvent.getClick();

                if (typeInterface.equalsIgnoreCase("Challenges")) {
                    List<String> slots = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getSlots();
                    List<Object> items = new ArrayList<>(Main.db.getChallenges());
                    Map<String, ItemConfig> itemConfigs = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getItemsConfig();
                    ItemConfig itemConfig = itemConfigs.get(slots.get(inventoryClickEvent.getSlot()));
                    if(itemConfig.equals(nbtItem.getItem(), false)) {
                        callbackActions.executeActions(itemConfigs, slots, clickType, inventoryClickEvent, items);
                    }
                }
            }
            if (debugGui) {
                debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debug.debug();
            }
        }
    }
}