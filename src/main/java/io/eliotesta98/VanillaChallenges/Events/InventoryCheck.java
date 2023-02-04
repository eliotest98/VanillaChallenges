package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;

public class InventoryCheck {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("Inventory");
    private final int numberSlot = Main.instance.getDailyChallenge().getNumber();
    private final int timeTaskInMinute = Main.instance.getDailyChallenge().getMinutes();
    private final int point = Main.dailyChallenge.getPoint();
    private final String item = Main.dailyChallenge.getItem();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    public InventoryCheck() {
        start();
    }

    public void start() {
        execute();
    }

    public void execute() {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            long tempo = System.currentTimeMillis();
            for (Player p : Bukkit.getOnlinePlayers()) {

                String worldName = p.getWorld().getName();

                if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                    if (debugActive) {
                        debugUtils.addLine("InventoryChallenge WorldsConfig= " + worldsEnabled);
                        debugUtils.addLine("InventoryChallenge PlayerWorld= " + worldName);
                        debugUtils.addLine("InventoryChallenge execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("InventoryChallenge");
                    }
                    continue;
                }

                int sizeInventory = 0;
                boolean itemCheck = item.equalsIgnoreCase("ALL");
                for (int i = 0; i < p.getInventory().getStorageContents().length; i++) {
                    if (p.getInventory().getItem(i) != null) {
                        sizeInventory++;
                        if(p.getInventory().getItem(i).getType().toString().equalsIgnoreCase(item) && !itemCheck) {
                            itemCheck = true;
                        }
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("InventoryChallenge Player= " + p.getName());
                    debugUtils.addLine("InventoryChallenge InventorySizePlayer= " + sizeInventory);
                    debugUtils.addLine("InventoryChallenge InventorySizeConfig= " + numberSlot);
                    debugUtils.addLine("InventoryChallenge InventoryItemConfig= " + item);
                    debugUtils.addLine("InventoryChallenge InventoryItemCheck= " + itemCheck);
                }
                if(numberSlot != -1) {
                    if (sizeInventory == numberSlot && itemCheck) {
                        Main.instance.getDailyChallenge().increment(p.getName(), point);
                    }
                } else {
                    if(itemCheck) {
                        Main.instance.getDailyChallenge().increment(p.getName(), point);
                    }
                }
            }
            if (debugActive) {
                debugUtils.addLine("InventoryChallenge execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("InventoryChallenge");
            }
        }, 0, (long) timeTaskInMinute * 60 * 20);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(task,"InventoryEvent",false);
    }
}
