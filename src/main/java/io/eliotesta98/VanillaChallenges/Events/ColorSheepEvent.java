package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ColorSheepEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ColorSheepEvent");
    private String color = Main.dailyChallenge.getColor();
    private int point = Main.dailyChallenge.getPoint();
    private String sneaking = Main.dailyChallenge.getSneaking();

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
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("ColorSheepEvent PlayerColoring= " + playerName);
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
                return;
            }
        });
    }
}
