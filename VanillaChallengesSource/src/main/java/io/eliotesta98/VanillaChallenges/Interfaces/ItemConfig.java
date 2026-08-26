package io.eliotesta98.VanillaChallenges.Interfaces;

import com.HeroxWar.HeroxCore.SoundGesture.SoundType;
import com.HeroxWar.HeroxCore.Utils.Texture;
import com.HeroxWar.HeroxCore.Utils.TextureException;
import io.eliotesta98.VanillaChallenges.Core.Main;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.logging.Level;

// This class manage the creation of items
// The difference between other plugin ItemConfig classes is that class not insert the lore if the placeholder is empty or null
public class ItemConfig {

    private String name, texture, nameItemConfig;
    private Material material;
    private SoundType soundClick;
    private int customModelData, amount;
    private short durability;
    private List<String> lore, itemFlags;
    private boolean hiddenTooltip;
    private Map<String, List<String>> actions;
    private Map<Enchantment, Integer> enchants;
    private Color rgb;

    public ItemConfig(String material, int amount) {
        this("", "", material, "", new ArrayList<>(),
                null, 0, amount, false, new HashMap<>(),
                new ArrayList<>(), null, null);
    }

    public ItemConfig(String name, String material, String texture, List<String> lore, int customModelData, int amount,
                      boolean hiddenTooltip, Map<Enchantment, Integer> enchants, List<String> itemFlags, Color rgb) {
        this("", name, material, texture, lore,
                null, customModelData, amount, hiddenTooltip, enchants, itemFlags, null, rgb);
    }

    public ItemConfig(String nameItemConfig, String name, String material, String texture, List<String> lore,
                      SoundType soundClick, int customModelData, int amount, boolean hiddenTooltip,
                      Map<Enchantment, Integer> enchants, List<String> itemFlags, Map<String, List<String>> actions, Color rgb) {
        this.name = name;
        material = material.trim();
        if (material.contains(";")) {
            String[] split = material.split(";");
            try {
                this.durability = Short.parseShort(split[1]);
            } catch (NumberFormatException e) {
                Main.messageGesturePaper.log("Invalid data value: " + this.durability, Level.WARNING);
                this.durability = 0;
            }
            this.material = Material.getMaterial(split[0].toUpperCase());
            if (this.material == null) {
                Main.messageGesturePaper.log("Invalid material: " + split[0], Level.WARNING);
                this.material = Material.STONE;
            }
        } else {
            this.durability = 0;
            this.material = Material.getMaterial(material.toUpperCase());
            if (this.material == null) {
                Main.messageGesturePaper.log("Invalid material: " + material, Level.WARNING);
                this.material = Material.STONE;
            }
        }
        this.texture = texture;
        this.lore = lore;
        this.soundClick = soundClick;
        this.nameItemConfig = nameItemConfig;
        this.customModelData = customModelData;
        this.amount = amount;
        this.hiddenTooltip = hiddenTooltip;
        this.enchants = enchants;
        this.itemFlags = itemFlags;
        this.actions = actions;
        this.rgb = rgb;
    }

    public ItemStack createItemConfig(NbtList nbtList) {
        // Create the base item
        ItemStack item = createBaseItem();

        // Apply skull texture if head
        item = applySkullTextureIfHead(item, nbtList);

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return item;
        }

        // Apply lore
        applyLore(itemMeta, nbtList);
        // Apply tooltip and item flags
        applyBasicProperties(itemMeta);
        // Apply enchants
        applyEnchants(itemMeta);
        // Apply Display Name
        applyDisplayName(itemMeta, nbtList);
        // Apply custom model data
        applyCustomModelData(itemMeta);
        // Apply special materials
        applySpecialMaterials(item);

        item.setItemMeta(itemMeta);

        if (item.getType() == Material.AIR) {
            return item;
        }

