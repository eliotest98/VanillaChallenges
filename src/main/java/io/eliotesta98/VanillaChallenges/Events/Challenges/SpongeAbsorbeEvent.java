package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.ArrayList;
import java.util.HashMap;

public class SpongeAbsorbeEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("SpongeAbsorbEvent");
    private final HashMap<String, String> players = new HashMap<>();
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final org.bukkit.event.block.BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType().toString().equalsIgnoreCase("SPONGE")) {
            if (players.get(e.getBlockPlaced().getLocation().toString()) == null) {
                players.put(e.getBlockPlaced().getLocation().toString(), e.getPlayer().getName());
            } else {
                players.replace(e.getBlockPlaced().getLocation().toString(), e.getPlayer().getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAbsorb(org.bukkit.event.block.SpongeAbsorbEvent e) {
        long tempo = System.currentTimeMillis();
        final Block spongeBlock = e.getBlock();
        final int amount = e.getBlocks().size();
        final String worldName = e.getBlock().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("SpongeAbsorbEvent PlayerAbsorbing= " + players.get(spongeBlock.getLocation().toString()));
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("ColorSheepEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("ColorSheepEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ColorSheepEvent");
                }
                return;
            }

            if (players.get(spongeBlock.getLocation().toString()) != null) {
                Main.dailyChallenge.increment(players.get(spongeBlock.getLocation().toString()), (long) amount * point);
                players.remove(players.get(spongeBlock.getLocation().toString()));
            }
            if (debugActive) {
                debugUtils.addLine("SpongeAbsorbEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("SpongeAbsorbEvent");
            }
        });
    }
}

