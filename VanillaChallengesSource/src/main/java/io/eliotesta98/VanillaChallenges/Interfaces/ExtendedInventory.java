package io.eliotesta98.VanillaChallenges.Interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ExtendedInventory {

    private Inventory inventory;
    private Player player;
    private int page;
    private Interface customInterface;
    private List<?> items = new ArrayList<>();

    public ExtendedInventory() {

    }

    public ExtendedInventory(Inventory inventory, Player player, int page, Interface customInterface, List<?> items) {
        this.inventory = inventory;
        this.player = player;
        this.page = page;
        this.customInterface = customInterface;
        this.items = items;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Interface getCustomInterface() {
        return customInterface;
    }

    public void setCustomInterface(Interface customInterface) {
        this.customInterface = customInterface;
    }

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "ExtendedInventory{" +
                "inventory=" + inventory +
                ", player=" + player +
                ", page=" + page +
                ", customInterface=" + customInterface +
                ", items=" + items +
                '}';
    }
}
