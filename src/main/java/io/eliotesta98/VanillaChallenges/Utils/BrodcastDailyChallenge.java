package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Challenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class BrodcastDailyChallenge {

    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private BukkitTask task;
    private String brodcastMessageTitle = Main.dailyChallenge.getTitle();
    private String brodcastMessageSubTitle = Main.dailyChallenge.getSubTitle();
    private final String topPlayers = Main.instance.getConfigGestion().getMessages().get("topPlayers");
    private final String actuallyInTop = Main.instance.getConfigGestion().getMessages().get("actuallyInTop");

    public void start(long time) {
        execute(time);
    }

    public void stop() {
        task.cancel();
    }


    public void execute(long time) {
        task = scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if(!p.hasPermission("vc.broadcast.timer")) {
                        return;
                    }
                    p.sendMessage("");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', brodcastMessageTitle));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', brodcastMessageSubTitle));
                    p.sendMessage("");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',actuallyInTop));
                    ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers();
                    int i = 1;
                    while (!top.isEmpty()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', topPlayers.replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + top.get(0).getPoints())));
                        top.remove(0);
                        i++;
                    }
                    p.sendMessage("");
                }
            }
        }, 0, time);
    }

}
