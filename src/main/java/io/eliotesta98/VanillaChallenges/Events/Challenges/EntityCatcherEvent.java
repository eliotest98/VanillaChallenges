package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.GriefPrevention.GriefPreventionUtils;
import io.eliotesta98.VanillaChallenges.Modules.Lands.LandsUtils;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Modules.WorldGuard.WorldGuardUtils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EntityCatcherEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("EntityCombustByEntityEvent");
    private final ArrayList<String> itemsInHand = Main.instance.getDailyChallenge().getItemsInHand();
    private final ArrayList<String> mobs = Main.instance.getDailyChallenge().getMobs();
    private final int point = Main.dailyChallenge.getPoint();
    private final boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final boolean worldGuardEnabled = Main.instance.getConfigGestion().getHooks().get("WorldGuard");
    private final boolean griefPreventionEnabled = Main.instance.getConfigGestion().getHooks().get("GriefPrevention");
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");
    private boolean ok = false;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityBurn(EntityCombustByEntityEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();

        if (!(e.getEntity() instanceof Player)) {
            if (debugActive) {
                debugUtils.addLine("This Entity is not a Player");
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return;
        }

        final Player player = (Player) e.getEntity();
        final ItemStack itemInMainHand;
        if (Main.version113) {
            itemInMainHand = player.getInventory().getItemInMainHand();
        } else {
            itemInMainHand = player.getInventory().getItemInHand();
        }

        final boolean sneakingPlayer = player.isSneaking();
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final Block block = location.getBlock();
        final String mobCaught = e.getCombuster().getName();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerBreaking= " + player.getName());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                if (griefPreventionEnabled) {
                    ok = GriefPreventionUtils.isReasonOk(player, block, e);
                }
            });
            if (ok) {
                ok = false;
                if (debugActive) {
                    debugUtils.addLine("Player is not trusted at Claim");
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (landsEnabled) {
                if (LandsUtils.isTrusted(location, player.getName())) {
                    if (debugActive) {
                        debugUtils.addLine("Player is trusted at Land");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("Player is not trusted at Land");
                        debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if (worldGuardEnabled) {
                if (WorldGuardUtils.isARagion(world, location)) {
                    if (debugActive) {
                        debugUtils.addLine("Player is in a region");
                        debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(player.getName()))) {
                    if (debugActive) {
                        debugUtils.addLine("Player is inside his own island");
                    }
                } else {
                    if (debugActive) {
                        debugUtils.addLine("Player isn't inside his own island");
                        debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                        debugUtils.debug();
                    }
                    return;
                }
            }

            if (!Controls.isWorldEnable(world.getName(), debugActive, debugUtils, tempo)) {
                return;
            }

            if(!mobs.isEmpty() && !mobs.contains(mobCaught)) {
                if (debugActive) {
                    debugUtils.addLine("MobCaught= " + mobCaught);
                    debugUtils.addLine("MobCaughtConfig= " + mobs);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (!Controls.isSneaking(sneakingPlayer, debugActive, debugUtils, tempo)) {
                return;
            }

            if (!itemsInHand.isEmpty() && !itemsInHand.contains(itemInMainHand.getType().toString())) {
                if (debugActive) {
                    debugUtils.addLine("ItemInHandConfig= " + itemsInHand);
                    debugUtils.addLine("ItemInHandPlayer= " + itemInMainHand.getType());
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            Main.instance.getDailyChallenge().increment(player.getName(), point);
            if (debugActive) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
        });
    }
}
