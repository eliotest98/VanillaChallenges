package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ColorSheepEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ColorSheepEvent");
    private final String color = Main.dailyChallenge.getColor();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onColorSheep(org.bukkit.event.entity.SheepDyeWoolEvent e) {
        long tempo = System.currentTimeMillis();
        if (e.getPlayer() == null || e.getEntity().getColor() == null) {
            if (debugActive) {
                debugUtils.addLine("ColorSheepEvent Player= " + e.getPlayer() + " EntityColor= " + e.getEntity().getColor());
                debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("ColorSheepEvent");
            }
            return;
        }
        final String playerName = e.getPlayer().getName();
        final String colorPlayer = e.getColor().toString();
        final boolean playerSneaking = e.getPlayer().isSneaking();
        final String worldName = e.getPlayer().getWorld().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("ColorSheepEvent PlayerColoring= " + playerName);
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

            if(!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != playerSneaking) {
                if (debugActive) {
                    debugUtils.addLine("ColorSheepEvent ConfigSneaking= " + sneaking);
                    debugUtils.addLine("ColorSheepEvent PlayerSneaking= " + playerSneaking);
                    debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ColorSheepEvent");
                }
                return;
            }
            if(!color.equalsIgnoreCase("ALL") && !color.equalsIgnoreCase(colorPlayer)) {
                if (debugActive) {
                    debugUtils.addLine("ColorSheepEvent ConfigColor= " + color);
                    debugUtils.addLine("ColorSheepEvent PlayerColorg= " + colorPlayer);
                    debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ColorSheepEvent");
                }
                return;
            }
            Main.dailyChallenge.increment(playerName, point);
            if (debugActive) {
                debugUtils.addLine("ColorSheepEvent ConfigColor= " + color);
                debugUtils.addLine("ColorSheepEvent PlayerColorg= " + colorPlayer);
                debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("ColorSheepEvent");
            }
        });
    }
}
