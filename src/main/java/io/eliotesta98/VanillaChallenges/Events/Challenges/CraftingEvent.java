package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.ArrayList;

public class CraftingEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("CraftItemEvent");
    private final ArrayList<String> itemsCrafting = Main.instance.getDailyChallenge().getItems();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftingItem(CraftItemEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final String playerName = e.getWhoClicked().getName();
        final String worldName = e.getWhoClicked().getWorld().getName();
        final String recipePlayer = e.getRecipe().getResult().getType().toString();
        final int amount = e.getRecipe().getResult().getAmount();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerCrafting= " + playerName);
            }

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(playerName))) {
                    if (debugActive) {
                        debugUtils.addLine("Player is inside his own island");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("Player isn't inside his own island");
                        debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("PlayerWorld= " + worldName);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
            if (!itemsCrafting.isEmpty() && !itemsCrafting.contains(recipePlayer)) {
                if (debugActive) {
                    debugUtils.addLine("RecipePlayer= " + recipePlayer);
                    debugUtils.addLine("RecipeConfig= " + itemsCrafting);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
            Main.dailyChallenge.increment(playerName, (long) point * amount);
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
