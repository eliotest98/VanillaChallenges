package io.eliotesta98.VanillaChallenges.Events.Challenges.Modules;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;

import java.util.ArrayList;

public class Controls {

    private static final ArrayList<String> worldsEnabled = Main.instance.getDailyChallenge().getWorlds();

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
}
