package io.eliotesta98.VanillaChallenges.Utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtils {

    public static ItemStack getChest(String type, String name, ArrayList<String> lore) {
        ItemStack chest = null;
        chest = new ItemStack(Material.getMaterial((String) type), 1, (short) 0);
        ItemMeta itemm = chest.getItemMeta();
        // setto il nome
        itemm.setDisplayName(ColorUtils.applyColor(name));
        ArrayList<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ColorUtils.applyColor(s));
        }
        itemm.setLore(newLore);
        chest.setItemMeta(itemm);
        NBTItem nbt = new NBTItem(chest);
        nbt.setBoolean("vc.chest", true);
        return nbt.getItem();
    }
}
