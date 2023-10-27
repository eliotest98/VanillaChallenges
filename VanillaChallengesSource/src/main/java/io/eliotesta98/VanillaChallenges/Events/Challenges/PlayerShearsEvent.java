package io.eliotesta98.VanillaChallenges.Events.Challenges;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class PlayerShearsEvent implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShear(PlayerShearEntityEvent event) {

    }

}
