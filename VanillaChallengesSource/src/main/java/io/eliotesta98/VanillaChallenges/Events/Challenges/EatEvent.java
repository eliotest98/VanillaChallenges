package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EatEvent implements Listener {

    private final HashMap<String, Integer> foodLevels = new HashMap<>();
    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGesture().getDebug().get("EatEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(org.bukkit.event.entity.FoodLevelChangeEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        final String playerName = e.getEntity().getName();
        final String worldName = e.getEntity().getWorld().getName();
        final int foodLevel = e.getFoodLevel();
        ItemStack itemUsedByPlayer = e.getEntity().getInventory().getItemInHand();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerEating= " + playerName);
            }
            if (foodLevels.get(playerName) == null) {
                foodLevels.put(playerName, Math.min(foodLevel, 20));
                Main.instance.getDailyChallenge().increment(playerName, Math.abs(point));
            } else {
                int number = foodLevel - foodLevels.get(playerName);
                foodLevels.remove(playerName);
                foodLevels.put(playerName, Math.min(foodLevel, 20));

                if(!Controls.hasPermission(playerName)) {
                    return;
                }

                if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                    return;
                }

                if (Controls.isItem(itemUsedByPlayer.getType().toString(), debugActive, debugUtils, tempo)) {
                    return;
                }

                Main.instance.getDailyChallenge().increment(playerName, (long) number * Math.abs(point));
            }
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
