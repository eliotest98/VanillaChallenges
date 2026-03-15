package io.eliotesta98.VanillaChallenges.Interfaces;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
        String encrypt = "";
        for (Map.Entry<String, String> nbt : nbts.entrySet()) {
            encrypt = encrypt + nbt.getKey() + ":" + nbt.getValue() + ";";
        }
        encrypt = encrypt.substring(0, encrypt.length() - 1);
        return encrypt;
    }

    // add nbt string format vc.asd=123
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
        NBTItem nbtItem = new NBTItem(itemStack);
        for (Map.Entry<String, String> nbt : nbts.entrySet()) {
            try {
                int anInteger = Integer.parseInt(nbt.getValue());
                nbtItem.setInteger(nbt.getKey(), anInteger);
            } catch (Exception ex) {
                nbtItem.setString(nbt.getKey(), nbt.getValue());
            }
        }
        return nbtItem.getItem();
    }

    @Override
    public String toString() {
        return "NbtList{" +
                "nbts=" + nbts +
                '}';
    }
}
