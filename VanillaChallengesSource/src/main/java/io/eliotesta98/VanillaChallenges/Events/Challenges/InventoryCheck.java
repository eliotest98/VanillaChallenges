package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class InventoryCheck {

    private final DebugUtils debugUtils = new DebugUtils("InventoryCheck");
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("Inventory");
    private final int numberSlot = Main.instance.getDailyChallenge().getNumber();
    private final int timeTaskInMinute = Main.instance.getDailyChallenge().getMinutes();
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final List<String> items = Main.instance.getDailyChallenge().getItems();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

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

                if (superiorSkyBlock2Enabled) {
                    if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(p.getName()))) {
                        if (debugActive) {
                            debugUtils.addLine("Player is inside his own island");
                        }
                    } else {
                        if (debugActive) {
                            debugUtils.addLine("Player isn't inside his own island");
                            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug();
                        }
                        continue;
                    }
                }

                if(!Controls.hasPermission(p.getName())) {
                    return;
                }

                if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                    continue;
                }

                int sizeInventory = 0;
                boolean itemCheck = items.isEmpty();
                Label: for (int i = 0; i < p.getInventory().getStorageContents().length; i++) {
                    if (p.getInventory().getItem(i) != null) {
                        sizeInventory++;
                        for(String item: items) {
                            if(p.getInventory().getItem(i).getType().toString().equalsIgnoreCase(item) && !itemCheck) {
                                itemCheck = true;
                                break Label;
                            }
                        }
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("Player= " + p.getName());
                    debugUtils.addLine("InventorySizePlayer= " + sizeInventory);
                    debugUtils.addLine("InventorySizeConfig= " + numberSlot);
                    debugUtils.addLine("InventoryItemConfig= " + items);
                    debugUtils.addLine("InventoryItemCheck= " + itemCheck);
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
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        }, 0, (long) timeTaskInMinute * 60 * 20);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(task,"InventoryEvent",false);
    }
}
