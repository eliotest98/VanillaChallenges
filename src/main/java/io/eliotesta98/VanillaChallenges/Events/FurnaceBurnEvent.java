package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FurnaceBurnEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("FurnaceCookEvent");
    private String itemBurn = Main.instance.getDailyChallenge().getItem();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCookItem(org.bukkit.event.inventory.FurnaceExtractEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (itemBurn.equalsIgnoreCase("ALL")) {
                    Main.dailyChallenge.increment(e.getPlayer().getName(), (long) e.getItemAmount() * point);
                } else {
                    if (e.getItemType().toString().equalsIgnoreCase(itemBurn)) {
                        Main.dailyChallenge.increment(e.getPlayer().getName(), (long) e.getItemAmount() * point);
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("FurnaceCookEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("FurnaceCookEvent");
        }
        return;
    }
}