        return nbtList.applyNbt(item);
    }

    @SuppressWarnings("deprecation")
    private ItemStack createBaseItem() {
        if (durability == 0) {
            ItemStack item = new ItemStack(material);
            item.setAmount(Math.max(1, Math.min(64, amount)));
            return item;
        } else {
            return new ItemStack(material, Math.max(1, Math.min(64, amount)), this.durability);
        }
    }

    private ItemStack applySkullTextureIfHead(ItemStack item, NbtList nbtList) {
        if (material != null && (material.toString().equalsIgnoreCase("PLAYER_HEAD") || material.toString().contains("SKULL"))) {
            boolean fromNbt = false;
            if (nbtList.containsKey("{texture}")) {
                texture = nbtList.getNbt("{texture}");
                fromNbt = true;
            }
            if (texture == null || texture.trim().isEmpty()) {
                return item;
            }

            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta == null) return item;

            try {
                ItemStack itemToReturn = Texture.setCustomTexture(item, texture);
                if (fromNbt) {
                    texture = null;
                }
                return itemToReturn;
            } catch (TextureException e) {
                Main.messageGesturePaper.log("Failed to apply custom texture to item: " + texture, Level.WARNING);
                Main.messageGesturePaper.log(e.getMessage(), Level.WARNING);
            }
        }
        return item;
    }

    private void applyLore(ItemMeta itemMeta, NbtList nbtList) {
        if (lore == null || lore.isEmpty()) {
            return;
        }

        List<String> newLore = new ArrayList<>();
        for (String loreLine : lore) {
            if (loreLine != null) {
                String processedLine = replacePlaceholders(loreLine, nbtList);
                if (processedLine.equalsIgnoreCase("")) {
                    continue;
                }

                if(processedLine.contains("\n")) {
                    for(String singleLine: processedLine.split("\n")) {
                        newLore.add(Main.messageGesturePaper.applyColorLegacy(singleLine));
                    }
                } else {
                    newLore.add(Main.messageGesturePaper.applyColorLegacy(processedLine));
                }
            }
        }
        List<String> finalLore = new ArrayList<>();
        for(String loreLine: newLore) {
            String processedLine = replacePlaceholders(loreLine, nbtList);
            if (processedLine.equalsIgnoreCase("")) {
                continue;
            }
            finalLore.add(processedLine);
        }
        itemMeta.setLore(finalLore);
    }

    private String replacePlaceholders(String text, NbtList nbtList) {
        String result = text;

        for (String nbtString : nbtList.getKeys()) {
            if (result.contains(nbtString)) {
                String value = nbtList.getNbt(nbtString);
                if (value.equalsIgnoreCase("[]")) {
                    return "";
                }
                if (value.equalsIgnoreCase("NOBODY")) {
                    return "";
                }
                result = result.replace(nbtString, value);
            }
        }
        return result;
    }

    private void applyBasicProperties(ItemMeta itemMeta) {
        itemMeta.setHideTooltip(hiddenTooltip);

        if (itemFlags != null) {
            for (String flag : itemFlags) {
                if (flag != null && !flag.trim().isEmpty()) {
                    try {
                        itemMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        Main.messageGesturePaper.log("Invalid item flag: " + flag, Level.WARNING);
                    }
                }
            }
        }
    }

    private void applyEnchants(ItemMeta itemMeta) {
        if (enchants != null && !enchants.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
                if (enchant.getKey() != null && enchant.getValue() != null) {
                    int level = Math.max(1, Math.min(255, enchant.getValue()));
                    itemMeta.addEnchant(enchant.getKey(), level, true);
                }
            }
        }
    }

    private void applyDisplayName(ItemMeta itemMeta, NbtList nbtList) {
        if (name != null && !name.trim().isEmpty()) {
            String processedName = replacePlaceholders(name, nbtList);
            itemMeta.setDisplayName(Main.messageGesturePaper.applyColorLegacy(processedName));
        }
    }

    private void applyCustomModelData(ItemMeta itemMeta) {
        if (customModelData > 0) {
            itemMeta.setCustomModelData(customModelData);
        }
    }

    private void applySpecialMaterials(ItemStack item) {
        if (material == null) return;

        if (material.toString().contains("LEATHER_") && !material.toString().contains("ARMOR") && rgb != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta) meta).setColor(rgb);
                item.setItemMeta(meta);
            }
        } else if (material.toString().contains("FIREWORK_") && rgb != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof FireworkEffectMeta) {
                ((FireworkEffectMeta) meta).setEffect(FireworkEffect.builder().withColor(rgb).build());
                item.setItemMeta(meta);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getNameItemConfig() {
        return nameItemConfig;
    }

    public void setNameItemConfig(String nameItemConfig) {
        this.nameItemConfig = nameItemConfig;
    }

    public SoundType getSoundClick() {
        return soundClick;
    }

    public void setSoundClick(SoundType soundClick) {
        this.soundClick = soundClick;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<String> getItemFlags() {
        return itemFlags;
    }

    public void setItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags;
    }

    public boolean isHiddenTooltip() {
        return hiddenTooltip;
    }

    public void setHiddenTooltip(boolean hiddenTooltip) {
        this.hiddenTooltip = hiddenTooltip;
    }

    public Map<String, List<String>> getActions() {
        return actions;
    }

    public void setActions(Map<String, List<String>> actions) {
        this.actions = actions;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public void setEnchants(Map<Enchantment, Integer> enchants) {
        this.enchants = enchants;
    }

    public Color getRgb() {
        return rgb;
    }

    public void setRgb(Color rgb) {
        this.rgb = rgb;
    }

    public short getDurability() {
        return durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemConfig that = (ItemConfig) o;
        return getCustomModelData() == that.getCustomModelData() && getAmount() == that.getAmount() && isHiddenTooltip() == that.isHiddenTooltip() && Objects.equals(getName(), that.getName()) && Objects.equals(getMaterial(), that.getMaterial()) && Objects.equals(getTexture(), that.getTexture()) && Objects.equals(getNameItemConfig(), that.getNameItemConfig()) && Objects.equals(getSoundClick(), that.getSoundClick()) && Objects.equals(getLore(), that.getLore()) && Objects.equals(getItemFlags(), that.getItemFlags()) && Objects.equals(getActions(), that.getActions()) && Objects.equals(getEnchants(), that.getEnchants()) && Objects.equals(getRgb(), that.getRgb());
    }

    public boolean equals(ItemStack itemStack, boolean amount) {
        if (itemStack == null) {
            return false;
        }
        if (amount) {
            return getDurability() == itemStack.getDurability() && getAmount() == itemStack.getAmount() && Objects.equals(getMaterial(), itemStack.getType());
        } else {
            return getDurability() == itemStack.getDurability() && Objects.equals(getMaterial(), itemStack.getType());
        }
    }

    @Override
    public ItemConfig clone() {
        return new ItemConfig(this.nameItemConfig, this.name, this.material.toString() + ";" + this.durability, this.texture, this.lore, this.soundClick, this.customModelData, this.amount, this.hiddenTooltip, this.enchants, this.itemFlags, this.actions, this.rgb);
    }

    public ItemConfig clone(String material) {
        return new ItemConfig(this.nameItemConfig, this.name, material, this.texture, this.lore, this.soundClick, this.customModelData, this.amount, this.hiddenTooltip, this.enchants, this.itemFlags, this.actions, this.rgb);
    }

    @Override
    public String toString() {
        return "ItemConfig{" +
                "name='" + name + '\'' +
                ", texture='" + texture + '\'' +
                ", nameItemConfig='" + nameItemConfig + '\'' +
                ", material=" + material +
                ", soundClick=" + soundClick +
                ", customModelData=" + customModelData +
                ", amount=" + amount +
                ", durability=" + durability +
                ", lore=" + lore +
                ", itemFlags=" + itemFlags +
                ", hiddenTooltip=" + hiddenTooltip +
                ", actions=" + actions +
                ", enchants=" + enchants +
                ", rgb=" + rgb +
                '}';
    }
}
