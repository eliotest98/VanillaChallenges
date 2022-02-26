package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HarvestEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("HarvestEvent");
    private String item = Main.dailyChallenge.getItem();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHarvestEvent(org.bukkit.event.player.PlayerHarvestBlockEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                int number = 0;
                for(int i=0;i<e.getItemsHarvested().size();i++) {
                    number = number + e.getItemsHarvested().get(i).getAmount();
                }
                if (item.equalsIgnoreCase("ALL")) {
                    Main.dailyChallenge.increment(e.getPlayer().getName(), (long) point * number);
                } else {
                    if (item.equalsIgnoreCase(e.getHarvestedBlock().getType().toString())) {
                        Main.dailyChallenge.increment(e.getPlayer().getName(), (long) point * number);
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("HarvestEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("HarvestEvent");
        }
        return;
    }
}
