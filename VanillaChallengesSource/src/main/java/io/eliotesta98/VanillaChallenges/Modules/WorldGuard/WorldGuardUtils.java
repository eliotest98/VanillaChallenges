package io.eliotesta98.VanillaChallenges.Modules.WorldGuard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

public class WorldGuardUtils {

    public static boolean isARagion(World world, Location location) {
        // prendo il container della regione
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        // prendo le regions del mondo
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions != null) {
            // controllo se c'è quella region
            for (Map.Entry<String, ProtectedRegion> region : regions.getRegions().entrySet()) {
                if(region.getValue().contains(BlockVector3.at(location.getBlockX(),location.getBlockY(),location.getBlockZ()))) {
                    return true;
                }
            }
        }
        return false;
    }

}
