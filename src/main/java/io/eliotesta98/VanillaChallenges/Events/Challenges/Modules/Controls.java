package io.eliotesta98.VanillaChallenges.Events.Challenges.Modules;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;

import java.util.ArrayList;

public class Controls {

    private static final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();
    private static final String sneaking = Main.dailyChallenge.getSneaking();
    private static final ArrayList<String> itemsInHand = Main.instance.getDailyChallenge().getItemsInHand();
    private static final ArrayList<String> blocks = Main.instance.getDailyChallenge().getBlocks();
    private static final ArrayList<String> blocksOnPlaced = Main.instance.getDailyChallenge().getBlocksOnPlace();
    private static final ArrayList<String> mobs = Main.dailyChallenge.getMobs();

    public static boolean isWorldEnable(String worldName, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!worldsEnabled.isEmpty() && !worldsEnabled.contains(worldName)) {
            if (debugActive) {
                debugUtils.addLine("WorldsConfig= " + worldsEnabled);
                debugUtils.addLine("PlayerWorld= " + worldName);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return false;
        } else {
            return true;
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
            return false;
        } else {
            return true;
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
            return false;
        } else {
            return true;
        }
    }

    public static boolean isBlock(String block, boolean debugActive, DebugUtils debugUtils, long tempo) {
        if (!blocks.isEmpty() && !blocks.contains(block)) {
            if (debugActive) {
                debugUtils.addLine("BlockConfig= " + blocks);
                debugUtils.addLine("Block= " + block);
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return false;
        } else {
            return true;
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
            return false;
        } else {
            return true;
        }
    }
}
