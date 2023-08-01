package io.eliotesta98.VanillaChallenges.Events.Challenges;

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
    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("EatEvent");
    private final ArrayList<String> items = Main.dailyChallenge.getItems();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(org.bukkit.event.entity.FoodLevelChangeEvent e) {
        debugUtils = new DebugUtils(e);
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
                debugUtils.addLine("PlayerEating= " + playerName);
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
                            debugUtils.addLine("WorldsConfig= " + worldsEnabled);
                            debugUtils.addLine("PlayerWorld= " + worldName);
                            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug();
                        }
                        return;
                    }

                    if(!items.isEmpty() && !items.contains(finalItemUsedByPlayer.getType().toString())) {
                        if (debugActive) {
                            debugUtils.addLine("ItemConsumedByPlayer= " + finalItemUsedByPlayer);
                            debugUtils.addLine("ItemConsumedConfig= " + items);
                            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug();
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
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
