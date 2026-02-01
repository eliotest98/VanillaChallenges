package io.eliotesta98.VanillaChallenges.Comandi;

import com.HeroxWar.HeroxCore.MessageGesture;
import com.HeroxWar.HeroxCore.ReloadGesture;
import com.HeroxWar.HeroxCore.TimeGesture.Time;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import io.eliotesta98.VanillaChallenges.Events.DailyGiveWinners;
import io.eliotesta98.VanillaChallenges.Interfaces.Interface;
import io.eliotesta98.VanillaChallenges.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import io.eliotesta98.VanillaChallenges.Core.Main;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Commands implements CommandExecutor {

    private final String errorYouAreNotAPlayer = Main.instance.getConfigGestion().getMessages().get("Errors.YouAreNotAPlayer");
    private final String errorCommandNotFound = Main.instance.getConfigGestion().getMessages().get("Errors.CommandNotFound");
    private final String errorNoPerms = Main.instance.getConfigGestion().getMessages().get("Errors.NoPerms");
    private final String alreadyStartEvent = Main.instance.getConfigGestion().getMessages().get("Errors.AlreadyStartEvent");
    private final String alreadyStopEvent = Main.instance.getConfigGestion().getMessages().get("Errors.AlreadyStopEvent");
    private final String scheduleError = Main.instance.getConfigGestion().getMessages().get("Errors.Schedule");
    private final String addError = Main.instance.getConfigGestion().getMessages().get("Errors.Add");
    private final String alreadyDisable = Main.instance.getConfigGestion().getMessages().get("Errors.AlreadyDisable");
    private final String timeError = Main.instance.getConfigGestion().getMessages().get("Errors.Time");

    private final String addSuccess = Main.instance.getConfigGestion().getMessages().get("Success.Add");
    private final String removeSuccess = Main.instance.getConfigGestion().getMessages().get("Success.Remove");
    private final String succesfullyRestored = Main.instance.getConfigGestion().getMessages().get("Success.Restored");
    private final String timeRemove = Main.instance.getConfigGestion().getMessages().get("Success.TimeRemove");
    private final String timeAdd = Main.instance.getConfigGestion().getMessages().get("Success.TimeAdd");
    private final String timeSet = Main.instance.getConfigGestion().getMessages().get("Success.TimeSet");

    private final String commandFooter = Main.instance.getConfigGestion().getMessages().get("Commands.Footer");
    private final String commandVcReloadHelp = Main.instance.getConfigGestion().getMessages().get("Commands.Reload");
    private final String commandVcNextHelp = Main.instance.getConfigGestion().getMessages().get("Commands.Next");
    private final String commandVcPointsHelp = Main.instance.getConfigGestion().getMessages().get("Commands.Points");
    private final String commandVcTopHelp = Main.instance.getConfigGestion().getMessages().get("Commands.Top");
    private final String commandVcClear = Main.instance.getConfigGestion().getMessages().get("Commands.Clear");
    private final String commandVcChallenge = Main.instance.getConfigGestion().getMessages().get("Commands.Challenge");
    private final String commandVcEconomyChallenge = Main.instance.getConfigGestion().getMessages().get("Commands.Economy");
    private final String commandVcReward = Main.instance.getConfigGestion().getMessages().get("Commands.Reward");
    private final String commandVcList = Main.instance.getConfigGestion().getMessages().get("Commands.List");
    private final String commandVcEvent = Main.instance.getConfigGestion().getMessages().get("Commands.Event");
    private final String commandVcSchedule = Main.instance.getConfigGestion().getMessages().get("Commands.Schedule");
    private final String commandVcRestore = Main.instance.getConfigGestion().getMessages().get("Commands.Restore");
    private final String commandVcTime = Main.instance.getConfigGestion().getMessages().get("Commands.Time");

    private final String fileList = Main.instance.getConfigGestion().getMessages().get("Lists.Files");
    private final String fileLine = Main.instance.getConfigGestion().getMessages().get("Lists.FilesLine");

    private final String pointsInfo = Main.instance.getConfigGestion().getMessages().get("Points.Info");
    private final String pointsPlayerPlaceholder = Main.instance.getConfigGestion().getMessages().get("Points.PlayerPlaceholder");
    private final String actuallyInTop = Main.instance.getConfigGestion().getMessages().get("ActuallyInTop");
    private final String pointsadd = Main.instance.getConfigGestion().getMessages().get("PointsAdd");
    private final String pointsremove = Main.instance.getConfigGestion().getMessages().get("PointsRemove");
    private final String timeRemaining = Main.instance.getConfigGestion().getMessages().get("TimeRemaining");
    private final String cooldown = Main.instance.getConfigGestion().getMessages().get("Cooldown");

    private final String challengeList = Main.instance.getConfigGestion().getMessages().get("ChallengeList");
    private final String challengeOfList = Main.instance.getConfigGestion().getMessages().get("ChallengeofList");

    private final boolean debugCommand = Main.instance.getConfigGestion().getDebug().get("Commands");
    private final boolean resetPoints = Main.instance.getConfigGestion().isResetPointsAtNewChallenge();

    private final int numberOfTop = Main.instance.getConfigGestion().getNumberOfTop();
    private final int numberOfRewardedPlayer = Main.instance.getConfigGestion().getNumberOfRewardPlayer();
    private final boolean rankingReward = Main.instance.getConfigGestion().isRankingReward();
    private final boolean randomReward = Main.instance.getConfigGestion().isRandomReward();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            DebugUtils debug = new DebugUtils("Commands");
            long tempo = System.currentTimeMillis();
            if (!command.getName().equalsIgnoreCase("vanillachallenges")) {// comando se esiste
                MessageGesture.sendMessage(sender, errorCommandNotFound);
                if (debugCommand) {
                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                    debug.debug();
                }
                return;
            }
            if (args.length == 0) {// se non ha scritto args
                String finale = "\n\n&e&l" + Main.instance.getName() + "&7 ● Version " + Main.instance.getDescription().getVersion()
                        + " created by &a&leliotesta98 & xSavior_of_God" + "\n&r\n";
                if (sender.hasPermission("vc.add.command")) {
                    finale = finale + commandVcEconomyChallenge + "\n";
                }
                if (sender.hasPermission("vc.challenge.command")) {
                    finale = finale + commandVcChallenge + "\n";
                }
                if (sender.hasPermission("vc.clear.command")) {
                    finale = finale + commandVcClear + "\n";
                }
                if (sender.hasPermission("vc.event.start.command") || sender.hasPermission("vc.event.stop.command")) {
                    finale = finale + commandVcEvent + "\n";
                }
                if (sender.hasPermission("vc.list.command")) {
                    finale = finale + commandVcList + "\n";
                }
                if (sender.hasPermission("vc.next.command")) {
                    finale = finale + commandVcNextHelp + "\n";
                }
                if (sender.hasPermission("vc.points.command")) {
                    finale = finale + commandVcPointsHelp + "\n";
                }
                if (sender.hasPermission("vc.reload.command")) {
                    finale = finale + commandVcReloadHelp + "\n";
                }
                if (sender.hasPermission("vc.reward.command")) {
                    finale = finale + commandVcReward + "\n";
                }
                if (sender.hasPermission("vc.schedule.add.command") || sender.hasPermission("vc.schedule.remove.command")) {
                    finale = finale + commandVcSchedule + "\n";
                }
                if (sender.hasPermission("vc.time.add.command") || sender.hasPermission("vc.time.remove.command") || sender.hasPermission("vc.time.set.command")) {
                    finale = finale + commandVcTime + "\n";
                }
                if (sender.hasPermission("vc.top.command")) {
                    finale = finale + commandVcTopHelp + "\n";
                }
                finale = finale + "&r\n";
                finale = finale + commandFooter;
                MessageGesture.sendMessage(sender, finale);
                if (debugCommand) {
                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                    debug.debug();
                }
                return;
            }

            switch (args[0]) {
                case "add":
                    if (!sender.hasPermission("vc.add.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    if (args.length > 3) {
                        MessageGesture.sendMessage(sender, commandVcEconomyChallenge);
                        break;
                    }
                    Main.instance.getDailyChallenge().incrementCommands(args[1], Long.parseLong(args[2]));
                    MessageGesture.sendMessage(sender, pointsadd.replace("{points}", args[2]).replace("{player}", args[1]));
                    break;
                case "challenge":
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    if (args.length != 1) {
                        MessageGesture.sendMessage(sender, commandVcChallenge);
                        break;
                    }
                    Main.instance.getDailyChallenge().message(sender);
                    break;
                case "clear":
                    if (!sender.hasPermission("vc.clear.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (args.length != 1) {
                        MessageGesture.sendMessage(sender, commandVcClear);
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Bukkit.getScheduler().runTask(Main.instance, () -> {
                        Main.db.clearAll();
                        if (Main.challengeSelected) {
                            Main.instance.getDailyChallenge().clearPlayers();
                            for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                                interfaces.getValue().closeAllInventories();
                            }
                        }
                        Main.instance.getConfigGestion().getTasks().stopAllTasks();
                        Main.instance.unregisterCurrentListener();
                        Main.instance.pluginStartingProcess();
                    });
                    break;
                case "event":
                    if (args.length < 2 || args.length > 3) {
                        MessageGesture.sendMessage(sender, commandVcEvent);
                        break;
                    }

                    String rowTime = "";
                    if (args.length == 3) {
                        rowTime = args[2];
                        String typeTime = rowTime.charAt(rowTime.length() - 1) + "";

                        try {
                            int time = Integer.parseInt(rowTime.replace(typeTime, ""));
                        } catch (NumberFormatException e) {
                            MessageGesture.sendMessage(sender, timeError.replace("{value}", rowTime));
                            break;
                        }
                    }

                    // event stop
                    if (args[1].equalsIgnoreCase("stop")) {
                        if (!sender.hasPermission("vc.event.stop.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        List<Challenge> challenges = Main.db.getChallenges();
                        if (!challenges.isEmpty() && !challenges.get(0).getChallengeName().contains("Event_")) {
                            MessageGesture.sendMessage(sender, alreadyStopEvent);
                            break;
                        }
                        if (challenges.isEmpty()) {
                            MessageGesture.sendMessage(sender, alreadyStopEvent);
                            break;
                        }
                        Bukkit.getScheduler().runTask(Main.instance, () -> {
                            List<Challenger> topPlayers = Main.instance.getDailyChallenge().getTopPlayers(numberOfRewardedPlayer);
                            Main.db.removeTopYesterday();
                            Main.db.saveTopYesterday(Main.instance.getDailyChallenge().getTopPlayers(numberOfTop));
                            if (Main.instance.getConfigGestion().isBackupEnabled()) {
                                Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                            }
                            int number = Main.db.lastDailyWinnerId();
                            Random random = new Random();
                            if (Main.instance.getDailyChallenge().isMinimumPointsReached()) {
                                for (int z = 0; z < topPlayers.size(); z++) {
                                    int placeInTop = z;
                                    int rewardsSize = Main.instance.getDailyChallenge().getRewards().size();
                                    if (z >= rewardsSize) {
                                        placeInTop = rewardsSize - 1;
                                    }
                                    number++;
                                    DailyWinner dailyWinner = new DailyWinner();
                                    dailyWinner.setPlayerName(topPlayers.get(z).getNomePlayer());
                                    dailyWinner.setNomeChallenge(Main.instance.getDailyChallenge().getChallengeName());
                                    if (rankingReward) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(placeInTop));
                                        Main.db.insertDailyWinner(dailyWinner);
                                    } else {
                                        if (randomReward) {
                                            dailyWinner.setId(number);
                                            dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(random.nextInt(rewardsSize)));
                                            Main.db.insertDailyWinner(dailyWinner);
                                        } else {
                                            for (int i = 0; i < rewardsSize; i++) {
                                                dailyWinner.setId(number);
                                                dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(i));
                                                Main.db.insertDailyWinner(dailyWinner);
                                                number++;
                                            }
                                        }
                                    }
                                }
                            }
                            Main.db.deleteChallengeWithName(Main.db.getChallenges().get(0).getChallengeName());
                            Main.db.resumeOldPoints();
                            if (Main.challengeSelected) {
                                Main.instance.getDailyChallenge().clearPlayers();
                                for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                                    interfaces.getValue().closeAllInventories();
                                }
                            }
                            Main.instance.getConfigGestion().getTasks().stopAllTasks();
                            Main.instance.unregisterCurrentListener();
                            Main.instance.pluginStartingProcess();
                        });
                    }
                    // start a random event challenge
                    else if (args[1].equalsIgnoreCase("random")) {
                        List<Challenge> challenges = Main.db.getChallenges();
                        if (!challenges.isEmpty() && challenges.get(0).getChallengeName().contains("Event_")) {
                            MessageGesture.sendMessage(sender, alreadyStartEvent);
                            break;
                        }
                        Random random = new Random();
                        int sizeChallenges = random.nextInt(Main.instance.getConfigGestion().getChallengesEvent().size() - 1);
                        int i = 0;
                        Challenge challengeSelected = null;
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
                            if (i == sizeChallenges) {
                                challengeSelected = challenge.getValue();
                                break;
                            }
                            i++;
                        }
                        if (!rowTime.equalsIgnoreCase("")) {
                            challengeSelected = challengeSelected.cloneChallenge(rowTime);
                        }
                        String challengeName = challengeSelected.getChallengeName();
                        long milliseconds = challengeSelected.getTimeChallenge().getMilliseconds();
                        Bukkit.getScheduler().runTask(Main.instance, () -> {
                            Main.db.insertChallengeEvent(challengeName, milliseconds);
                            if (Main.challengeSelected) {
                                Main.db.saveOldPointsForChallengeEvents();
                                Main.instance.getDailyChallenge().clearPlayers();
                                for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                                    interfaces.getValue().closeAllInventories();
                                }
                            }
                            Main.instance.getConfigGestion().getTasks().stopAllTasks();
                            Main.instance.unregisterCurrentListener();
                            Main.instance.pluginStartingProcess();
                        });
                    }
                    // start a specified event challenge
                    else if (Main.instance.getConfigGestion().getChallengesEvent().get(args[1]) != null) {
                        if (!sender.hasPermission("vc.event.start.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        List<Challenge> challenges = Main.db.getChallenges();
                        if (!challenges.isEmpty() && challenges.get(0).getChallengeName().contains("Event_")) {
                            MessageGesture.sendMessage(sender, alreadyStartEvent);
                            break;
                        }
                        Challenge challengeSelected = Main.instance.getConfigGestion().getChallengesEvent().get(args[1]);
                        if (!rowTime.equalsIgnoreCase("")) {
                            challengeSelected = challengeSelected.cloneChallenge(rowTime);
                        }
                        String challengeName = challengeSelected.getChallengeName();
                        long milliseconds = challengeSelected.getTimeChallenge().getMilliseconds();
                        Bukkit.getScheduler().runTask(Main.instance, () -> {
                            Main.db.insertChallengeEvent(challengeName, milliseconds);
                            if (Main.challengeSelected) {
                                Main.db.saveOldPointsForChallengeEvents();
                                Main.instance.getDailyChallenge().clearPlayers();
                                for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                                    interfaces.getValue().closeAllInventories();
                                }
                            }
                            Main.instance.getConfigGestion().getTasks().stopAllTasks();
                            Main.instance.unregisterCurrentListener();
                            Main.instance.pluginStartingProcess();
                        });
                    }
                    // list of challenges
                    else {
                        if (!sender.hasPermission("vc.event.start.command") || !sender.hasPermission("vc.event.stop.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        StringBuilder send = new StringBuilder("\n");
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
                            send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                        }
                        MessageGesture.sendMessage(sender, challengeList.replace("{challengeList}", send.toString()));
                    }
                    break;
                case "list":
                    if (!(sender instanceof Player)) {
                        MessageGesture.sendMessage(sender, errorYouAreNotAPlayer);
                        break;
                    }
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    if (!sender.hasPermission("vc.list.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (args.length != 1) {
                        MessageGesture.sendMessage(sender, commandVcList);
                        break;
                    }
                    Bukkit.getScheduler().runTask(Main.instance, () -> {
                        int size = Main.instance.getConfigGestion().getInterfaces().get("Challenges").getSizeModificableSlot();
                        ArrayList<Challenge> challenges = new ArrayList<>();
                        for (Challenge challenge : Main.db.getChallenges()) {
                            if (challenges.size() < size) {
                                challenges.add(challenge);
                            }
                        }
                        Main.instance.getConfigGestion().getInterfaces().get("Challenges").openInterface(
                                challenges, (Player) sender, 1);
                    });
                    break;
                case "next":
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    if (!sender.hasPermission("vc.next.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (args.length > 2) {
                        MessageGesture.sendMessage(sender, commandVcNextHelp);
                        break;
                    }

                    boolean skipPeacefulTime = args.length != 1;

                    if (Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                        MessageGesture.sendMessage(sender, alreadyStartEvent);
                        break;
                    }

                    Bukkit.getScheduler().runTask(Main.instance, () -> Main.instance.getDailyChallenge().nextChallenge(resetPoints, rankingReward, randomReward, numberOfRewardedPlayer, numberOfTop, "Command Execution", skipPeacefulTime));
                    break;
                case "points":
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    // controllo se ha il permesso
                    if (!sender.hasPermission("vc.points.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (args.length > 2) {
                        MessageGesture.sendMessage(sender, commandVcPointsHelp);
                        break;
                    }
                    if (args.length == 1) {
                        if (sender instanceof Player) {
                            long points = Main.instance.getDailyChallenge().getPointFromPLayerName(sender.getName());
                            MessageGesture.sendMessage(sender, pointsInfo.replace("{player}", pointsPlayerPlaceholder).replace("{number}", "" + points));
                        } else {
                            MessageGesture.sendMessage(sender, errorYouAreNotAPlayer);
                        }
                    } else {
                        if (!sender.hasPermission("vc.points.admin.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        long points = Main.instance.getDailyChallenge().getPointFromPLayerName(args[1]);
                        MessageGesture.sendMessage(sender, pointsInfo.replace("{player}", args[1]).replace("{number}", "" + points));
                    }
                    break;
                case "reload":
                    // controllo se ha il permesso
                    if (!sender.hasPermission("vc.reload.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (args.length != 1) {
                        MessageGesture.sendMessage(sender, commandVcReloadHelp);
                        break;
                    }
                    Bukkit.getScheduler().runTask(Main.instance, () -> {
                        MessageGesture.sendMessage(sender, "&6Reloading...");
                        ReloadGesture.reload(Main.instance.getName());
                        MessageGesture.sendMessage(sender, "&aReloaded!");
                    });
                    break;
                case "remove":
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    // controllo se ha il permesso
                    if (!sender.hasPermission("vc.remove.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (args.length > 2) {
                        MessageGesture.sendMessage(sender, commandVcEconomyChallenge);
                        break;
                    }
                    Main.instance.getDailyChallenge().incrementCommands(args[1], -Long.parseLong(args[2]));
                    MessageGesture.sendMessage(sender, pointsremove.replace("{points}", args[2]).replace("{player}", args[1]));
                    break;
                case "reward":
                    if (!(sender instanceof Player)) {
                        MessageGesture.sendMessage(sender, errorYouAreNotAPlayer);
                        break;
                    }
                    // controllo se ha il permesso
                    if (!sender.hasPermission("vc.reward.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (args.length > 1) {
                        MessageGesture.sendMessage(sender, commandVcReward);
                        break;
                    }
                    List<DailyWinner> winners = Main.db.getDailyWinners();
                    if (!winners.isEmpty()) {
                        DailyGiveWinners.getRewardsAtPlayers((Player) sender, winners);
                    }
                    break;
                case "restore":
                    if (args.length > 3) {
                        MessageGesture.sendMessage(sender, commandVcRestore);
                        break;
                    }
                    if (args.length == 1) {
                        File folder = new File(Main.instance.getDataFolder() +
                                File.separator + "backup");
                        StringBuilder finale = new StringBuilder("\n" + fileList + "\n");
                        for (int i = 0; i < folder.listFiles().length; i++) {
                            if (folder.listFiles()[i].length() != 0L) {
                                finale.append(fileLine.replace("{fileName}", folder.listFiles()[i].getName() + "\n"));
                            }
                        }
                        MessageGesture.sendMessage(sender, finale.toString());
                        break;
                    }
                    File configFile = new File(Main.instance.getDataFolder() +
                            File.separator + "backup", args[1]);
                    Main.db.clearAll();
                    YamlConfiguration file = YamlConfiguration.loadConfiguration(configFile);
                    if (file.getConfigurationSection("Challenges") != null) {
                        for (String challenge : file.getConfigurationSection("Challenges").getKeys(true)) {
                            Main.db.insertChallenge(challenge, file.getInt("Challenges." + challenge));
                        }
                    }
                    if (file.getConfigurationSection("Points") != null) {
                        for (String challengerName : file.getConfigurationSection("Points").getKeys(true)) {
                            Main.db.insertChallenger(challengerName, file.getInt("Points." + challengerName));
                        }
                    }
                    if (file.getConfigurationSection("DailyWinners") != null) {
                        for (String idWinner : file.getConfigurationSection("DailyWinners").getKeys(false)) {
                            Main.db.insertDailyWinner(new DailyWinner(
                                    Integer.parseInt(idWinner),
                                    file.getString("DailyWinners." + idWinner + ".PlayerName"),
                                    file.getString("DailyWinners." + idWinner + ".NomeChallenge"),
                                    file.getString("DailyWinners." + idWinner + ".Reward")
                            ));
                        }
                    }
                    if (file.getConfigurationSection("TopYesterday") != null) {
                        H2Database h2 = (H2Database) Main.db;
                        for (String winnerYesterday : file.getConfigurationSection("TopYesterday").getKeys(true)) {
                            h2.insertChallengerTopYesterday(winnerYesterday, file.getInt("TopYesterday." + winnerYesterday));
                        }
                    }
                    MessageGesture.sendMessage(sender, succesfullyRestored);
                    if (Main.challengeSelected) {
                        Main.instance.getDailyChallenge().clearPlayers();
                        for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                            interfaces.getValue().closeAllInventories();
                        }
                    }
                    Main.instance.getConfigGestion().getTasks().stopAllTasks();
                    Main.instance.unregisterCurrentListener();
                    Main.instance.pluginStartingProcess();
                    break;
                case "schedule":
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    if (args.length < 2 || args.length > 4) {
                        MessageGesture.sendMessage(sender, commandVcSchedule);
                        break;
                    }

                    Challenge challenge1 = null;
                    if (args.length >= 3) {
                        challenge1 = Main.instance.getConfigGestion().getChallenges().get(args[2]);
                    }

                    // schedule add
                    if (args[1].equalsIgnoreCase("add")) {
                        if (!sender.hasPermission("vc.schedule.add.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        if (challenge1 == null && !args[2].equalsIgnoreCase("random")) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                            }
                            MessageGesture.sendMessage(sender, challengeList.replace("{challengeList}", send.toString()));
                        } else {
                            if (args[2].equalsIgnoreCase("random")) {
                                List<Challenge> scheduledChallenges = Main.db.getChallenges();
                                ArrayList<Challenge> remainChallenges = new ArrayList<>();
                                boolean find;
                                for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallenges().entrySet()) {
                                    find = false;
                                    for (Challenge challenge2 : scheduledChallenges) {
                                        if (challenge.getKey().equalsIgnoreCase(challenge2.getChallengeName())) {
                                            find = true;
                                            break;
                                        }
                                    }
                                    if (!find) {
                                        remainChallenges.add(challenge.getValue());
                                    }
                                }
                                if (remainChallenges.isEmpty()) {
                                    MessageGesture.sendMessage(sender, addError);
                                    break;
                                } else {
                                    Collections.shuffle(remainChallenges);
                                    challenge1 = remainChallenges.get(0);
                                }
                            }
                            if (Main.db.isChallengePresent(challenge1.getChallengeName())) {
                                MessageGesture.sendMessage(sender, addError);
                            } else {
                                String rowTimeScheduler = "";
                                if (args.length == 4) {
                                    rowTimeScheduler = args[3];
                                    String typeTime = rowTimeScheduler.charAt(rowTimeScheduler.length() - 1) + "";

                                    try {
                                        int time = Integer.parseInt(rowTimeScheduler.replace(typeTime, ""));
                                    } catch (NumberFormatException e) {
                                        MessageGesture.sendMessage(sender, timeError.replace("{value}", rowTimeScheduler));
                                        break;
                                    }
                                }
                                Challenge finalChallenge;
                                if (rowTimeScheduler.equalsIgnoreCase("")) {
                                    finalChallenge = challenge1;
                                } else {
                                    finalChallenge = challenge1.cloneChallenge(rowTimeScheduler);
                                }
                                Challenge challengeSelected = finalChallenge;
                                Bukkit.getScheduler().runTask(Main.instance, () -> {
                                    Main.db.addChallenge(challengeSelected);
                                    Main.db.insertChallenge(challengeSelected.getChallengeName(), challengeSelected.getTimeChallenge().getMilliseconds());
                                    MessageGesture.sendMessage(sender, addSuccess);
                                    if (Main.challengeSelected) {
                                        Main.instance.getDailyChallenge().clearPlayers();
                                        for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                                            interfaces.getValue().closeAllInventories();
                                        }
                                    }
                                    Main.instance.getConfigGestion().getTasks().stopAllTasks();
                                    Main.instance.unregisterCurrentListener();
                                    Main.instance.pluginStartingProcess();
                                });
                            }
                        }
                    }
                    // schedule remove
                    else if (args[1].equalsIgnoreCase("remove")) {
                        if (!sender.hasPermission("vc.schedule.remove.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        if (challenge1 == null) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Challenge challenge : Main.db.getChallenges()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getChallengeName())).append("\n");
                            }
                            MessageGesture.sendMessage(sender, challengeList.replace("{challengeList}", send.toString()));
                        } else {
                            if (Main.instance.getDailyChallenge().getChallengeName().equalsIgnoreCase(args[2])) {
                                MessageGesture.sendMessage(sender, scheduleError);
                            } else {
                                Bukkit.getScheduler().runTask(Main.instance, () -> {
                                    Main.db.deleteChallengeWithName(args[2]);
                                    MessageGesture.sendMessage(sender, removeSuccess);
                                    if (Main.challengeSelected) {
                                        Main.instance.getDailyChallenge().clearPlayers();
                                        for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                                            interfaces.getValue().closeAllInventories();
                                        }
                                    }
                                    Main.instance.getConfigGestion().getTasks().stopAllTasks();
                                    Main.instance.unregisterCurrentListener();
                                    Main.instance.pluginStartingProcess();
                                });
                            }
                        }
                    }
                    // schedule disable
                    else if (args[1].equalsIgnoreCase("disable")) {
                        if (!sender.hasPermission("vc.schedule.disable.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        if (Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                            MessageGesture.sendMessage(sender, alreadyDisable);
                            break;
                        }
                        Main.instance.getConfigGestion().setChallengeGeneration("Nothing");
                        Main.instance.unregisterCurrentListener();
                        Main.instance.getConfigGestion().getTasks().stopAllTasks();
                        Main.instance.pluginStartingProcess();
                    }
                    // schedule help command
                    else {
                        MessageGesture.sendMessage(sender, commandVcSchedule);
                    }
                    break;
                case "time":
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    if (args.length < 2 || args.length > 3) {
                        MessageGesture.sendMessage(sender, commandVcTime);
                        break;
                    }

                    if (args.length == 3) {
                        String rawTime = args[2];
                        String typeTime = rawTime.charAt(rawTime.length() - 1) + "";
                        int time;
                        try {
                            time = Integer.parseInt(rawTime.replace(typeTime, ""));
                        } catch (NumberFormatException exception) {
                            MessageGesture.sendMessage(sender, timeError.replace("{value}", args[2]));
                            break;
                        }
                        Time timeForCommand;
                        switch (typeTime) {
                            case "s":
                                timeForCommand = new Time(0, 0, 0, time, ':');
                                break;
                            case "m":
                                timeForCommand = new Time(0, 0, time, 0, ':');
                                break;
                            case "h":
                                timeForCommand = new Time(0, time, 0, 0, ':');
                                break;
                            default:
                                timeForCommand = new Time(0, ':');
                                break;
                        }

                        // time add
                        if (args[1].equalsIgnoreCase("add")) {
                            if (!sender.hasPermission("vc.time.add.command")) {
                                MessageGesture.sendMessage(sender, errorNoPerms);
                                break;
                            }
                            Main.instance.getDailyChallenge().addTime(timeForCommand);
                            Time timeRemaining = Main.instance.getDailyChallenge().getTimeChallenge();
                            MessageGesture.sendMessage(sender, timeAdd
                                    .replace("{time}", args[2])
                                    .replace("{hours}", timeRemaining.getHours() + "")
                                    .replace("{minutes}", timeRemaining.getMinutes() + "")
                                    .replace("{seconds}", timeRemaining.getSeconds() + "")
                            );
                        }
                        // time remove
                        else if (args[1].equalsIgnoreCase("remove")) {
                            if (!sender.hasPermission("vc.time.remove.command")) {
                                MessageGesture.sendMessage(sender, errorNoPerms);
                                break;
                            }
                            Main.instance.getDailyChallenge().removeTime(timeForCommand);
                            Time timeRemaining = Main.instance.getDailyChallenge().getTimeChallenge();
                            MessageGesture.sendMessage(sender, timeRemove
                                    .replace("{time}", args[2])
                                    .replace("{hours}", timeRemaining.getHours() + "")
                                    .replace("{minutes}", timeRemaining.getMinutes() + "")
                                    .replace("{seconds}", timeRemaining.getSeconds() + "")
                            );
                        }
                        // time set
                        else if (args[1].equalsIgnoreCase("set")) {
                            if (!sender.hasPermission("vc.time.set.command")) {
                                MessageGesture.sendMessage(sender, errorNoPerms);
                                break;
                            }
                            if (timeForCommand.getMilliseconds() <= 0) {
                                break;
                            }
                            Main.instance.getDailyChallenge().setTimeChallenge(timeForCommand);
                            MessageGesture.sendMessage(sender, timeSet.replace("{time}", args[2]));
                        }
                        // time remaining
                        else {
                            if (!sender.hasPermission("vc.time.remaining.command")) {
                                MessageGesture.sendMessage(sender, errorNoPerms);
                                break;
                            }
                            Time timeRemain = Main.instance.getDailyChallenge().getTimeChallenge();
                            MessageGesture.sendMessage(sender, timeRemaining
                                    .replace("{hours}", timeRemain.getHours() + "")
                                    .replace("{minutes}", timeRemain.getMinutes() + "")
                                    .replace("{seconds}", timeRemain.getSeconds() + "")
                            );
                        }
                    }
                    // time remaining
                    else {
                        if (!sender.hasPermission("vc.time.remaining.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        Time timeRemain = Main.instance.getDailyChallenge().getTimeChallenge();
                        MessageGesture.sendMessage(sender, timeRemaining
                                .replace("{hours}", timeRemain.getHours() + "")
                                .replace("{minutes}", timeRemain.getMinutes() + "")
                                .replace("{seconds}", timeRemain.getSeconds() + "")
                        );
                    }
                    break;
                case "top":
                    if (!Main.challengeSelected) {
                        if (!Main.db.checkPeacefulTime()) {
                            if (!Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Nothing")) {
                                sender.sendMessage(ChatColor.RED + "No DailyChallenge selected, check the configurations files and restart the plugin!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "No Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
                            }
                        } else {
                            Time time = Main.db.getPeacefulTime();
                            MessageGesture.sendMessage(sender, cooldown
                                    .replace("{hours}", time.getHours() + "")
                                    .replace("{minutes}", time.getMinutes() + "")
                                    .replace("{seconds}", time.getSeconds() + ""));
                        }
                        break;
                    }
                    // controllo se ha il permesso
                    if (!sender.hasPermission("vc.top.command")) {
                        MessageGesture.sendMessage(sender, errorNoPerms);
                        break;
                    }
                    if (args.length > 2) {
                        MessageGesture.sendMessage(sender, commandVcTopHelp);
                        break;
                    }
                    List<Challenger> top = new ArrayList<>();
                    if (args.length == 1) {
                        if (Main.instance.getConfigGestion().isYesterdayTop()) {
                            top = Main.db.getTopYesterday();
                        } else {
                            top = Main.instance.getDailyChallenge().getTopPlayers(numberOfTop);
                        }
                    } else if (args[1].equalsIgnoreCase("yesterday")) {
                        if (!sender.hasPermission("vc.top.yesterday.command")) {
                            MessageGesture.sendMessage(sender, errorNoPerms);
                            break;
                        }
                        top = Main.db.getTopYesterday();
                    }
                    MessageGesture.sendMessage(sender, actuallyInTop);
                    int i = 1;
                    for (Challenger challenger : top) {
                        MessageGesture.sendMessage(sender, Main.instance.getConfigGestion().getMessages().get("TopPlayers" + i)
                                .replace("{number}", "" + i)
                                .replace("{player}", challenger.getNomePlayer())
                                .replace("{points}", MoneyUtils.transform(challenger.getPoints())));
                        i++;
                    }
                    break;
                default:
                    String finale = "\n\n&e&l" + Main.instance.getName() + "&7 ● Version " + Main.instance.getDescription().getVersion()
                            + " created by &a&leliotesta98 & xSavior_of_God" + "\n&r\n";
                    if (sender.hasPermission("vc.add.command")) {
                        finale = finale + commandVcEconomyChallenge + "\n";
                    }
                    if (sender.hasPermission("vc.challenge.command")) {
                        finale = finale + commandVcChallenge + "\n";
                    }
                    if (sender.hasPermission("vc.clear.command")) {
                        finale = finale + commandVcClear + "\n";
                    }
                    if (sender.hasPermission("vc.event.start.command") || sender.hasPermission("vc.event.stop.command")) {
                        finale = finale + commandVcEvent + "\n";
                    }
                    if (sender.hasPermission("vc.list.command")) {
                        finale = finale + commandVcList + "\n";
                    }
                    if (sender.hasPermission("vc.next.command")) {
                        finale = finale + commandVcNextHelp + "\n";
                    }
                    if (sender.hasPermission("vc.points.command")) {
                        finale = finale + commandVcPointsHelp + "\n";
                    }
                    if (sender.hasPermission("vc.reload.command")) {
                        finale = finale + commandVcReloadHelp + "\n";
                    }
                    if (sender.hasPermission("vc.reward.command")) {
                        finale = finale + commandVcReward + "\n";
                    }
                    if (sender.hasPermission("vc.schedule.add.command") || sender.hasPermission("vc.schedule.remove.command")) {
                        finale = finale + commandVcSchedule + "\n";
                    }
                    if (sender.hasPermission("vc.time.add.command") || sender.hasPermission("vc.time.remove.command") || sender.hasPermission("vc.time.set.command")) {
                        finale = finale + commandVcTime + "\n";
                    }
                    if (sender.hasPermission("vc.top.command")) {
                        finale = finale + commandVcTopHelp + "\n";
                    }
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    MessageGesture.sendMessage(sender, finale);
                    break;
            }
            if (debugCommand) {
                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                debug.debug();
            }
        });
        return false;
    }
}