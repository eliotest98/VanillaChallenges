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
                    debugUtils.addLine("BlockBreakEvent PlayerColoring= " + playerName);
                    debugUtils.addLine("BlockBreakEvent PlayerSneaking= " + playerSneaking);
                    debugUtils.addLine("BlockBreakEvent ConfigSneaking= " + sneaking);
                }
                if (sneaking.equalsIgnoreCase("NOBODY")) {
                    if (color.equalsIgnoreCase("ALL")) {
                        if (debugActive) {
                            debugUtils.addLine("ColorSheepEvent Conditions= 0");
                        }
                        Main.dailyChallenge.increment(playerName, point);
                        if (debugActive) {
                            debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug("ColorSheepEvent");
                        }
                        return;
                    } else {
                        if (debugActive) {
                            debugUtils.addLine("ColorSheepEvent ColorByPlayer= " + colorPlayer);
                            debugUtils.addLine("ColorSheepEvent ColorConfig= " + color);
                        }
                        if (colorPlayer.equalsIgnoreCase(color)) {
                            if (debugActive) {
                                debugUtils.addLine("ColorSheepEvent Conditions= 1");
                            }
                            Main.dailyChallenge.increment(playerName, point);
                            if (debugActive) {
                                debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("ColorSheepEvent");
                            }
                            return;
                        }
                    }
                } else {
                    if (Boolean.parseBoolean(sneaking) == playerSneaking) {
                        if (color.equalsIgnoreCase("ALL")) {
                            if (debugActive) {
                                debugUtils.addLine("ColorSheepEvent Conditions= 0");
                            }
                            Main.dailyChallenge.increment(playerName, point);
                            if (debugActive) {
                                debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("ColorSheepEvent");
                            }
                            return;
                        } else {
                            if (debugActive) {
                                debugUtils.addLine("ColorSheepEvent ColorByPlayer= " + colorPlayer);
                                debugUtils.addLine("ColorSheepEvent ColorConfig= " + color);
                            }
                            if (colorPlayer.equalsIgnoreCase(color)) {
                                if (debugActive) {
                                    debugUtils.addLine("ColorSheepEvent Conditions= 1");
                                }
                                Main.dailyChallenge.increment(playerName, point);
                                if (debugActive) {
                                    debugUtils.addLine("ColorSheepEvent execution time= " + (System.currentTimeMillis() - tempo));
                                    debugUtils.debug("ColorSheepEvent");
                                }
                                return;
                            }
                        }
                    }
                }
            }
        });
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
    }
}
