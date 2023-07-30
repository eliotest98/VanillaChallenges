package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class AFKCheck {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("AFKEvent");
    private final int numberSlot = Main.instance.getDailyChallenge().getNumber();
    private final int timeTaskInMinute = Main.instance.getDailyChallenge().getMinutes();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> items = Main.dailyChallenge.getItems();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

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
                            debugUtils.addLine("AFKEvent NewPlayerLocation= " + p.getLocation());
                        }
                        continue;
                    }
                } else {
                    playerLocation.put(p.getName(), p.getLocation());
                    if (debugActive) {
                        debugUtils.addLine("AFKEvent FirstPlayerLocation= " + p.getLocation());
                    }
                    continue;
                }

                String worldName = p.getWorld().getName();

                if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                    if (debugActive) {
                        debugUtils.addLine("AFKEvent WorldsConfig= " + worldsEnabled);
                        debugUtils.addLine("AFKEvent PlayerWorld= " + worldName);
                        debugUtils.addLine("AFKEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("AFKEvent");
                    }
                    continue;
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
                    debugUtils.addLine("AFKEvent Player= " + p.getName());
                    debugUtils.addLine("AFKEvent InventorySizePlayer= " + sizeInventory);
                    debugUtils.addLine("AFKEvent InventorySizeConfig= " + numberSlot);
                    debugUtils.addLine("AFKEvent InventoryItemConfig= " + items);
                    debugUtils.addLine("AFKEvent InventoryItemCheck= " + itemCheck);
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
                debugUtils.addLine("AFKEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("AFKEvent");
            }
        }, 0, (long) timeTaskInMinute * 60 * 20);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(task, "AFKEvent", false);
    }
}
