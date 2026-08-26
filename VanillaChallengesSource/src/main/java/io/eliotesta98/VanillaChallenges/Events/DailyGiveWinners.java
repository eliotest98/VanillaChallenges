package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DailyGiveWinners implements Listener {

    private final DebugUtils debugUtils = new DebugUtils("DailyGiveRewardEvent");
    private final boolean debug = Main.instance.getConfigGestion().getDebug().get("DailyGiveRewardEvent");
    private static final String challengeReward = Main.instance.getConfigGestion().getMessages().get("ChallengeReward");
    private static final String prefix = Main.instance.getConfigGestion().getMessages().get("Prefix");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDailyGiveRewards(PlayerJoinEvent e) {
        long tempo = System.currentTimeMillis();
        List<DailyWinner> winners = Main.db.getDailyWinners();
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

    @SuppressWarnings("deprecation")
    public static void getRewardsAtPlayers(Player player, List<DailyWinner> winners) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for (DailyWinner winner : winners) {
                if (winner.getPlayerName().equalsIgnoreCase(player.getName())) {
                    String[] reward = winner.getReward().split(":");
                    boolean give = true;
                    // If there is not any winner
                    if (winner.getReward().equalsIgnoreCase("NOBODY")) {
                        give = false;
                        Bukkit.getScheduler().runTask(Main.instance, () -> {
                            Main.db.deleteDailyWinnerWithId(winner.getId());
                        });
                    }
                    //item
                    if (player.getInventory().firstEmpty() != -1
                            && give
                            && !reward[0].equalsIgnoreCase("[command]")) {
                        ItemStack item;
                        if (reward[0].contains("-")) {
                            try {
                                String[] splitItem = reward[0].split("-");
                                item = new ItemStack(Material.getMaterial(splitItem[0]), 1, Short.parseShort(splitItem[1]));
                                Main.messageGesturePaper.sendMessage(player,challengeReward.replace("{number}", reward[1]).replace("{item}", splitItem[0] + "-" + splitItem[1]));
                            } catch (IllegalArgumentException exception) {
                                Bukkit.getScheduler().runTask(Main.instance, () -> {
                                    Main.db.deleteDailyWinnerWithId(winner.getId());
                                });
                                continue;
                            }
                        } else {
                            try {
                                item = new ItemStack(Material.getMaterial(reward[0]));
                                Main.messageGesturePaper.sendMessage(player,challengeReward.replace("{number}", reward[1]).replace("{item}", reward[0]));
                            } catch (IllegalArgumentException exception) {
                                Bukkit.getScheduler().runTask(Main.instance, () -> {
                                    Main.db.deleteDailyWinnerWithId(winner.getId());
                                });
                                continue;
                            }
                        }
                        item.setAmount(Integer.parseInt(reward[1]));
                        ItemStack finalItem = item;
                        Bukkit.getScheduler().runTask(Main.instance, () -> {
                            if (player.getInventory().firstEmpty() != -1) {
                                player.getInventory().addItem(finalItem);
                                Main.messageGesturePaper.sendMessage(prefix + "&6Winner: " + player.getName() + " has received his reward: " + finalItem);
                                Main.db.deleteDailyWinnerWithId(winner.getId());
                            }
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
                        Bukkit.getScheduler().runTask(Main.instance, () -> {
                            String commandRefact = finalCommandRefactor.replace("%player%", player.getName());
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandRefact);
                            Main.messageGesturePaper.sendMessage(prefix + "&6Winner: " + player.getName() + " has received his reward: " + commandRefact);
                            Main.db.deleteDailyWinnerWithId(winner.getId());
                        });
                    }
                }
            }
        });
    }

    private void stopEvent() {
        PlayerJoinEvent.getHandlerList().unregister(this);
    }

    /**
     * [19:24:20 WARN]: [VanillaChallenges] Plugin VanillaChallenges v2.0.3 generated an exception while executing task 2534
     * java.util.ConcurrentModificationException: null
     *         at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1096) ~[?:?]
     *         at java.base/java.util.ArrayList$Itr.next(ArrayList.java:1050) ~[?:?]
     *         at VanillaChallenges 2.0.3.jar/io.eliotesta98.VanillaChallenges.Events.DailyGiveWinners.lambda$getRewardsAtPlayers$5(DailyGiveWinners.java:43) ~[VanillaChallenges 2.0.3.jar:?]
     *         at org.bukkit.craftbukkit.scheduler.CraftTask.run(CraftTask.java:78) ~[paper-1.21.10.jar:1.21.10-91-9934c17]
     *         at org.bukkit.craftbukkit.scheduler.CraftAsyncTask.run(CraftAsyncTask.java:57) ~[paper-1.21.10.jar:1.21.10-91-9934c17]
     *         at com.destroystokyo.paper.ServerSchedulerReportingWrapper.run(ServerSchedulerReportingWrapper.java:22) ~[paper-1.21.10.jar:?]
     *         at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1095) ~[?:?]
     *         at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:619) ~[?:?]
     *         at java.base/java.lang.Thread.run(Thread.java:1447) ~[?:?]
     */

}
