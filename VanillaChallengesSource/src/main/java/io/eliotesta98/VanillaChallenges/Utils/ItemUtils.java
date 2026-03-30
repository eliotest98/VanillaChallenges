package io.eliotesta98.VanillaChallenges.Utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.eliotesta98.VanillaChallenges.Core.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    @SuppressWarnings("deprecation")
    public static ItemStack getChest(String type, String name, List<String> lore) {
        ItemStack chest;
        chest = new ItemStack(Material.getMaterial(type), 1, (short) 0);
        ItemMeta itemm = chest.getItemMeta();
        // setto il nome
        itemm.setDisplayName(Main.messageGesturePaper.applyColorLegacy(name));
        ArrayList<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(Main.messageGesturePaper.applyColorLegacy(s));
        }
        itemm.setLore(newLore);
        chest.setItemMeta(itemm);
        try {
            NBTItem nbt = new NBTItem(chest);
            nbt.setBoolean("vc.chest", true);
            return nbt.getItem();
        } catch (NoClassDefFoundError ignore) {
            return chest;
        }
    }
}
