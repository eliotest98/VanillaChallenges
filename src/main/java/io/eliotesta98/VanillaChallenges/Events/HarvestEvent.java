package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HarvestEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("HarvestEvent");
    private final ArrayList<String> items = Main.dailyChallenge.getItems();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHarvestEvent(org.bukkit.event.player.PlayerHarvestBlockEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final List<ItemStack> itemsHarvested = e.getItemsHarvested();
        final String blockHarvested = e.getHarvestedBlock().getType().toString();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {

            if (debugActive) {
                debugUtils.addLine("HarvestEvent PlayerHarvesting= " + playerName);
                debugUtils.addLine("HarvestEvent PlayerSneaking= " + sneakingPlayer);
                debugUtils.addLine("HarvestEvent ConfigSneaking= " + sneaking);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("HarvestEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("HarvestEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("HarvestEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("HarvestEvent");
                }
                return;
            }

            if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("HarvestEvent ConfigSneaking= " + sneaking);
                    debugUtils.addLine("HarvestEvent PlayerSneaking= " + sneakingPlayer);
                    debugUtils.addLine("HarvestEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("HarvestEvent");
                }
                return;
            }

            if(!items.isEmpty() && !items.contains(blockHarvested)) {
                if (debugActive) {
                    debugUtils.addLine("HarvestEvent BlockHarvestedByPlayer= " + blockHarvested);
                    debugUtils.addLine("HarvestEvent BlockHarvestedConfig= " + items);
                    debugUtils.addLine("HarvestEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("HarvestEvent");
                }
                return;
            }

            int number = 0;
            for (ItemStack itemStack : itemsHarvested) {
                number = number + itemStack.getAmount();
            }
            Main.dailyChallenge.increment(playerName, (long) point * number);

            if (debugActive) {
                debugUtils.addLine("HarvestEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("HarvestEvent");
            }
        });
    }
}
