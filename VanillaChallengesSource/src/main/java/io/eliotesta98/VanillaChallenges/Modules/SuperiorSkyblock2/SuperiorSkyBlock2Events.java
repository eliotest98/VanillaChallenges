package io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2;

import com.bgsoftware.superiorskyblock.api.events.MissionCompleteEvent;
import io.eliotesta98.VanillaChallenges.Core.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SuperiorSkyBlock2Events implements Listener {

    private final int point = Main.dailyChallenge.getPoint();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMissionComplete(MissionCompleteEvent event) {
        Main.instance.getDailyChallenge().increment(event.getPlayer().getName(), point);
    }

}
