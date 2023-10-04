package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class AFKCheck {

    private final DebugUtils debugUtils = new DebugUtils("AFKEvent");
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("AFKEvent");
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");
    private final int numberSlot = Main.instance.getDailyChallenge().getNumber();
    private final int timeTaskInMinute = Main.instance.getDailyChallenge().getMinutes();
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final ArrayList<String> items = Main.instance.getDailyChallenge().getItems();

    private final HashMap<String, Location> playerLocation = new HashMap<>();

    public AFKCheck() {
        start();
    }

    public void start() {
        execute();
    }

    public void execute() {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            long tempo = System.currentTimeMillis();
            for (Player p : Bukkit.getOnlinePlayers()) {

                if (playerLocation.containsKey(p.getName())) {
                    if (!playerLocation.get(p.getName()).equals(p.getLocation())) {
                        playerLocation.replace(p.getName(), p.getLocation());
                        if (debugActive) {
                            debugUtils.addLine("NewPlayerLocation= " + p.getLocation());
                        }
                        continue;
                    }
                } else {
                    playerLocation.put(p.getName(), p.getLocation());
                    if (debugActive) {
                        debugUtils.addLine("FirstPlayerLocation= " + p.getLocation());
                    }
                    continue;
                }

                if (!Controls.isWorldEnable(p.getWorld().getName(), debugActive, debugUtils, tempo)) {
                    continue;
                }

                if (superiorSkyBlock2Enabled) {
                    if (!SuperiorSkyBlock2Utils.isAfkInIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(p.getName()))) {
                        if (debugActive) {
                            debugUtils.addLine("Player isn't afk for SuperiorSkyBlock2");
                            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug();
                        }
                        continue;
                    }
                }

                int sizeInventory = 0;
                boolean itemCheck = items.isEmpty();
                Label:
                for (int i = 0; i < p.getInventory().getStorageContents().length; i++) {
                    if (p.getInventory().getItem(i) != null) {
                        sizeInventory++;
                        for (String item : items) {
                            if (p.getInventory().getItem(i).getType().toString().equalsIgnoreCase(item) && !itemCheck) {
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
                if (numberSlot != -1) {
                    if (sizeInventory == numberSlot && itemCheck) {
                        Main.instance.getDailyChallenge().increment(p.getName(), point);
                    }
                } else {
                    if (itemCheck) {
                        Main.instance.getDailyChallenge().increment(p.getName(), point);
                    }
                }
            }
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        }, 0, (long) timeTaskInMinute * 60 * 20);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(task, "AFKEvent", false);
    }
}
