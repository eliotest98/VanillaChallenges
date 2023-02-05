package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class FurnaceBurnEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("FurnaceCookEvent");
    private final ArrayList<String> itemsBurn = Main.instance.getDailyChallenge().getItems();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCookItem(org.bukkit.event.inventory.FurnaceExtractEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String itemBurnByPlayer = e.getItemType().toString();
        final int amount = e.getItemAmount();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("FurnaceCookEvent PlayerCooking= " + playerName);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("FurnaceCookEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("FurnaceCookEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("FurnaceCookEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("FurnaceCookEvent");
                }
                return;
            }

            if(!itemsBurn.isEmpty() && !itemsBurn.contains(itemBurnByPlayer)) {
                if (debugActive) {
                    debugUtils.addLine("FurnaceCookEvent ItemBurnByPlayer= " + itemBurnByPlayer);
                    debugUtils.addLine("FurnaceCookEvent ItemBurnConfig= " + itemsBurn);
                    debugUtils.addLine("FurnaceCookEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("FurnaceCookEvent");
                }
                return;
            }
            Main.dailyChallenge.increment(playerName, (long) amount * point);
            if (debugActive) {
                debugUtils.addLine("FurnaceCookEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("FurnaceCookEvent");
            }
        });
    }
}
