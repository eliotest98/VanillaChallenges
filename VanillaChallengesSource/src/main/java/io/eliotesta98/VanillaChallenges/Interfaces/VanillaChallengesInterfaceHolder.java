package io.eliotesta98.VanillaChallenges.Interfaces;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class VanillaChallengesInterfaceHolder implements InventoryHolder {

	private final Inventory inv;

	public VanillaChallengesInterfaceHolder(int size, String title) {
		inv = Bukkit.createInventory(this, size, title);
	}

	@Override
	public @NotNull Inventory getInventory() {
		return inv;
	}

}