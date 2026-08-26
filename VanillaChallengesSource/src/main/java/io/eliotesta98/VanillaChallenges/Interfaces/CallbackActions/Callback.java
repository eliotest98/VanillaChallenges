package io.eliotesta98.VanillaChallenges.Interfaces.CallbackActions;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Interfaces.Interface;
import io.eliotesta98.VanillaChallenges.Interfaces.ItemConfig;
import io.eliotesta98.VanillaChallenges.Interfaces.NbtList;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Map;

public abstract class Callback {

    private HumanEntity player;
    private Inventory inventory;
    private int clickedSlot;
    private ItemStack clickedItem;
    private Interface iInterface;
    private NbtList nbtList = new NbtList();

    public abstract void execute(InventoryClickEvent inventoryClickEvent, List<?> items);

    public Player getPlayer() {
        return (Player) player;
    }

    public void setPlayer(HumanEntity player) {
        this.player = player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getClickedSlot() {
        return clickedSlot;
    }

    public void setClickedSlot(int clickedSlot) {
        this.clickedSlot = clickedSlot;
    }

    public ItemStack getClickedItem() {
        return clickedItem;
    }

    public void setClickedItem(ItemStack clickedItem) {
        this.clickedItem = clickedItem;
        this.nbtList.deserializeAndAdd(clickedItem);
        this.iInterface = Main.instance.getConfigGestion().getInterfaces().get(nbtList.getNbt("{currentInterface}"));
    }

    public void setSlot(int slot, int amount, Inventory inventory, ItemConfig itemConfig) {
        ItemStack itemStack = itemConfig.createItemConfig(getNbtList());
        itemStack.setAmount(amount);
        inventory.setItem(slot, itemStack);
    }

    public void setSlot(int slot, int amount, Interface anInterface, Inventory inventory, String nameItemConfig) {
        Map.Entry<String, ItemConfig> itemConfig = getItemConfig(anInterface, nameItemConfig);
        ItemStack itemStack = itemConfig.getValue().createItemConfig(getNbtList());
        itemStack.setAmount(amount);
        inventory.setItem(slot, itemStack);
    }

    public Map.Entry<String, ItemConfig> getItemConfig(Interface anInterface, String nameItemConfig) {
        for (Map.Entry<String, ItemConfig> item : anInterface.getItemsConfig().entrySet()) {
            if (item.getValue().getNameItemConfig().equalsIgnoreCase(nameItemConfig)) {
                return item;
            }
        }
        return null;
    }

    public void setSlot(int slot, Inventory inventory, ItemConfig itemConfig) {
        setSlot(slot, 1, inventory, itemConfig);
    }

    public void setSlot(int slot, Interface anInterface, Inventory inventory, String nameItemConfig) {
        setSlot(slot, 1, anInterface, inventory, nameItemConfig);
    }

    public NbtList getNbtList() {
        return nbtList;
    }

    public void setNbtList(NbtList nbtList) {
        this.nbtList = nbtList;
    }

    public Interface getInterface() {
        return iInterface;
    }

    public void setInterface(Interface iInterface) {
        this.iInterface = iInterface;
    }
}
