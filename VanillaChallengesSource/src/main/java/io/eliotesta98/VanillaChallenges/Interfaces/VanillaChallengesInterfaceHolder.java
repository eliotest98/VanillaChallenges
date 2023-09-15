package io.eliotesta98.VanillaChallenges.Interfaces;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class VanillaChallengesInterfaceHolder implements InventoryHolder {

	private Inventory inv;

	public VanillaChallengesInterfaceHolder(int size, String title) {
		inv = Bukkit.createInventory(this, size, title);
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

}