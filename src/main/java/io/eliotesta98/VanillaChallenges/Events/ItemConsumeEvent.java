package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ItemConsumeEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ItemConsumeEvent");
    private String itemConsume = Main.instance.getDailyChallenge().getItem();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemConsume(org.bukkit.event.player.PlayerItemDamageEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (itemConsume.equalsIgnoreCase("ALL")) {
                    Main.dailyChallenge.increment(e.getPlayer().getName());
                } else {
                    if (e.getItem().getType().toString().equalsIgnoreCase(itemConsume)) {
                        Main.dailyChallenge.increment(e.getPlayer().getName());
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("ItemConsumeEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ItemConsumeEvent");
        }
        return;
    }
}
