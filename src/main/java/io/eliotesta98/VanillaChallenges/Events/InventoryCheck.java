package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class InventoryCheck {

    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private static BukkitTask task;

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("Inventory");
    private int numberSlot = Main.instance.getDailyChallenge().getNumber();
    private int timeTaskInMinute = Main.instance.getDailyChallenge().getTime();
    private int point = Main.dailyChallenge.getPoint();

    public InventoryCheck() {
        start();
    }

    public void start() {
        execute();
    }

    public static void stop() {
        task.cancel();
    }

    public void execute() {
        task = scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                long tempo = System.currentTimeMillis();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    int sizeInventory = 0;
                    for (int i = 0; i < p.getInventory().getStorageContents().length; i++) {
                        if (p.getInventory().getItem(i) != null) {
                            sizeInventory++;
                        }
                    }
                    if (debugActive) {
                        debugUtils.addLine("InventoryChallenge Player= " + p.getName());
                        debugUtils.addLine("InventoryChallenge InventorySizePlayer= " + sizeInventory);
                        debugUtils.addLine("InventoryChallenge InventorySizeConfig= " + numberSlot);
                    }
                    if (sizeInventory == numberSlot) {
                        Main.instance.getDailyChallenge().increment(p.getName(), point);
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("InventoryChallenge execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("InventoryChallenge");
                }
            }
        }, 0, (long) timeTaskInMinute * 60 * 20);
    }
}
