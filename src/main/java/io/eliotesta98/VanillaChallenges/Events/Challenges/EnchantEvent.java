package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class EnchantEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("EnchantItemEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEnchant(org.bukkit.event.enchantment.EnchantItemEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getEnchanter().getName();
        final int numberOfEnchants = e.getEnchantsToAdd().size();
        final String worldName = e.getEnchanter().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("EnchantItemEvent PlayerEnchanting= " + playerName);
            }

            if(!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                if (debugActive) {
                    debugUtils.addLine("EnchantItemEvent WorldsConfig= " + worldsEnabled);
                    debugUtils.addLine("EnchantItemEvent PlayerWorld= " + worldName);
                    debugUtils.addLine("EnchantItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("EnchantItemEvent");
                }
                return;
            }

            Main.dailyChallenge.increment(playerName, (long) point * numberOfEnchants);
            if (debugActive) {
                debugUtils.addLine("EnchantItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("EnchantItemEvent");
            }
        });
    }
}
