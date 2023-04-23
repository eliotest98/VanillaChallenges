package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class EatEvent implements Listener {

    private final HashMap<String, Integer> foodLevels = new HashMap<>();
    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("EatEvent");
    private final ArrayList<String> items = Main.dailyChallenge.getItems();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(org.bukkit.event.entity.FoodLevelChangeEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getEntity().getName();
        final String worldName = e.getEntity().getWorld().getName();
        final int foodLevel = e.getFoodLevel();
        ItemStack itemUsedByPlayer;
        if(Main.version113) {
            itemUsedByPlayer = e.getItem();
        } else {
            itemUsedByPlayer = e.getEntity().getInventory().getItemInHand();
        }
        ItemStack finalItemUsedByPlayer = itemUsedByPlayer;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("EatEvent PlayerEating= " + playerName);
            }
            if (foodLevels.get(playerName) == null) {
                foodLevels.put(playerName, Math.min(foodLevel, 20));
                Main.dailyChallenge.increment(playerName, Math.abs(point));
            } else {
                if (finalItemUsedByPlayer != null) {
                    int number = foodLevel - foodLevels.get(playerName);
                    foodLevels.remove(playerName);
                    foodLevels.put(playerName, Math.min(foodLevel, 20));

                    if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                        if (debugActive) {
                            debugUtils.addLine("EatEvent WorldsConfig= " + worldsEnabled);
                            debugUtils.addLine("EatEvent PlayerWorld= " + worldName);
                            debugUtils.addLine("EatEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("EatEvent");
                        }
                        return;
                    }

                    if(!items.isEmpty() && !items.contains(finalItemUsedByPlayer.getType().toString())) {
                        if (debugActive) {
                            debugUtils.addLine("EatEvent ItemConsumedByPlayer= " + finalItemUsedByPlayer);
                            debugUtils.addLine("EatEvent ItemConsumedConfig= " + items);
                            debugUtils.addLine("EatEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("EatEvent");
                        }
                        return;
                    }
                    Main.dailyChallenge.increment(playerName, (long) number * Math.abs(point));
                } else {
                    foodLevels.remove(playerName);
                    foodLevels.put(playerName, foodLevel);
                    Main.dailyChallenge.increment(playerName, Math.abs(point));
                }
            }
            if (debugActive) {
                debugUtils.addLine("EatEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("EatEvent");
            }
        });
    }
}
