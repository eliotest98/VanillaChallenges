package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftingEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("CraftItemEvent");
    private String itemCrafting = Main.instance.getDailyChallenge().getItem();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftingItem(CraftItemEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (itemCrafting.equalsIgnoreCase("ALL")) {
                    Main.dailyChallenge.increment(e.getWhoClicked().getName());
                } else {
                    if (e.getInventory().getRecipe().getResult().getType().toString() == itemCrafting) {
                        Main.dailyChallenge.increment(e.getWhoClicked().getName());
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getWhoClicked().getName());
        if (debugActive) {
            debugUtils.addLine("CraftItemEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("CraftItemEvent");
        }
        return;
    }
}
