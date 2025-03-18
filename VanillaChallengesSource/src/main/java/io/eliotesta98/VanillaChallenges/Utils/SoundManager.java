package io.eliotesta98.VanillaChallenges.Utils;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;

import java.lang.reflect.Method;

public class SoundManager {
    @SuppressWarnings("CallToPrintStackTrace")
    public void playSound(final CommandSender commandSender, final String sound, final float n, final float n2) {
        Sound _sound = this.getSound(sound);
        if (_sound == null) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&cSomething went wrong while trying to play the sound '" + sound + "' are you sure it exists?"));
            return;
        }

        if (commandSender instanceof Player) {
            try {
                final Player player = (Player) commandSender;
                player.playSound(player.getLocation(), _sound, n, n2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void playSound(final Player player, final String sound, final float n, final float n2) {
        Sound _sound = this.getSound(sound);
        if (_sound == null) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&cSomething went wrong while trying to play the sound '" + sound + "' are you sure it exists?"));
            return;
        }

        try {
            player.playSound(player.getLocation(), _sound, n, n2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void playSound(final Location location, final String sound, final float n, final float n2) {
        Sound _sound = this.getSound(sound);
        if (_sound == null) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&cSomething went wrong while trying to play the sound '" + sound + "' are you sure it exists?"));
            return;
        }

        try {
            location.getWorld().playSound(location, _sound, n, n2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private Sound getSound(final String sound) {
        try {
            // Get the `valueOf` method from the `Sound` class with String as its parameter
            Method valueOfMethod = Sound.class.getMethod("valueOf", String.class);

            // Invoke `valueOf` on the `Sound` class, passing the string parameter
            return (Sound) valueOfMethod.invoke(null, sound);
        } catch (Exception e) {
            // Handle cases where the sound name is invalid or reflection fails
            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&cError: Unable to resolve sound '" + sound+"', if your server is running 1.21.3 or newer, make sure to convert the sound names to the new format, like 'minecraft:entity.ender_dragon.flap'."));
            e.printStackTrace();
            return null; // Or handle this as appropriate for your code
        }
    }
}
