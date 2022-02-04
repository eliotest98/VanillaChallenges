package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DailyGiveWinners implements Listener {

    private ArrayList<DailyWinner> winners = new ArrayList<DailyWinner>();
    private DebugUtils debugUtils = new DebugUtils();
    private boolean debug = Main.instance.getConfigGestion().getDebug().get("DailyGiveRewardEvent");
    private String challengeReward = Main.instance.getConfigGestion().getMessages().get("challengeReward");

    public DailyGiveWinners() {
        winners = H2Database.instance.getAllDailyWinners();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDailyGiveRewards(PlayerJoinEvent e) {
        long tempo = System.currentTimeMillis();
        if (winners.isEmpty()) {
            if (debug) {
                debugUtils.addLine("DailyGiveRewardEvent execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug("DailyGiveRewardEvent");
            }
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < winners.size(); i++) {
                    if (winners.get(i).getPlayerName().equalsIgnoreCase(e.getPlayer().getName())) {
                        String[] reward = winners.get(i).getReward().split(":");
                        if (e.getPlayer().getInventory().firstEmpty() != -1) {
                            ItemStack item = new ItemStack(Material.getMaterial(reward[0]));
                            item.setAmount(Integer.parseInt(reward[1]));
                            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', challengeReward.replace("{number}", reward[1]).replace("{item}", reward[0])));
                            final int number = i;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                                @Override
                                public void run() {
                                    e.getPlayer().getInventory().addItem(item);
                                    H2Database.instance.deleteDailyWinnerWithId(winners.get(number).getId());
                                    winners.remove(number);
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
