package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.Lands.LandsUtils;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;

public class DyeEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("DyeEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> items = Main.dailyChallenge.getItems();
    private final ArrayList<String> causes = Main.dailyChallenge.getCauses();
    private final boolean keepInventory = Main.dailyChallenge.isKeepInventory();
    private final boolean deathInLand = Main.dailyChallenge.isDeathInLand();
    private final boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final int numberOfSlots = Main.dailyChallenge.getNumber();
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        String playerName = e.getEntity().getName();
        String worldName = e.getEntity().getWorld().getName();
        String causePlayer = e.getEntity().getLastDamageCause().getCause().toString();
        Location playerLocation = e.getEntity().getLocation();
        boolean sneakingPlayer = e.getEntity().isSneaking();
        final PlayerInventory inventory = e.getEntity().getInventory();
        String itemInHandPlayer;
        if (Main.version113) {
            itemInHandPlayer = e.getEntity().getInventory().getItemInMainHand().getType().toString();
        } else {
            itemInHandPlayer = e.getEntity().getInventory().getItemInHand().getType().toString();
        }
        if (keepInventory) {
            e.setKeepInventory(true);
        }
        if (!items.isEmpty()) {
            boolean itemok = false;
            for (String itemName : items) {
                ItemStack itemStack = new ItemStack(Material.getMaterial(itemName));
                if (inventory.contains(itemStack)) {
                    itemok = true;
                    break;
                }
            }
            if (!itemok) {
                if (debugActive) {
                    debugUtils.addLine("ItemsListPlayer= " + Arrays.toString(inventory.getContents()));
                    debugUtils.addLine("ItemConfig= " + items);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
        }
        int sizeInventory = 0;
        if (numberOfSlots != -1) {
            for (int i = 0; i < inventory.getStorageContents().length; i++) {
                if (inventory.getItem(i) != null) {
                    sizeInventory++;
                }
            }
        }
        final int inventorySize = sizeInventory;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("PlayerDye= " + playerName);
            }

            if (deathInLand && landsEnabled) {
                if (LandsUtils.isTrusted(playerLocation, playerName)) {
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

            if (superiorSkyBlock2Enabled) {
                if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(playerName))) {
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

            if (!Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
                return;
            }

            if (!causes.isEmpty() && !causes.contains(causePlayer)) {
                if (debugActive) {
                    debugUtils.addLine("CausePlayer= " + causePlayer);
                    debugUtils.addLine("CauseConfig= " + causes);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            if (!Controls.isSneaking(sneakingPlayer, debugActive, debugUtils, tempo)) {
                return;
            }

            if (!Controls.isItemInHand(itemInHandPlayer, debugActive, debugUtils, tempo)) {
                return;
            }

            if (numberOfSlots != -1 && inventorySize != numberOfSlots) {
                if (debugActive) {
                    debugUtils.addLine("NumberOfSlot= " + inventorySize);
                    debugUtils.addLine("NumberOfSlotConfig= " + numberOfSlots);
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }

            Main.dailyChallenge.increment(playerName, point);
        });

        /*
        System.out.println(e.getEntity().getSaturation());
        System.out.println(e.getEntity().isSprinting());
        System.out.println(e.getEntity().isInWater());
        System.out.println(e.getEntity().isSwimming());
        System.out.println(e.getEntity().isRiptiding());
        System.out.println(e.getEntity().isFlying());
        System.out.println(e.getEntity().getFoodLevel());
        System.out.println(e.getEntity().getHealth());*/
        //e.getDroppedExp()

        if (debugActive) {
            debugUtils.addLine("AddedPoints= " + point);
            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug();
        }
    }
}
