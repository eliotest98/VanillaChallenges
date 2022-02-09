package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.ArrayList;

public class SpongeAbsorbeEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("SpongeAbsorbEvent");
    private ArrayList<String> players = new ArrayList<String>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        if(e.getBlockPlaced().getType().toString().equalsIgnoreCase("SPONGE")) {
            players.add(e.getPlayer().getName());
        } else {
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSneak(org.bukkit.event.block.SpongeAbsorbEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                while(!players.isEmpty()) {
                    Main.dailyChallenge.increment(players.get(0),e.getBlocks().size());
                    players.remove(0);
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("SpongeAbsorbEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("SpongeAbsorbEvent");
        }
        return;
    }
}

