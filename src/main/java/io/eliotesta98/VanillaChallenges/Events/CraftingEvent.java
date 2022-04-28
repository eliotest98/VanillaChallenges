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
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftingItem(CraftItemEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getWhoClicked().getName();
        final String recipePlayer = e.getRecipe().getResult().getType().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("BlockBreakEvent PlayerCrafting= " + playerName);
                }
                if (itemCrafting.equalsIgnoreCase("ALL")) {
                    if (debugActive) {
                        debugUtils.addLine("CraftItemEvent Conditions= 0");
                    }
                    Main.dailyChallenge.increment(playerName, point);
                    if (debugActive) {
                        debugUtils.addLine("CraftItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("CraftItemEvent");
                    }
                    return;
                } else {
                    if (debugActive) {
                        debugUtils.addLine("CraftItemEvent RecipePlayer= " + recipePlayer);
                        debugUtils.addLine("CraftItemEvent RecipeConfig= " + itemCrafting);
                    }
                    if (recipePlayer.equalsIgnoreCase(itemCrafting)) {
                        if (debugActive) {
                            debugUtils.addLine("CraftItemEvent Conditions= 1");
                        }
                        Main.dailyChallenge.increment(playerName, point);
                        if (debugActive) {
                            debugUtils.addLine("CraftItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("CraftItemEvent");
                        }
                        return;
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("CraftItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("CraftItemEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getWhoClicked().getName());
    }
}
