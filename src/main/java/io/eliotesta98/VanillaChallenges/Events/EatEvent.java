package io.eliotesta98.VanillaChallenges.Events;

import com.sun.org.apache.xpath.internal.objects.XNumber;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.HashMap;

public class EatEvent implements Listener {

    private HashMap<String,Integer> foodLevels = new HashMap<>();
    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("KillEvent");
    private String item = Main.dailyChallenge.getItem();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(org.bukkit.event.entity.FoodLevelChangeEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if(foodLevels.get(e.getEntity().getName()) == null) {
                    if(e.getFoodLevel() <= 20) {
                        foodLevels.put(e.getEntity().getName(),e.getFoodLevel());
                    } else {
                        foodLevels.put(e.getEntity().getName(),20);
                    }
                    Main.dailyChallenge.increment(e.getEntity().getName());
                } else {
                    if(e.getItem() != null) {
                        int number = e.getFoodLevel() - foodLevels.get(e.getEntity().getName());
                        foodLevels.remove(e.getEntity().getName());
                        if(e.getFoodLevel() <= 20) {
                            foodLevels.put(e.getEntity().getName(),e.getFoodLevel());
                        } else {
                            foodLevels.put(e.getEntity().getName(),20);
                        }
                        if(item.equalsIgnoreCase("ALL")) {
                            Main.dailyChallenge.increment(e.getEntity().getName(), number);
                        } else {
                            if(e.getItem().getType().toString().equalsIgnoreCase(item)) {
                                Main.dailyChallenge.increment(e.getEntity().getName(), number);
                            }
                        }
                    } else {
                        foodLevels.remove(e.getEntity().getName());
                        foodLevels.put(e.getEntity().getName(),e.getFoodLevel());
                        Main.dailyChallenge.increment(e.getEntity().getName());
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("ExpCollectorEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ExpCollectorEvent");
        }
        return;
    }
}
