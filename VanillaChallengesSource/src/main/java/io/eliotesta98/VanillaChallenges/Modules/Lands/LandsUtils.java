package io.eliotesta98.VanillaChallenges.Modules.Lands;

import io.eliotesta98.VanillaChallenges.Core.Main;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class LandsUtils {

    public static LandsIntegration landsIntegration;

    public static void setLandsIntegration() {
        LandsIntegration.of(Main.instance);
    }

    public static Land getLandFromLocation(Location location) {
        if (location == null) {
            return null;
        }
        return landsIntegration.getArea(location).getLand();
    }

    public static boolean isTrusted(Location playerLocation, String playerName) {
        Land land = getLandFromLocation(playerLocation);
        if (land != null) {
            for (UUID p : land.getTrustedPlayers()) {// scorro la lista dei player membri
                OfflinePlayer player = Bukkit.getOfflinePlayer(p);// prendo il player
                if (player.getName().equalsIgnoreCase(playerName)) {// controllo il nome
                    return true;
                }
            }
        }
        return false;
    }
}
