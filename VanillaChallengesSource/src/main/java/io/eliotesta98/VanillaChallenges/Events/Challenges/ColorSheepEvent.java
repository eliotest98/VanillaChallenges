package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;

public class ColorSheepEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ColorSheepEvent");
    private final int point = Main.instance.getDailyChallenge().getPoint();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    private final HashMap<Entity, String> sheepColored = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onColorSheep(org.bukkit.event.entity.SheepDyeWoolEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        if (sheepColored.containsKey(e.getEntity())) {
            final String playerName = sheepColored.get(e.getEntity());
            final String colorPlayer = e.getColor().toString();
            final boolean playerSneaking = Bukkit.getPlayer(playerName).isSneaking();
            final String worldName = Bukkit.getPlayer(playerName).getWorld().getName();
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                if (debugActive) {
                    debugUtils.addLine("PlayerColoring= " + playerName);
                }

                if (superiorSkyBlock2Enabled) {
                    if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(playerName))) {
                        if (debugActive) {
                            debugUtils.addLine("BlockBreakEvent Player is inside his own island");
                        }
                    } else {
                        if (debugActive) {
                            debugUtils.addLine("BlockBreakEvent Player isn't inside his own island");
                            debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                            debugUtils.debug();
                        }
                        return;
                    }
                }

                if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                    return;
                }

                if (Controls.isSneaking(playerSneaking, debugActive, debugUtils, tempo)) {
                    return;
                }

                if (Controls.isColor(colorPlayer, debugActive, debugUtils, tempo)) {
                    return;
                }
                Main.instance.getDailyChallenge().increment(playerName, point);
                if (debugActive) {
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
            });
            sheepColored.remove(e.getEntity());
        } else {
            if (e.getPlayer() == null || e.getEntity().getColor() == null) {
                if (debugActive) {
                    debugUtils.addLine("Player= " + e.getPlayer() + " EntityColor= " + e.getEntity().getColor());
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
            final String playerName = e.getPlayer().getName();
            final String colorPlayer = e.getColor().toString();
            final boolean playerSneaking = e.getPlayer().isSneaking();
            final String worldName = e.getPlayer().getWorld().getName();
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                if (debugActive) {
                    debugUtils.addLine("PlayerColoring= " + playerName);
                }

                if (!Controls.hasPermission(playerName)) {
                    return;
                }

                if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                    return;
                }

                if (Controls.isSneaking(playerSneaking, debugActive, debugUtils, tempo)) {
                    return;
                }

                if (Controls.isColor(colorPlayer, debugActive, debugUtils, tempo)) {
                    return;
                }

                Main.instance.getDailyChallenge().increment(playerName, point);
                if (debugActive) {
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRightClickSheep(PlayerInteractEntityEvent playerInteractEvent) {
        if (!Main.version.isInRange(8, 12)) {
            return;
        }
        if (playerInteractEvent.getRightClicked().toString().equalsIgnoreCase("CraftSheep")) {
            sheepColored.put(playerInteractEvent.getRightClicked(), playerInteractEvent.getPlayer().getName());
        }
    }
}
