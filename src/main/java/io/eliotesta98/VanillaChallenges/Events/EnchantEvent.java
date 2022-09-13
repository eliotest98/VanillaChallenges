package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EnchantEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("EnchantItemEvent");
    private int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEnchant(org.bukkit.event.enchantment.EnchantItemEvent e) {
        long tempo = System.currentTimeMillis();
        final String playerName = e.getEnchanter().getName();
        final int numberOfEnchants = e.getEnchantsToAdd().size();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("EnchantItemEvent PlayerEnchanting= " + playerName);
                }
                Main.dailyChallenge.increment(playerName, (long) point * numberOfEnchants);
                if (debugActive) {
                    debugUtils.addLine("EnchantItemEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("EnchantItemEvent");
                }
                return;
            }
        });
    }
}
