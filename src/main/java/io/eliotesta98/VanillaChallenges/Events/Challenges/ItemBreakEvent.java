package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ItemBreakEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ItemBreakEvent");
    private final ArrayList<String> items = Main.dailyChallenge.getItems();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreakItem(org.bukkit.event.player.PlayerItemBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String brokenItemByPlayer = e.getBrokenItem().getType().toString();
        final boolean playerSneaking = e.getPlayer().isSneaking();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {

            if (debugActive) {
                debugUtils.addLine("ItemBreakEvent PlayerBreaking= " + playerName);
                debugUtils.addLine("ItemBreakEvent PlayerSneaking= " + playerSneaking);
                debugUtils.addLine("ItemBreakEvent ConfigSneaking= " + sneaking);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("ItemBreakEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("ItemBreakEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("ItemBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemBreakEvent");
                }
                return;
            }

            if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != playerSneaking) {
                if (debugActive) {
                    debugUtils.addLine("ItemBreakEvent ConfigSneaking= " + sneaking);
                    debugUtils.addLine("ItemBreakEvent PlayerSneaking= " + playerSneaking);
                    debugUtils.addLine("ItemBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemBreakEvent");
                }
                return;
            }

            if(!items.isEmpty() && !items.contains(brokenItemByPlayer)) {
                if (debugActive) {
                    debugUtils.addLine("ItemBreakEvent BlockHarvestedByPlayer= " + brokenItemByPlayer);
                    debugUtils.addLine("ItemBreakEvent BlockHarvestedConfig= " + items);
                    debugUtils.addLine("ItemBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemBreakEvent");
                }
                return;
            }

            Main.dailyChallenge.increment(playerName, point);

            if (debugActive) {
                debugUtils.addLine("ItemBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("ItemBreakEvent");
            }
        });
    }
}

