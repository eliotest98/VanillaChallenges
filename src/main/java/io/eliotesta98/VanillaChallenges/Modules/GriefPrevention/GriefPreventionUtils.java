package io.eliotesta98.VanillaChallenges.Modules.GriefPrevention;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

public class GriefPreventionUtils {

    public static String isAllowBreak(Player player, Block block, BlockBreakEvent event) {
        return GriefPrevention.instance.allowBreak(player, block, block.getLocation(), event);
    }

    public static String isAllowPlace(Player player, Location location) {
        return GriefPrevention.instance.allowBuild(player, location);
    }

    public static boolean isReasonOk(Player player, Block block, Event event) {
        String reason;
        if (event instanceof BlockBreakEvent) {
            reason = isAllowBreak(player, block, (BlockBreakEvent) event);
        } else {
            reason = isAllowPlace(player, block.getLocation());
        }
        return reason != null;
    }
}
