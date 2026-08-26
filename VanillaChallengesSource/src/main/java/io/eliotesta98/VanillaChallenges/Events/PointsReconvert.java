package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Interfaces.NbtList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PointsReconvert implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;
        Player player = event.getPlayer();
        NbtList nbtList = new NbtList();
        nbtList.deserializeAndAdd(item);
        if(nbtList.containsKey("vc.point")) {
            event.setCancelled(true);
            int amount = item.getAmount();
            player.getInventory().remove(item);
            Main.instance.getDailyChallenge().incrementCommands(player.getName(), amount);
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
