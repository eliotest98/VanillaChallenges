package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class SneakEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("SneakEvent");
    private final ArrayList<String> blocks = Main.dailyChallenge.getBlocks();
    private final String item = Main.dailyChallenge.getItem();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSneak(org.bukkit.event.player.PlayerToggleSneakEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String blockWalk = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType().toString();
        final String itemInHand = e.getPlayer().getItemInHand().getType().toString();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("SneakEvent PlayerSkeaking= " + playerName);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("SneakEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("SneakEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("SneakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("SneakEvent");
                }
                return;
            }

            if(!item.equalsIgnoreCase("ALL") && !item.equalsIgnoreCase(itemInHand)) {
                if (debugActive) {
                    debugUtils.addLine("SneakEvent ItemInHandConfig= " + item);
                    debugUtils.addLine("SneakEvent ItemInHandPlayer= " + itemInHand);
                    debugUtils.addLine("SneakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("SneakEvent");
                }
                return;
            }

            if(!blocks.isEmpty() && !blocks.contains(blockWalk)) {
                if (debugActive) {
                    debugUtils.addLine("SneakEvent BlockStepOnPlayer= " + blockWalk);
                    debugUtils.addLine("SneakEvent BlockStepOnConfig= " + blocks);
                    debugUtils.addLine("SneakEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("SneakEvent");
                }
                return;
            }

            Main.dailyChallenge.increment(playerName, point);

            if (debugActive) {
                debugUtils.addLine("SneakEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("SneakEvent");
            }
        });
    }
}

