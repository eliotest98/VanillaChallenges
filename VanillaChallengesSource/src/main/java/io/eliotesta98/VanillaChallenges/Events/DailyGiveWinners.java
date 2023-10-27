package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DailyGiveWinners implements Listener {

    private final DebugUtils debugUtils = new DebugUtils("DailyGiveRewardEvent");
    private final boolean debug = Main.instance.getConfigGestion().getDebug().get("DailyGiveRewardEvent");
    private static final String challengeReward = Main.instance.getConfigGestion().getMessages().get("ChallengeReward");
    private static final String prefix = Main.instance.getConfigGestion().getMessages().get("Prefix");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDailyGiveRewards(PlayerJoinEvent e) {
        long tempo = System.currentTimeMillis();
        ArrayList<DailyWinner> winners = Main.db.getAllDailyWinners();
        if (winners.isEmpty()) {
            stopEvent();
            if (debug) {
                debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                debugUtils.debug();
            }
            return;
        }
        getRewardsAtPlayers(e.getPlayer(), winners);
    }

    public static void getRewardsAtPlayers(Player player, ArrayList<DailyWinner> winners) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for (int i = 0; i < winners.size(); i++) {
                if (winners.get(i).getPlayerName().equalsIgnoreCase(player.getName())) {
                    String[] reward = winners.get(i).getReward().split(":");
                    final int number = i;
                    boolean give = true;
                    if (winners.get(i).getReward().equalsIgnoreCase("NOBODY")) {
                        give = false;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
                            winners.remove(number);
                        });
                    }
                    //item
                    if (player.getInventory().firstEmpty() != -1
                            && give
                            && !reward[0].equalsIgnoreCase("[command]")) {
                        ItemStack item;
                        if (reward[0].contains("-")) {
                            String[] splitItem = reward[0].split("-");
                            item = new ItemStack(Material.getMaterial(splitItem[0]), 1, Short.parseShort(splitItem[1]));
                            player.sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", reward[1]).replace("{item}", splitItem[0] + "-" + splitItem[1])));
                        } else {
                            item = new ItemStack(Material.getMaterial(reward[0]));
                            player.sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", reward[1]).replace("{item}", reward[0])));
                        }
                        item.setAmount(Integer.parseInt(reward[1]));
                        ItemStack finalItem = item;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            player.getInventory().addItem(finalItem);
                            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor(prefix + "&6Winner: " + player.getName() + " has received his reward: " + finalItem));
                            Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
                            winners.remove(number);
                        });
                    }
                    //command
                    else {
                        StringBuilder commandRefactor = new StringBuilder();
                        if (reward.length > 2) {
                            boolean first = false;
                            for (String part : reward) {
                                if (!first) {
                                    first = true;
                                    continue;
                                }
                                commandRefactor.append(part).append(":");
                            }
                            commandRefactor = new StringBuilder(commandRefactor.substring(0, commandRefactor.length() - 1));
                        } else {
                            commandRefactor = new StringBuilder(reward[1]);
                        }
                        String finalCommandRefactor = commandRefactor.toString();
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            String commandRefact = finalCommandRefactor.replace("%player%", player.getName());
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandRefact);
                            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.applyColor(prefix + "&6Winner: " + player.getName() + " has received his reward: " + commandRefact));
                            Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
                            winners.remove(number);
                        });
                    }
                    break;
                }
            }
        });
    }

    private void stopEvent() {
        PlayerJoinEvent.getHandlerList().unregister(this);
    }
}
