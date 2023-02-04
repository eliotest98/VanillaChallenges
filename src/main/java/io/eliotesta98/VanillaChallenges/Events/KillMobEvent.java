package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class KillMobEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("KillEvent");
    private final String mobKill = Main.dailyChallenge.getMob();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKillEvent(org.bukkit.event.entity.EntityDeathEvent e) {
        long tempo = System.currentTimeMillis();
        String pName;
        boolean sneakingPlayer;
        String world = "";
        if (e.getEntity().getKiller() != null) {
            pName = e.getEntity().getKiller().getName();
            sneakingPlayer = e.getEntity().getKiller().isSneaking();
            world = e.getEntity().getKiller().getWorld().getName();
        } else {
            if (debugActive) {
                debugUtils.addLine("KillEvent PlayerKilling= null");
                debugUtils.addLine("KillEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("KillEvent");
            }
            return;
        }
        final String playerName = pName;
        final String mobKilled = e.getEntity().getName();
        boolean finalSneakingPlayer = sneakingPlayer;
        final String worldName = world;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("KillEvent PlayerKilling= " + playerName);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("KillEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("KillEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("KillEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("KillEvent");
                }
                return;
            }

            if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != finalSneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("KillEvent SneakingPlayer= " + finalSneakingPlayer);
                    debugUtils.addLine("KillEvent SneakingConfig= " + sneaking);
                    debugUtils.addLine("KillEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("KillEvent");
                }
                return;
            }

            if(!mobKill.equalsIgnoreCase("ALL") && !mobKill.equalsIgnoreCase(mobKilled)) {
                if (debugActive) {
                    debugUtils.addLine("KillEvent MobKilled= " + mobKilled);
                    debugUtils.addLine("KillEvent MobKilledConfig= " + mobKill);
                    debugUtils.addLine("KillEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("KillEvent");
                }
                return;
            }
            Main.dailyChallenge.increment(playerName, point);
            if (debugActive) {
                debugUtils.addLine("KillEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("KillEvent");
            }
        });
    }
}
