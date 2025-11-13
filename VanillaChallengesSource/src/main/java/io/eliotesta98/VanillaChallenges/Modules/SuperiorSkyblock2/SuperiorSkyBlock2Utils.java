package io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

public class SuperiorSkyBlock2Utils {

    public static SuperiorPlayer getSuperiorPlayer(String playerName) {
        return SuperiorSkyblockAPI.getPlayer(playerName);
    }

    public static boolean isAfkInIsland(SuperiorPlayer superiorPlayer) {
        return superiorPlayer.isAFK();
    }

    public static boolean isInsideIsland(SuperiorPlayer superiorPlayer) {
        return superiorPlayer.isInsideIsland();
    }

}
