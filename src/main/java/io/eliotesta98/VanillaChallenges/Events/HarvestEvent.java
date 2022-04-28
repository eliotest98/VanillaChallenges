package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HarvestEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("HarvestEvent");
    private String item = Main.dailyChallenge.getItem();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHarvestEvent(org.bukkit.event.player.PlayerHarvestBlockEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final List<ItemStack> itemsHarvested = e.getItemsHarvested();
        final String blockHarvested = e.getHarvestedBlock().getType().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("HarvestEvent PlayerHarvesting= " + playerName);
                }
                if (item.equalsIgnoreCase("ALL")) {
                    int number = 0;
                    for (int i = 0; i < itemsHarvested.size(); i++) {
                        number = number + itemsHarvested.get(i).getAmount();
                    }
                    if (debugActive) {
                        debugUtils.addLine("HarvestEvent Conditions= 0");
                    }
                    Main.dailyChallenge.increment(playerName, (long) point * number);
                } else {
                    if (debugActive) {
                        debugUtils.addLine("HarvestEvent BlockHarvestedByPlayer= " + blockHarvested);
                        debugUtils.addLine("HarvestEvent BlockHarvestedConfig= " + item);
                    }
                    if (item.equalsIgnoreCase(blockHarvested)) {
                        int number = 0;
                        for (int i = 0; i < itemsHarvested.size(); i++) {
                            number = number + itemsHarvested.get(i).getAmount();
                        }
                        if (debugActive) {
                            debugUtils.addLine("HarvestEvent Conditions= 1");
                        }
                        Main.dailyChallenge.increment(playerName, (long) point * number);
                    }
                }
                if (debugActive) {
                    debugUtils.addLine("HarvestEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("HarvestEvent");
                }
                return;
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}
