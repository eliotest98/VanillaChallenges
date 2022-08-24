package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DyeEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("DyeEvent");
    private int point = Main.dailyChallenge.getPoint();
    private String cause = Main.dailyChallenge.getCause();
    private String sneaking = Main.dailyChallenge.getSneaking();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
        long tempo = System.currentTimeMillis();
        String playerName = e.getEntity().getName();
        String causePlayer = e.getEntity().getLastDamageCause().getCause().toString();
        boolean sneakingPlayer = e.getEntity().isSneaking();

        if (debugActive) {
            debugUtils.addLine("DyeEvent PlayerDye= " + playerName);
        }

        if(!cause.equalsIgnoreCase("ALL") && !cause.equalsIgnoreCase(causePlayer)) {
            if (debugActive) {
                debugUtils.addLine("DyeEvent CausePlayer= " + causePlayer);
                debugUtils.addLine("DyeEvent CauseConfig= " + cause);
                debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("DyeEvent");
            }
            return;
        }
        if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
            if (debugActive) {
                debugUtils.addLine("DyeEvent SneakingPlayer= " + sneakingPlayer);
                debugUtils.addLine("DyeEvent SneakingConfig= " + sneaking);
                debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("DyeEvent");
            }
            return;
        }
        Main.dailyChallenge.increment(playerName, point);

        /*System.out.println(e.getEntity().getLastDamageCause().getDamage());
        System.out.println(e.getEntity().getSaturation());
        System.out.println(e.getEntity().isSprinting());
        System.out.println(e.getEntity().isInWater());
        System.out.println(e.getEntity().isSwimming());
        System.out.println(e.getEntity().isRiptiding());
        System.out.println(e.getEntity().isFlying());
        System.out.println(e.getEntity().getFoodLevel());
        System.out.println(e.getEntity().getHealth());*/

        if (debugActive) {
            debugUtils.addLine("DyeEvent AddedPoints= " + point);
            debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("DyeEvent");
        }
        return;
    }
}
