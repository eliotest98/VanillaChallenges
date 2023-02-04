package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class EggThrowEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("EggThrowEvent");
    private final String mob = Main.dailyChallenge.getMob();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEvent(org.bukkit.event.player.PlayerEggThrowEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getPlayer().getName();
        final String worldName = e.getPlayer().getWorld().getName();
        final boolean hatching = e.isHatching();
        final byte numberHatches = e.getNumHatches();
        final boolean sneakingPlayer = e.getPlayer().isSneaking();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("EggThrowEvent PlayerThrowing= " + playerName);
                debugUtils.addLine("EggThrowEvent MobConfig= " + mob);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("EggThrowEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("EggThrowEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("EggThrowEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("EggThrowEvent");
                }
                return;
            }

            if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("EggThrowEvent ConfigSneaking= " + sneaking);
                    debugUtils.addLine("EggThrowEvent PlayerSneaking= " + sneakingPlayer);
                    debugUtils.addLine("EggThrowEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("EggThrowEvent");
                }
                return;
            }
            if(mob.equalsIgnoreCase("CHICKEN")) {
                if (hatching) {
                    Main.dailyChallenge.increment(playerName, (long) point * numberHatches);
                } else {
                    Main.dailyChallenge.increment(playerName, (long) point);
                }
            } else {
                Main.dailyChallenge.increment(playerName, point);
            }
            if (debugActive) {
                debugUtils.addLine("EggThrowEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("EggThrowEvent");
            }
            return;
        });
    }
}
