package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ItemBreakEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ItemBreakEvent");
    private String item = Main.dailyChallenge.getItem();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreakItem(org.bukkit.event.player.PlayerItemBreakEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String brokenItemByPlayer = e.getBrokenItem().getType().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                debugUtils.addLine("ItemBreakEvent PlayerBreaking= " + playerName);
                if (item.equalsIgnoreCase("ALL")) {
                    debugUtils.addLine("ItemBreakEvent Conditions= 0");
                    Main.dailyChallenge.increment(playerName, point);
                } else {
                    debugUtils.addLine("ItemBreakEvent ItemBrokenByPlayer= " + brokenItemByPlayer);
                    debugUtils.addLine("ItemBreakEvent ItemBrokenConfig= " + item);
                    if (item.equalsIgnoreCase(brokenItemByPlayer)) {
                        debugUtils.addLine("ItemBreakEvent Conditions= 1");
                        Main.dailyChallenge.increment(playerName, point);
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("ItemBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemBreakEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}

