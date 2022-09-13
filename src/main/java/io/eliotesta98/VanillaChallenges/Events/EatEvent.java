package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EatEvent implements Listener {

    private HashMap<String, Integer> foodLevels = new HashMap<>();
    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("EatEvent");
    private String item = Main.dailyChallenge.getItem();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(org.bukkit.event.entity.FoodLevelChangeEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getEntity().getName();
        final int foodLevel = e.getFoodLevel();
        final ItemStack itemUsedByPlayer = e.getItem();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("EatEvent PlayerEating= " + playerName);
                }
                if (foodLevels.get(playerName) == null) {
                    if (foodLevel <= 20) {
                        foodLevels.put(playerName, foodLevel);
                    } else {
                        foodLevels.put(playerName, 20);
                    }
                    Main.dailyChallenge.increment(playerName, point);
                } else {
                    if (itemUsedByPlayer != null) {
                        int number = foodLevel - foodLevels.get(playerName);
                        foodLevels.remove(playerName);
                        if (foodLevel <= 20) {
                            foodLevels.put(playerName, foodLevel);
                        } else {
                            foodLevels.put(playerName, 20);
                        }
                        if(!item.equalsIgnoreCase("ALL") && !item.equalsIgnoreCase(itemUsedByPlayer.getType().toString())) {
                            if (debugActive) {
                                debugUtils.addLine("EatEvent ItemConsumedByPlayer= " + itemUsedByPlayer);
                                debugUtils.addLine("EatEvent ItemConsumedConfig= " + item);
                                debugUtils.addLine("EatEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("EatEvent");
                            }
                            return;
                        }
                        Main.dailyChallenge.increment(playerName, (long) number * point);
                    } else {
                        foodLevels.remove(playerName);
                        foodLevels.put(playerName, foodLevel);
                        Main.dailyChallenge.increment(playerName, point);
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("EatEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("EatEvent");
                }
                return;
            }
        });
    }
}
