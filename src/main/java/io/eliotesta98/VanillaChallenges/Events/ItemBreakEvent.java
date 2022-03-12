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
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (item.equalsIgnoreCase("ALL")) {
                    Main.dailyChallenge.increment(e.getPlayer().getName(), point);
                } else {
                    if (item.equalsIgnoreCase(e.getBrokenItem().getType().toString())) {
                        Main.dailyChallenge.increment(e.getPlayer().getName(), point);
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("ItemBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ItemBreakEvent");
        }
        return;
    }
}

