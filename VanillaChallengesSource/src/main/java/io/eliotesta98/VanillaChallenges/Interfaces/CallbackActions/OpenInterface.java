package io.eliotesta98.VanillaChallenges.Interfaces.CallbackActions;

import io.eliotesta98.VanillaChallenges.Core.Main;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.ArrayList;
import java.util.List;

public class OpenInterface extends Callback {

    @Override
    public void execute(InventoryClickEvent inventoryClickEvent, List<?> items) {
        setPlayer(inventoryClickEvent.getWhoClicked());
        setInventory(inventoryClickEvent.getInventory());
        setClickedSlot(inventoryClickEvent.getSlot());
        setClickedItem(inventoryClickEvent.getCurrentItem());

        int count = 1;
        int number = getInterface().getSizeModificableSlot();
        List<Object> realItems = new ArrayList<>();
        for (Object entry : items) {
            if (0 < count && number > 0) {
                realItems.add(entry);
                number--;
            }
            count++;
        }

        Main.instance.getConfigGestion().getInterfaces().get(getInterface().getNameInterfaceToOpen()).openInterface(realItems, getPlayer(), 1);
    }
}
