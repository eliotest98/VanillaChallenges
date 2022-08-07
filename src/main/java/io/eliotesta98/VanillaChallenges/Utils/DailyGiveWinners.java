package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.text.ParseException;
import java.util.ArrayList;

public class DailyGiveWinners implements Listener {

    private ArrayList<DailyWinner> winners = new ArrayList<DailyWinner>();
    private DebugUtils debugUtils = new DebugUtils();
    private boolean debug = Main.instance.getConfigGestion().getDebug().get("DailyGiveRewardEvent");
    private String challengeReward = Main.instance.getConfigGestion().getMessages().get("challengeReward");

    public DailyGiveWinners() {
        winners = Main.db.getAllDailyWinners();
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
                        final int number = i;
                        boolean give = true;
                        if (winners.get(i).getReward().equalsIgnoreCase("NOBODY")) {
                            give = false;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                                @Override
                                public void run() {
                                    Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
                                    winners.remove(number);
                                }
                            });
                        }
                        if (e.getPlayer().getInventory().firstEmpty() != -1
                                && give
                                && !reward[0].equalsIgnoreCase("[command]")) {
                            ItemStack item = new ItemStack(Material.getMaterial(reward[0]));
                            item.setAmount(Integer.parseInt(reward[1]));
                            e.getPlayer().sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", reward[1]).replace("{item}", reward[0])));
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                                @Override
                                public void run() {
                                    e.getPlayer().getInventory().addItem(item);
                                    Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
                                    winners.remove(number);
                                }
                            });
                        } else {
                            String[] listCommand = reward[1].split("\\s+");
                            int amount = 0;
                            Material material = Material.AIR;
                            for (int j = 0; j < listCommand.length; j++) {
                                try {
                                    amount = Integer.parseInt(listCommand[j]);
                                } catch (NumberFormatException ex) {
                                    try {
                                        material = Material.getMaterial(listCommand[j]);
                                    } catch (NullPointerException e) {

                                    }
                                }
                            }
                            if (material == Material.AIR || material == null) {
                                e.getPlayer().sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", amount + "").replace("{item}", "")));
                            } else {
                                e.getPlayer().sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", amount + "").replace("{item}", material.toString())));
                            }
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                                @Override
                                public void run() {
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), reward[1].replace("%player%", e.getPlayer().getName()));
                                    Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
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
