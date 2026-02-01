package io.eliotesta98.VanillaChallenges.Events.Challenges.Modules;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Controls {

    private static List<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private static List<String> itemsInHand = Main.instance.getDailyChallenge().getItemsInHand();
    private static List<String> items = Main.instance.getDailyChallenge().getItems();
    private static List<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private static List<String> blocksOnPlaced = Main.instance.getDailyChallenge().getBlocksOnPlace();
    private static List<String> mobs = Main.instance.getDailyChallenge().getMobs();
    private static List<String> causes = Main.instance.getDailyChallenge().getCauses();
    private static List<String> colors = Main.instance.getDailyChallenge().getColors();
    private static List<String> vehicles = Main.instance.getDailyChallenge().getVehicle();
    private static String sneaking = Main.instance.getDailyChallenge().getSneaking();
    private static String onGround = Main.instance.getDailyChallenge().getOnGround();
    private static double force = Main.instance.getDailyChallenge().getForce();
    private static double power = Main.instance.getDailyChallenge().getPower();
    private static String permission = Main.instance.getConfigGestion().getPermissionPointsGive();

    static {
        reload();
    }

    public static void reload() {
        worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
        itemsInHand = Main.instance.getDailyChallenge().getItemsInHand();
        items = Main.instance.getDailyChallenge().getItems();
        blocks = Main.instance.getDailyChallenge().getBlocks();
        blocksOnPlaced = Main.instance.getDailyChallenge().getBlocksOnPlace();
        mobs = Main.instance.getDailyChallenge().getMobs();
        causes = Main.instance.getDailyChallenge().getCauses();
        colors = Main.instance.getDailyChallenge().getColors();
        vehicles = Main.instance.getDailyChallenge().getVehicle();
        sneaking = Main.instance.getDailyChallenge().getSneaking();
        onGround = Main.instance.getDailyChallenge().getOnGround();
        force = Main.instance.getDailyChallenge().getForce();
        power = Main.instance.getDailyChallenge().getPower();
        permission = Main.instance.getConfigGestion().getPermissionPointsGive();
    }

    // When true all checks in challenges return, in this way the points are not given.
    // Ex. if (Controls.isWorldEnable(worldName, debugActive, debugUtils, tempo)) {
    //          return;
    //     }
    public static boolean isWorldEnable(String worldName, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
            if (debugActive) {
                debugUtils.addLine("WorldsConfig= " + worldsEnabled);
                debugUtils.addLine("PlayerWorld= " + worldName);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSneaking(boolean sneakingPlayer, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!sneaking.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(sneaking) != sneakingPlayer) {
            if (debugActive) {
                debugUtils.addLine("ConfigSneaking= " + sneaking);
                debugUtils.addLine("PlayerSneaking= " + sneakingPlayer);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isItemInHand(String itemInMainHand, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!itemsInHand.isEmpty() && !itemsInHand.contains(itemInMainHand)) {
            if (debugActive) {
                debugUtils.addLine("ItemInHandConfig= " + itemsInHand);
                debugUtils.addLine("ItemInHandPlayer= " + itemInMainHand);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean isItemInInventory(Inventory playerInventory, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!items.isEmpty()) {
            for (String item : items) {
                ItemStack itemStack;
                if (item.contains(":")) {
                    String[] itemSplit = item.split(":");
                    Material material = Material.getMaterial(itemSplit[0]);
                    if (material == null) {
                        return true;
                    }
                    itemStack = new ItemStack(material, 1, Short.parseShort(itemSplit[1]));
                } else {
                    Material material = Material.getMaterial(item);
                    if (material == null) {
                        return true;
                    }
                    itemStack = new ItemStack(material);
                }
                if (playerInventory.contains(itemStack)) {
                    return false;
                }
            }
        }
        if (debugActive) {
            debugUtils.addLine("ItemsConfig= " + items);
            debugUtils.addLine("PlayerInventory= " + playerInventory);
            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug();
        }
        return false;
    }

    public static boolean isBlock(String block, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!blocks.isEmpty() && !blocks.contains(block)) {
            if (debugActive) {
                debugUtils.addLine("BlockConfig= " + blocks);
                debugUtils.addLine("Block= " + block);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isBlockOnPlaced(String block, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!blocksOnPlaced.isEmpty() && !blocksOnPlaced.contains(block)) {
            if (debugActive) {
                debugUtils.addLine("BlockOnPlacedConfig= " + blocksOnPlaced);
                debugUtils.addLine("BlockOnPlaced= " + block);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean isMob(String mob, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!mobs.isEmpty() && !mobs.contains(mob)) {
            if (debugActive) {
                debugUtils.addLine("MobBreedConfig= " + mobs);
                debugUtils.addLine("MobBreded= " + mob);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCause(String cause, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!causes.isEmpty() && !causes.contains(cause)) {
            if (debugActive) {
                debugUtils.addLine("CausesConfig= " + causes);
                debugUtils.addLine("Cause= " + cause);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isColor(String color, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!colors.isEmpty() && !colors.contains(color)) {
            if (debugActive) {
                debugUtils.addLine("ColorsConfig= " + causes);
                debugUtils.addLine("Color= " + color);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isItem(String item, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!items.isEmpty() && !items.contains(item)) {
            if (debugActive) {
                debugUtils.addLine("ItemPlayer= " + item);
                debugUtils.addLine("ItemsConfig= " + items);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isVehicle(String vehicle, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!vehicles.isEmpty() && !vehicles.contains(vehicle)) {
            if (debugActive) {
                debugUtils.addLine("VehiclePlayer= " + vehicle);
                debugUtils.addLine("VehicleConfig= " + vehicles);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean isOnGround(boolean playerOnGround, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!onGround.equalsIgnoreCase("NOBODY") && Boolean.parseBoolean(onGround) != playerOnGround) {
            if (debugActive) {
                debugUtils.addLine("OnGroundConfig= " + onGround);
                debugUtils.addLine("OnGroundPlayer= " + playerOnGround);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean isForce(double playerForce, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (force != 0.0 && playerForce < force) {
            if (debugActive) {
                debugUtils.addLine("ForcePlayer= " + playerForce);
                debugUtils.addLine("ForceConfig= " + force);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPower(double playerPower, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (power != 0.0 && playerPower < power) {
            if (debugActive) {
                debugUtils.addLine("PowerPlayer= " + power);
                debugUtils.addLine("PowerConfig= " + force);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasPermission(String playerName) {
        if (permission.equalsIgnoreCase("")) {
            return true;
        } else {
            return Bukkit.getPlayer(playerName).hasPermission(permission);
        }
    }
}
