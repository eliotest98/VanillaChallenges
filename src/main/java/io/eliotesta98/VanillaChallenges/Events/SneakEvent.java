package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SneakEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("SneakEvent");
    private String block = Main.dailyChallenge.getBlock();
    private String item = Main.dailyChallenge.getItem();
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSneak(org.bukkit.event.player.PlayerToggleSneakEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String blockWalk = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType().toString();
        final String itemInHand = e.getPlayer().getItemInHand().getType().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("SneakEvent PlayerSkeaking= " + playerName);
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

                if(!block.equalsIgnoreCase("ALL") && !block.equalsIgnoreCase(blockWalk)) {
                    if (debugActive) {
                        debugUtils.addLine("SneakEvent BlockStepOnPlayer= " + blockWalk);
                        debugUtils.addLine("SneakEvent BlockStepOnConfig= " + block);
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
            }
        });
    }
}

