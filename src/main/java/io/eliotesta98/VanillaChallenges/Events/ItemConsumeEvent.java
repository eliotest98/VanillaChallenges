package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ItemConsumeEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ItemConsumeEvent");
    private final String itemConsume = Main.instance.getDailyChallenge().getItem();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemConsume(org.bukkit.event.player.PlayerItemDamageEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String itemConsumingByPlayer = e.getItem().getType().toString();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("ItemConsumeEvent PlayerConsuming= " + playerName);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("ItemConsumeEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("ItemConsumeEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("ItemConsumeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemConsumeEvent");
                }
                return;
            }

            if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("ItemConsumeEvent SneakingPlayer= " + sneakingPlayer);
                    debugUtils.addLine("ItemConsumeEvent SneakingConfig= " + sneaking);
                    debugUtils.addLine("ItemConsumeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemConsumeEvent");
                }
                return;
            }

            if(!itemConsume.equalsIgnoreCase("NOBODY") && itemConsume.equalsIgnoreCase(itemConsumingByPlayer)) {
                if (debugActive) {
                    debugUtils.addLine("ItemConsumeEvent ItemConsumeByPlayer= " + itemConsumingByPlayer);
                    debugUtils.addLine("ItemConsumeEvent itemConsumeConfig= " + itemConsume);
                    debugUtils.addLine("ItemConsumeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ItemConsumeEvent");
                }
                return;
            }

            Main.dailyChallenge.increment(playerName, point);

            if (debugActive) {
                debugUtils.addLine("ItemConsumeEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("ItemConsumeEvent");
            }
        });
    }
}
