package io.eliotesta98.VanillaChallenges.Utils;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;

public class SoundManager {
    public void playSound(final CommandSender commandSender, final Sound sound, final float n, final float n2) {
        if (commandSender instanceof Player) {
            final Player player = (Player) commandSender;
            player.playSound(player.getLocation(), sound, n, n2);
        }
    }

    public void playSound(final Player player, final Sound sound, final float n, final float n2) {
        player.playSound(player.getLocation(), sound, n, n2);
    }

    public void playSound(final Location location, final Sound sound, final float n, final float n2) {
        location.getWorld().playSound(location, sound, n, n2);
    }
}
