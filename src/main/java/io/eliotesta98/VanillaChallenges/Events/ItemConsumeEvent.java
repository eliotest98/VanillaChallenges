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
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemConsume(org.bukkit.event.player.PlayerItemDamageEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String itemConsumingByPlayer = e.getItem().getType().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                debugUtils.addLine("ItemConsumeEvent PlayerConsuming= " + playerName);
                if (itemConsume.equalsIgnoreCase("ALL")) {
                    debugUtils.addLine("ItemConsumeEvent Conditions= 0");
                    Main.dailyChallenge.increment(playerName, point);
                } else {
                    debugUtils.addLine("ItemConsumeEvent ItemConsumeByPlayer= " + itemConsumingByPlayer);
                    debugUtils.addLine("ItemConsumeEvent ItemConsumeConfig= " + itemConsume);
                    if (itemConsumingByPlayer.equalsIgnoreCase(itemConsume)) {
                        debugUtils.addLine("ItemConsumeEvent Conditions= 1");
                        Main.dailyChallenge.increment(playerName, point);
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("ItemConsumeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemConsumeEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}
