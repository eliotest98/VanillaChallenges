package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.ArrayList;

public class CraftingEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("CraftItemEvent");
    private final ArrayList<String> itemsCrafting = Main.instance.getDailyChallenge().getItems();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftingItem(CraftItemEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getWhoClicked().getName();
        final String worldName = e.getWhoClicked().getWorld().getName();
        final String recipePlayer = e.getRecipe().getResult().getType().toString();
        final int amount = e.getRecipe().getResult().getAmount();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("CraftItemEvent PlayerCrafting= " + playerName);
            }
            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("CraftItemEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("CraftItemEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("CraftItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("CraftItemEvent");
                }
                return;
            }
            if (!itemsCrafting.isEmpty() && !itemsCrafting.contains(recipePlayer)) {
                if (debugActive) {
                    debugUtils.addLine("CraftItemEvent RecipePlayer= " + recipePlayer);
                    debugUtils.addLine("CraftItemEvent RecipeConfig= " + itemsCrafting);
                    debugUtils.addLine("CraftItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("CraftItemEvent");
                }
                return;
            }
            Main.dailyChallenge.increment(playerName, (long) point * amount);
            if (debugActive) {
                debugUtils.addLine("CraftItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("CraftItemEvent");
            }
        });
    }
}
