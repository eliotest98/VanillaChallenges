package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class ColorSheepEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ColorSheepEvent");
    private final ArrayList<String> colors = Main.dailyChallenge.getColors();
    private final int point = Main.dailyChallenge.getPoint();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

    private final HashMap<Entity, String> sheepColored = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onColorSheep(org.bukkit.event.entity.SheepDyeWoolEvent e) {
        long tempo = System.currentTimeMillis();
        if(sheepColored.containsKey(e.getEntity())) {
            final String playerName = sheepColored.get(e.getEntity());
            final String colorPlayer = e.getColor().toString();
            final boolean playerSneaking = Bukkit.getPlayer(playerName).isSneaking();
            final String worldName = Bukkit.getPlayer(playerName).getWorld().getName();
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                if (debugActive) {
                    debugUtils.addLine("ColorSheepEvent PlayerColoring= " + playerName);
                }

                if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                    if (debugActive) {
                        debugUtils.addLine("ColorSheepEvent WorldsConfig= " + worldsEnabled);
                        debugUtils.addLine("ColorSheepEvent PlayerWorld= " + worldName);
                        debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("ColorSheepEvent");
                    }
                    return;
                }

                if (!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != playerSneaking) {
                    if (debugActive) {
                        debugUtils.addLine("ColorSheepEvent ConfigSneaking= " + sneaking);
                        debugUtils.addLine("ColorSheepEvent PlayerSneaking= " + playerSneaking);
                        debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("ColorSheepEvent");
                    }
                    return;
                }
                if (!colors.isEmpty() && !colors.contains(colorPlayer)) {
                    if (debugActive) {
                        debugUtils.addLine("ColorSheepEvent ConfigColor= " + colors);
                        debugUtils.addLine("ColorSheepEvent PlayerColorg= " + colorPlayer);
                        debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("ColorSheepEvent");
                    }
                    return;
                }
                Main.dailyChallenge.increment(playerName, point);
                if (debugActive) {
                    debugUtils.addLine("ColorSheepEvent ConfigColor= " + colors);
                    debugUtils.addLine("ColorSheepEvent PlayerColorg= " + colorPlayer);
                    debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ColorSheepEvent");
                }
            });
            sheepColored.remove(e.getEntity());
        } else {
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

                if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
                    if (debugActive) {
                        debugUtils.addLine("ColorSheepEvent WorldsConfig= " + worldsEnabled);
                        debugUtils.addLine("ColorSheepEvent PlayerWorld= " + worldName);
                        debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("ColorSheepEvent");
                    }
                    return;
                }

                if (!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != playerSneaking) {
                    if (debugActive) {
                        debugUtils.addLine("ColorSheepEvent ConfigSneaking= " + sneaking);
                        debugUtils.addLine("ColorSheepEvent PlayerSneaking= " + playerSneaking);
                        debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("ColorSheepEvent");
                    }
                    return;
                }
                if (!colors.isEmpty() && !colors.contains(colorPlayer)) {
                    if (debugActive) {
                        debugUtils.addLine("ColorSheepEvent ConfigColor= " + colors);
                        debugUtils.addLine("ColorSheepEvent PlayerColorg= " + colorPlayer);
                        debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug("ColorSheepEvent");
                    }
                    return;
                }
                Main.dailyChallenge.increment(playerName, point);
                if (debugActive) {
                    debugUtils.addLine("ColorSheepEvent ConfigColor= " + colors);
                    debugUtils.addLine("ColorSheepEvent PlayerColorg= " + colorPlayer);
                    debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("ColorSheepEvent");
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRightClickSheep(PlayerInteractEntityEvent playerInteractEvent) {
        if(Main.version113) {
            return;
        }
        if (playerInteractEvent.getRightClicked().toString().equalsIgnoreCase("CraftSheep")) {
            sheepColored.put(playerInteractEvent.getRightClicked(), playerInteractEvent.getPlayer().getName());
        }
    }
}
