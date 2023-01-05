package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.Tombs.Interfaces.TombsInterfaceHolder;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DyeEvent implements Listener {

    private final DebugUtils debugUtils = new DebugUtils();
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("DyeEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final String itemInHand = Main.dailyChallenge.getItemInHand();
    private final String item = Main.dailyChallenge.getItem();
    private final String cause = Main.dailyChallenge.getCause();
    private final String sneaking = Main.dailyChallenge.getSneaking();
    private final boolean keepInventory = Main.dailyChallenge.isKeepInventory();
    private final boolean deathInLand = Main.dailyChallenge.isDeathInLand();
    private boolean landsEnabled = Main.instance.getConfigGestion().getHooks().get("Lands");
    private final int numberOfSlots = Main.dailyChallenge.getNumber();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
        long tempo = System.currentTimeMillis();
        String playerName = e.getEntity().getName();
        String causePlayer = e.getEntity().getLastDamageCause().getCause().toString();
        Location playerLocation = e.getEntity().getLocation();
        boolean sneakingPlayer = e.getEntity().isSneaking();
        final PlayerInventory inventory = e.getEntity().getInventory();
        String itemInHandPlayer = e.getEntity().getInventory().getItemInMainHand().getType().toString();
        if (keepInventory) {
            e.setKeepInventory(true);
        }
        if (!item.equalsIgnoreCase("ALL")) {
            ItemStack itemStack = new ItemStack(Material.getMaterial(item));
            if (!inventory.contains(itemStack)) {
                if (debugActive) {
                    debugUtils.addLine("DyeEvent ItemsListPlayer= " + Arrays.toString(inventory.getContents()));
                    debugUtils.addLine("DyeEvent ItemConfig= " + item);
                    debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("DyeEvent");
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
                debugUtils.addLine("DyeEvent PlayerDye= " + playerName);
            }

            if (deathInLand && landsEnabled) {
                Land land = Main.landsIntegration.getLand(playerLocation);
                if (land != null) {
                    for (UUID p : land.getTrustedPlayers()) {// scorro la lista dei player membri
                        OfflinePlayer player = Bukkit.getOfflinePlayer(p);// prendo il player
                        if (player.getName().equalsIgnoreCase(playerName)) {// controllo il nome
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent Player is trusted at Land");
                            }
                        } else {
                            if (debugActive) {
                                debugUtils.addLine("BlockBreakEvent Player is not trusted at Land");
                                debugUtils.addLine("BlockBreakEvent execution time= " + (System.currentTimeMillis() - tempo));
                                debugUtils.debug("BlockBreakEvent");
                            }
                            return;
                        }
                    }
                }
            }

            if (!cause.equalsIgnoreCase("ALL") && !cause.equalsIgnoreCase(causePlayer)) {
                if (debugActive) {
                    debugUtils.addLine("DyeEvent CausePlayer= " + causePlayer);
                    debugUtils.addLine("DyeEvent CauseConfig= " + cause);
                    debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("DyeEvent");
                }
                return;
            }

            if (!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
                if (debugActive) {
                    debugUtils.addLine("DyeEvent SneakingPlayer= " + sneakingPlayer);
                    debugUtils.addLine("DyeEvent SneakingConfig= " + sneaking);
                    debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("DyeEvent");
                }
                return;
            }

            if (!itemInHand.equalsIgnoreCase("ALL") && !itemInHand.equalsIgnoreCase(itemInHandPlayer)) {
                if (debugActive) {
                    debugUtils.addLine("DyeEvent ItemInHandPlayer= " + itemInHandPlayer);
                    debugUtils.addLine("DyeEvent ItemInHandConfig= " + itemInHand);
                    debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("DyeEvent");
                }
                return;
            }

            if (numberOfSlots != -1 && inventorySize != numberOfSlots) {
                if (debugActive) {
                    debugUtils.addLine("DyeEvent NumberOfSlot= " + inventorySize);
                    debugUtils.addLine("DyeEvent NumberOfSlotConfig= " + numberOfSlots);
                    debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug("DyeEvent");
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
            debugUtils.addLine("DyeEvent AddedPoints= " + point);
            debugUtils.addLine("DyeEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("DyeEvent");
        }
    }
}
