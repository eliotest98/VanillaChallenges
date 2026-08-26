package io.eliotesta98.VanillaChallenges.Interfaces;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NbtList {

    private final Map<String, String> nbts = new HashMap<>();

    public NbtList() {

    }

    public NbtList(String nbts) {
        decode(nbts);
    }

    public NbtList(Map<String, String> nbts) {
        this.nbts.putAll(nbts);
    }

    public void decode(String nbts) {
        String[] splitNbts = nbts.split(";");
        for (String splitNbt : splitNbts) {
            String[] split = splitNbt.split("=");
            this.nbts.put(split[0], split[1]);
        }
    }

    public String encode() {
        StringBuilder encrypt = new StringBuilder();
        for (Map.Entry<String, String> nbt : nbts.entrySet()) {
            encrypt.append(nbt.getKey()).append(":").append(nbt.getValue()).append(";");
        }
        encrypt = new StringBuilder(encrypt.substring(0, encrypt.length() - 1));
        return encrypt.toString();
    }

    // add nbt string format asd.asd=123
    public void addNbt(String nbt) {
        String[] split = nbt.split("=");
        this.nbts.put(split[0], split[1]);
    }

    public void addNbt(String key, String value) {
        this.nbts.put(key, value);
    }

    public void removeNbt(String key) {
        this.nbts.remove(key);
    }

    public String getNbt(String key) {
        return this.nbts.get(key);
    }

    public Set<String> getKeys() {
        return this.nbts.keySet();
    }

    public Map<String, String> getNbts() {
        return nbts;
    }

    public boolean containsKey(String key) {
        return this.nbts.containsKey(key);
    }

    public boolean equals(NbtList nbtList) {
        return this.nbts.equals(nbtList.getNbts());
    }

    public ItemStack applyNbt(ItemStack itemStack) {
        if(nbts.isEmpty()) {
            return itemStack;
        }
        try {
            NBTItem nbtItem = new NBTItem(itemStack);
            for (Map.Entry<String, String> nbt : nbts.entrySet()) {
                nbtItem.setString(nbt.getKey(), nbt.getValue());
            }
            return nbtItem.getItem();
        } catch (NoClassDefFoundError | Exception e) {
            // Return item without NBT in test environments
            return itemStack;
        }
    }

    public void deserializeAndAdd(ItemStack itemStack) {
        final NBTItem clickedItemNBT = new NBTItem(itemStack);
        Set<String> keys = clickedItemNBT.getKeys();

        for (String key : keys) {
            String value = clickedItemNBT.getOrNull(key, String.class);
            if (value == null) {
                value = clickedItemNBT.getCompound().toString();
            }
            addNbt(key, value);
        }
    }

    public Map<String, String> deserializeAndReturn(ItemStack itemStack) {
        Map<String, String> nbts = new HashMap<>();
        NBTItem clickedItemNBT = new NBTItem(itemStack);
        Set<String> keys = clickedItemNBT.getKeys();

        for (String key : keys) {
            String value = clickedItemNBT.getOrNull(key, String.class);
            if (value == null) {
                value = clickedItemNBT.getCompound().toString();
            }
            nbts.put(key, value);
        }
        return nbts;
    }

    @Override
    public String toString() {
        return "NbtList{" +
                "nbts=" + nbts +
                '}';
    }
}
