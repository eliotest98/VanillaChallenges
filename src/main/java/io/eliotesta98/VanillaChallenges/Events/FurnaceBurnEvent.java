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
        final String playerName = e.getPlayer().getName();
        final String itemBurnByPlayer = e.getItemType().toString();
        final int amount = e.getItemAmount();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                debugUtils.addLine("FurnaceCookEvent PlayerCooking= " + playerName);
                if (itemBurn.equalsIgnoreCase("ALL")) {
                    debugUtils.addLine("FurnaceCookEvent Conditions= 0");
                    Main.dailyChallenge.increment(playerName, (long) amount * point);
                } else {
                    debugUtils.addLine("FurnaceCookEvent ItemBurnByPlayer= " + itemBurnByPlayer);
                    debugUtils.addLine("FurnaceCookEvent ItemBurnConfig= " + itemBurn);
                    if (itemBurnByPlayer.equalsIgnoreCase(itemBurn)) {
                        debugUtils.addLine("FurnaceCookEvent Conditions= 1");
                        Main.dailyChallenge.increment(playerName, (long) amount * point);
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("FurnaceCookEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("FurnaceCookEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}
