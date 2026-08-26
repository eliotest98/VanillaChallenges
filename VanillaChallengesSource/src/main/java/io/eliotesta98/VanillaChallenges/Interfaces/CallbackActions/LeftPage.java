package io.eliotesta98.VanillaChallenges.Interfaces.CallbackActions;

import io.eliotesta98.VanillaChallenges.Core.Main;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.ArrayList;
import java.util.List;

public class LeftPage extends Callback {

    @Override
    public void execute(InventoryClickEvent inventoryClickEvent, List<?> items) {
        setPlayer(inventoryClickEvent.getWhoClicked());
        setInventory(inventoryClickEvent.getInventory());
        setClickedSlot(inventoryClickEvent.getSlot());
        setClickedItem(inventoryClickEvent.getCurrentItem());

        List<String> slots = getInterface().getSlots();
        String nameItemConfig = getInterface().getItemsConfig().
                get(slots.get(Integer.parseInt(getNbtList().getNbt("{positionItem}"))))
                .getNameItemConfig();

        int pageNumber = Integer.parseInt(getNbtList().getNbt("{number}"));
        if (nameItemConfig.equalsIgnoreCase("LeftPage")) {
            List<Object> realItems = new ArrayList<>();
            int limit = (pageNumber - 1) * getInterface().getSizeModificableSlot();
            int count = 1;
            int number = getInterface().getSizeModificableSlot();
            if (pageNumber != 1) {
                for (Object entry : items) {
                    if (limit < count && number > 0) {
                        realItems.add(entry);
                        number--;
                    }
                    count++;
                }
            } else {
                for (Object entry : items) {
                    if (number != 0) {
                        realItems.add(entry);
                    } else {
                        break;
                    }
                    number--;
                }
            }
            Main.instance.getConfigGestion().getInterfaces().get(getInterface().getNameInterface()).openInterface(realItems, getPlayer(), pageNumber);
        }
    }
}
