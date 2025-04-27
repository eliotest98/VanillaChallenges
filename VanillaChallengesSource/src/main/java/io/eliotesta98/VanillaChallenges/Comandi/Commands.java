package io.eliotesta98.VanillaChallenges.Comandi;

import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Events.ApiEvents.ChallengeChangeEvent;
import io.eliotesta98.VanillaChallenges.Events.Challenges.ItemCollector.ItemCollector;
import io.eliotesta98.VanillaChallenges.Events.DailyGiveWinners;
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

    private final String errorYouAreNotAPlayer = Main.instance.getConfigGesture().getMessages().get("Errors.YouAreNotAPlayer");
    private final String errorCommandNotFound = Main.instance.getConfigGesture().getMessages().get("Errors.CommandNotFound");
    private final String errorNoPerms = Main.instance.getConfigGesture().getMessages().get("Errors.NoPerms");
    private final String alreadyStartEvent = Main.instance.getConfigGesture().getMessages().get("Errors.AlreadyStartEvent");
    private final String alreadyStopEvent = Main.instance.getConfigGesture().getMessages().get("Errors.AlreadyStopEvent");
    private final String scheduleError = Main.instance.getConfigGesture().getMessages().get("Errors.Schedule");
    private final String addError = Main.instance.getConfigGesture().getMessages().get("Errors.Add");

    private final String addSuccess = Main.instance.getConfigGesture().getMessages().get("Success.Add");
    private final String removeSuccess = Main.instance.getConfigGesture().getMessages().get("Success.Remove");
    private final String succesfullyRestored = Main.instance.getConfigGesture().getMessages().get("Success.Restored");

    private final String commandFooter = Main.instance.getConfigGesture().getMessages().get("Commands.Footer");
    private final String commandVcReloadHelp = Main.instance.getConfigGesture().getMessages().get("Commands.Reload");
    private final String commandVcNextHelp = Main.instance.getConfigGesture().getMessages().get("Commands.Next");
    private final String commandVcPointsHelp = Main.instance.getConfigGesture().getMessages().get("Commands.Points");
    private final String commandVcTopHelp = Main.instance.getConfigGesture().getMessages().get("Commands.Top");
    private final String commandVcClear = Main.instance.getConfigGesture().getMessages().get("Commands.Clear");
    private final String commandVcChallenge = Main.instance.getConfigGesture().getMessages().get("Commands.Challenge");
    private final String commandVcEconomyChallenge = Main.instance.getConfigGesture().getMessages().get("Commands.Economy");
    private final String commandVcReward = Main.instance.getConfigGesture().getMessages().get("Commands.Reward");
    private final String commandVcList = Main.instance.getConfigGesture().getMessages().get("Commands.List");
    private final String commandVcEvent = Main.instance.getConfigGesture().getMessages().get("Commands.Event");
    private final String commandVcSchedule = Main.instance.getConfigGesture().getMessages().get("Commands.Schedule");
    private final String commandVcRestore = Main.instance.getConfigGesture().getMessages().get("Commands.Restore");

    private final String fileList = Main.instance.getConfigGesture().getMessages().get("Lists.Files");
    private final String fileLine = Main.instance.getConfigGesture().getMessages().get("Lists.FilesLine");

    private final String pointsInfo = Main.instance.getConfigGesture().getMessages().get("Points.Info");
    private final String pointsPlayerPlaceholder = Main.instance.getConfigGesture().getMessages().get("Points.PlayerPlaceholder");
    private final String actuallyInTop = Main.instance.getConfigGesture().getMessages().get("ActuallyInTop");
    private final String pointsadd = Main.instance.getConfigGesture().getMessages().get("PointsAdd");
    private final String pointsremove = Main.instance.getConfigGesture().getMessages().get("PointsRemove");

    private final String challengeList = Main.instance.getConfigGesture().getMessages().get("ChallengeList");
    private final String challengeOfList = Main.instance.getConfigGesture().getMessages().get("ChallengeofList");

    private final boolean debugCommand = Main.instance.getConfigGesture().getDebug().get("Commands");
    private final boolean resetPoints = Main.instance.getConfigGesture().isResetPointsAtNewChallenge();

    private final int numberOfTop = Main.instance.getConfigGesture().getNumberOfTop();
    private final int numberOfPlayerRewarded = Main.instance.getConfigGesture().getNumberOfRewardPlayer();
    private final boolean rankingReward = Main.instance.getConfigGesture().isRankingReward();
    private final boolean randomReward = Main.instance.getConfigGesture().isRandomReward();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                DebugUtils debug = new DebugUtils("Commands");
                long tempo = System.currentTimeMillis();
                if (!Main.challengeSelected) {
                    sender.sendMessage(ChatColor.RED + "No DailyChallenge selected control the configurations files and restart the plugin!");
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                    return;
                }
                if (!command.getName().equalsIgnoreCase("vanillachallenges")) {// comando se esiste
                    sender.sendMessage(ColorUtils.applyColor(errorCommandNotFound));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args.length == 0) {// se non ha scritto args
                    String finale = "\n\n&e&l" + Main.instance.getName() + "&7 ● Version " + Main.instance.getDescription().getVersion()
                            + " created by &a&leliotesta98" + "\n&r\n";
                    finale = finale + commandVcEconomyChallenge + "\n";
                    finale = finale + commandVcChallenge + "\n";
                    finale = finale + commandVcClear + "\n";
                    finale = finale + commandVcEvent + "\n";
                    finale = finale + commandVcList + "\n";
                    finale = finale + commandVcNextHelp + "\n";
                    finale = finale + commandVcPointsHelp + "\n";
                    finale = finale + commandVcReloadHelp + "\n";
                    finale = finale + commandVcRestore + "\n";
                    finale = finale + commandVcReward + "\n";
                    finale = finale + commandVcTopHelp + "\n";
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("add")) {
                    if (args.length == 1 || args.length == 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcEconomyChallenge));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Main.instance.getDailyChallenge().incrementCommands(args[1], Long.parseLong(args[2]));
                    sender.sendMessage(ColorUtils.applyColor(pointsadd.replace("{points}", args[2]).replace("{player}", args[1])));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("event")) {
                    if (args.length != 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcEvent));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args[1].equalsIgnoreCase("stop")) {
                        if (!Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                            sender.sendMessage(ColorUtils.applyColor(alreadyStopEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {

                            List<Challenger> topPlayers = Main.instance.getDailyChallenge().getTopPlayers(numberOfPlayerRewarded);

                            Main.db.removeTopYesterday();
                            Main.db.saveTopYesterday(topPlayers);
                            if (Main.instance.getConfigGesture().isBackupEnabled()) {
                                Main.db.backupDb(Main.instance.getConfigGesture().getNumberOfFilesInFolderForBackup());
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
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    } else if (args[1].equalsIgnoreCase("random")) {
                        if (Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                            sender.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        Random random = new Random();
                        int sizeChallenges = random.nextInt(Main.instance.getConfigGesture().getChallengesEvent().size());
                        int i = 0;
                        Challenge challengeSelected = null;
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGesture().getChallengesEvent().entrySet()) {
                            if (i == sizeChallenges) {
                                challengeSelected = challenge.getValue();
                                break;
                            }
                            i++;
                        }
                        Challenge finalChallengeSelected = challengeSelected;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            Main.db.insertChallengeEvent(finalChallengeSelected.getChallengeName(),
                                    finalChallengeSelected.getTimeChallenge());
                            Main.db.saveOldPointsForChallengeEvents();
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    } else if (Main.instance.getConfigGesture().getChallengesEvent().get(args[1]) != null) {
                        if (Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                            sender.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            Main.db.insertChallengeEvent(args[1],
                                    Main.instance.getConfigGesture().getChallenges().get(args[1]).getTimeChallenge());
                            Main.db.saveOldPointsForChallengeEvents();
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    } else {
                        StringBuilder send = new StringBuilder("\n");
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGesture().getChallengesEvent().entrySet()) {
                            send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                        }
                        send.append("Random (Random Challenge)");
                        sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    }
                } else if (args[0].equalsIgnoreCase("restore")) {
                    if (args.length > 3) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcRestore));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
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
                        sender.sendMessage(ColorUtils.applyColor(finale.toString()));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
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
                    sender.sendMessage(ColorUtils.applyColor(succesfullyRestored));
                    ReloadUtils.reload();
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 1 || args.length == 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcEconomyChallenge));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Main.instance.getDailyChallenge().incrementCommands(args[1], -Long.parseLong(args[2]));
                    sender.sendMessage(ColorUtils.applyColor(pointsremove.replace("{points}", args[2]).replace("{player}", args[1])));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("challenge")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcChallenge));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Main.instance.getDailyChallenge().message(sender);
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("clear")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcClear));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        Main.db.clearAll();
                        ReloadUtils.reload();
                    });
                } else if (args[0].equalsIgnoreCase("next")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcNextHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }

                    if (Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                        sender.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        ChallengeChangeEvent challengeChangeEvent = new ChallengeChangeEvent("Command Execution", Main.instance.getDailyChallenge());
                        Bukkit.getPluginManager().callEvent(challengeChangeEvent);
                        if (challengeChangeEvent.isCancelled()) {
                            if (debugCommand) {
                                debug.addLine("Cancelled from ChallengeChangeEvent");
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }

                        List<Challenger> topPlayers = Main.instance.getDailyChallenge().getTopPlayers(numberOfPlayerRewarded);
                        Main.db.deleteChallengeWithName(Main.instance.getDailyChallenge().getChallengeName());
                        Main.db.removeTopYesterday();
                        Main.db.saveTopYesterday(topPlayers);
                        if (Main.instance.getDailyChallenge().getTypeChallenge().equalsIgnoreCase("ItemCollectionChallenge")) {
                            ItemCollector.deleteDb();
                        }
                        if (Main.instance.getConfigGesture().isBackupEnabled()) {
                            Main.db.backupDb(Main.instance.getConfigGesture().getNumberOfFilesInFolderForBackup());
                        }
                        int number = Main.db.lastDailyWinnerId();
                        if (Main.instance.getDailyChallenge().isMinimumPointsReached()) {
                            for (int i = 0; i < topPlayers.size(); i++) {
                                int placeInTop = i;
                                int rewardSize = Main.instance.getDailyChallenge().getRewards().size();
                                if (i >= rewardSize) {
                                    placeInTop = rewardSize - 1;
                                }
                                // Player Stat section
                                if (Main.db.isPlayerHaveStats(topPlayers.get(i).getNomePlayer())) {
                                    PlayerStats playerStats = Main.db.getStatsPlayer(topPlayers.get(i).getNomePlayer());
                                    playerStats.setNumberOfVictories(playerStats.getNumberOfVictories() + 1);
                                    if (i == 0) {
                                        playerStats.setNumberOfFirstPlace(playerStats.getNumberOfFirstPlace() + 1);
                                    } else if (i == 1) {
                                        playerStats.setNumberOfSecondPlace(playerStats.getNumberOfSecondPlace() + 1);
                                    } else if (i == 2) {
                                        playerStats.setNumberOfThirdPlace(playerStats.getNumberOfThirdPlace() + 1);
                                    }
                                    Main.db.updatePlayerStat(playerStats);
                                } else {
                                    PlayerStats playerStats = new PlayerStats();
                                    playerStats.setPlayerName(topPlayers.get(i).getNomePlayer());
                                    playerStats.setNumberOfVictories(1);
                                    if (i == 0) {
                                        playerStats.setNumberOfFirstPlace(1);
                                    } else if (i == 1) {
                                        playerStats.setNumberOfSecondPlace(1);
                                    } else if (i == 2) {
                                        playerStats.setNumberOfThirdPlace(1);
                                    }
                                    Main.db.insertPlayerStat(playerStats);
                                }

                                number++;
                                DailyWinner dailyWinner = new DailyWinner();
                                dailyWinner.setId(number);
                                dailyWinner.setPlayerName(topPlayers.get(i).getNomePlayer());
                                dailyWinner.setNomeChallenge(Main.instance.getDailyChallenge().getChallengeName());
                                if (Main.instance.getConfigGesture().isRankingReward()) {
                                    dailyWinner.setId(number);
                                    dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(placeInTop));
                                    Main.db.insertDailyWinner(dailyWinner);
                                } else {
                                    for (int x = 0; x < rewardSize; x++) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(x));
                                        Main.db.insertDailyWinner(dailyWinner);
                                        number++;
                                    }
                                }
                            }
                        }
                        if (resetPoints) {
                            Main.db.clearChallengers();
                            Main.instance.getDailyChallenge().clearPlayers();
                        }
                        ReloadUtils.reload();
                    });
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("schedule")) {
                    if (args.length != 3) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcSchedule));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Challenge challenge1 = Main.instance.getConfigGesture().getChallenges().get(args[2]);
                    if (args[1].equalsIgnoreCase("add")) {
                        if (challenge1 == null && !args[2].equalsIgnoreCase("random")) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGesture().getChallengesEvent().entrySet()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                            }
                            send.append("Random (Random Challenge)");
                            sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        } else {
                            if (args[2].equalsIgnoreCase("random")) {
                                List<Challenge> scheduledChallenges = Main.db.getChallenges();
                                ArrayList<Challenge> remainChallenges = new ArrayList<>();
                                boolean find;
                                for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGesture().getChallenges().entrySet()) {
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
                                    sender.sendMessage(ColorUtils.applyColor(addError));
                                    if (debugCommand) {
                                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                        debug.debug();
                                    }
                                    return;
                                } else {
                                    Collections.shuffle(remainChallenges);
                                    challenge1 = remainChallenges.get(0);
                                }
                            }
                            if (Main.db.isChallengePresent(challenge1.getChallengeName())) {
                                sender.sendMessage(ColorUtils.applyColor(addError));
                            } else {
                                Challenge finalChallenge = challenge1;
                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                                    Main.db.insertChallenge(finalChallenge.getChallengeName(), finalChallenge.getTimeChallenge());
                                    sender.sendMessage(ColorUtils.applyColor(addSuccess));
                                    ReloadUtils.reload();
                                });
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (challenge1 == null) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Challenge challenge : Main.db.getChallenges()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getChallengeName())).append("\n");
                            }
                            sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        } else {
                            if (Main.instance.getDailyChallenge().getChallengeName().equalsIgnoreCase(args[2])) {
                                sender.sendMessage(ColorUtils.applyColor(scheduleError));
                            } else {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                                    Main.db.deleteChallengeWithName(args[2]);
                                    sender.sendMessage(ColorUtils.applyColor(removeSuccess));
                                    ReloadUtils.reload();
                                });
                            }
                        }
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    String finale = "\n\n&e&l" + Main.instance.getName() + "&7 ● Version " + Main.instance.getDescription().getVersion()
                            + " created by &a&leliotesta98" + "\n&r\n";
                    finale = finale + commandVcEconomyChallenge + "\n";
                    finale = finale + commandVcChallenge + "\n";
                    finale = finale + commandVcClear + "\n";
                    finale = finale + commandVcEvent + "\n";
                    finale = finale + commandVcNextHelp + "\n";
                    finale = finale + commandVcPointsHelp + "\n";
                    finale = finale + commandVcReloadHelp + "\n";
                    finale = finale + commandVcRestore + "\n";
                    finale = finale + commandVcReward + "\n";
                    finale = finale + commandVcSchedule + "\n";
                    finale = finale + commandVcTopHelp + "\n";
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("points")) {
                    if (args.length > 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcReloadHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(ColorUtils.applyColor(errorYouAreNotAPlayer));
                    } else {
                        long points = Main.instance.getDailyChallenge().getPointFromPLayerName(args[1]);
                        sender.sendMessage(ColorUtils.applyColor(pointsInfo.replace("{player}", args[1]).replace("{number}", "" + points)));
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcReloadHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        sender.sendMessage(ColorUtils.applyColor("&6Reloading..."));
                        ReloadUtils.reload();
                        sender.sendMessage(ColorUtils.applyColor("&aReloaded!"));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    });
                } else if (args[0].equalsIgnoreCase("reward")) {
                    sender.sendMessage(ColorUtils.applyColor(errorYouAreNotAPlayer));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(ColorUtils.applyColor(errorYouAreNotAPlayer));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("top")) {
                    if (args.length > 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcTopHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    List<Challenger> top = new ArrayList<>();
                    if (args.length == 1) {
                        if (Main.instance.getConfigGesture().isYesterdayTop()) {
                            top = Main.db.getTopYesterday();
                        } else {
                            top = Main.instance.getDailyChallenge().getTopPlayers(numberOfTop);
                        }
                    } else if (args[1].equalsIgnoreCase("yesterday")) {
                        top = Main.db.getTopYesterday();
                    }
                    sender.sendMessage(ColorUtils.applyColor(actuallyInTop));
                    int i = 1;
                    while (!top.isEmpty()) {
                        sender.sendMessage(ColorUtils.applyColor(Main.instance.getConfigGesture().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", MoneyUtils.transform(top.get(0).getPoints()))));
                        top.remove(0);
                        i++;
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else {
                    String finale = "\n\n&e&l" + Main.instance.getName() + "&7 ● Version " + Main.instance.getDescription().getVersion()
                            + " created by &a&leliotesta98" + "\n&r\n";
                    finale = finale + commandVcEconomyChallenge + "\n";
                    finale = finale + commandVcChallenge + "\n";
                    finale = finale + commandVcClear + "\n";
                    finale = finale + commandVcEvent + "\n";
                    finale = finale + commandVcList + "\n";
                    finale = finale + commandVcNextHelp + "\n";
                    finale = finale + commandVcPointsHelp + "\n";
                    finale = finale + commandVcReloadHelp + "\n";
                    finale = finale + commandVcRestore + "\n";
                    finale = finale + commandVcReward + "\n";
                    finale = finale + commandVcSchedule + "\n";
                    finale = finale + commandVcTopHelp + "\n";
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                }
            });
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                final Player p = (Player) sender;
                DebugUtils debug = new DebugUtils("Commands");
                long tempo = System.currentTimeMillis();
                if (!Main.challengeSelected) {
                    p.sendMessage(ChatColor.RED + "No DailyChallenge selected control the configurations files and restart the plugin!");
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                    return;
                }
                if (!command.getName().equalsIgnoreCase("vanillachallenges")) {// comando se esiste
                    p.sendMessage(ColorUtils.applyColor(errorCommandNotFound));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args.length == 0) {
                    String finale = "\n\n&e&l" + Main.instance.getName() + "&7 ● Version " + Main.instance.getDescription().getVersion()
                            + " created by &a&leliotesta98" + "\n&r\n";
                    if (p.hasPermission("vc.challenge.command")) {
                        finale = finale + commandVcChallenge + "\n";
                    }
                    if (p.hasPermission("vc.clear.command")) {
                        finale = finale + commandVcClear + "\n";
                    }
                    if (p.hasPermission("vc.event.start.command") || p.hasPermission("vc.event.stop.command")) {
                        finale = finale + commandVcEvent + "\n";
                    }
                    if (p.hasPermission("vc.list.command")) {
                        finale = finale + commandVcList + "\n";
                    }
                    if (p.hasPermission("vc.next.command")) {
                        finale = finale + commandVcNextHelp + "\n";
                    }
                    if (p.hasPermission("vc.points.command")) {
                        finale = finale + commandVcPointsHelp + "\n";
                    }
                    if (p.hasPermission("vc.reload.command")) {
                        finale = finale + commandVcReloadHelp + "\n";
                    }
                    if (p.hasPermission("vc.reward.command")) {
                        finale = finale + commandVcReward + "\n";
                    }
                    if (p.hasPermission("vc.schedule.add.command") || p.hasPermission("vc.schedule.remove.command")) {
                        finale = finale + commandVcSchedule + "\n";
                    }
                    if (p.hasPermission("vc.top.command")) {
                        finale = finale + commandVcTopHelp + "\n";
                    }
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    p.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("challenge")) {
                    if (!p.hasPermission("vc.challenge.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcChallenge));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Main.instance.getDailyChallenge().message(sender);
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("clear")) {
                    if (!p.hasPermission("vc.clear.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcClear));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        Main.db.clearAll();
                        ReloadUtils.reload();
                    });
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (!p.hasPermission("vc.list.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcList));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        int size = Main.instance.getConfigGesture().getInterfaces().get("Challenges").getSizeModificableSlot();
                        ArrayList<Challenge> challenges = new ArrayList<>();
                        for (Challenge challenge : Main.db.getChallenges()) {
                            if (challenges.size() < size) {
                                challenges.add(challenge);
                            }
                        }
                        Main.instance.getConfigGesture().getInterfaces().get("Challenges").openInterface(
                                challenges, p, 1);
                    });
                } else if (args[0].equalsIgnoreCase("next")) {
                    if (!p.hasPermission("vc.next.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcNextHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                        sender.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        ChallengeChangeEvent challengeChangeEvent = new ChallengeChangeEvent("Command Execution", Main.instance.getDailyChallenge());
                        Bukkit.getPluginManager().callEvent(challengeChangeEvent);
                        if (challengeChangeEvent.isCancelled()) {
                            if (debugCommand) {
                                debug.addLine("Cancelled from ChallengeChangeEvent");
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        List<Challenger> topPlayers = Main.instance.getDailyChallenge().getTopPlayers(numberOfPlayerRewarded);
                        Main.db.deleteChallengeWithName(Main.instance.getDailyChallenge().getChallengeName());
                        Main.db.removeTopYesterday();
                        Main.db.saveTopYesterday(topPlayers);
                        if (Main.instance.getDailyChallenge().getTypeChallenge().equalsIgnoreCase("ItemCollectionChallenge")) {
                            ItemCollector.deleteDb();
                        }
                        if (Main.instance.getConfigGesture().isBackupEnabled()) {
                            Main.db.backupDb(Main.instance.getConfigGesture().getNumberOfFilesInFolderForBackup());
                        }
                        int number = Main.db.lastDailyWinnerId();
                        if (Main.instance.getDailyChallenge().isMinimumPointsReached()) {
                            for (int i = 0; i < topPlayers.size(); i++) {
                                int placeInTop = i;
                                if (i >= Main.instance.getDailyChallenge().getRewards().size()) {
                                    placeInTop = Main.instance.getDailyChallenge().getRewards().size() - 1;
                                }

                                // Player Stat section
                                if (Main.db.isPlayerHaveStats(topPlayers.get(i).getNomePlayer())) {
                                    PlayerStats playerStats = Main.db.getStatsPlayer(topPlayers.get(i).getNomePlayer());
                                    playerStats.setNumberOfVictories(playerStats.getNumberOfVictories() + 1);
                                    if (i == 0) {
                                        playerStats.setNumberOfFirstPlace(playerStats.getNumberOfFirstPlace() + 1);
                                    } else if (i == 1) {
                                        playerStats.setNumberOfSecondPlace(playerStats.getNumberOfSecondPlace() + 1);
                                    } else if (i == 2) {
                                        playerStats.setNumberOfThirdPlace(playerStats.getNumberOfThirdPlace() + 1);
                                    }
                                    Main.db.updatePlayerStat(playerStats);
                                } else {
                                    PlayerStats playerStats = new PlayerStats();
                                    playerStats.setPlayerName(topPlayers.get(i).getNomePlayer());
                                    playerStats.setNumberOfVictories(1);
                                    if (i == 0) {
                                        playerStats.setNumberOfFirstPlace(1);
                                    } else if (i == 1) {
                                        playerStats.setNumberOfSecondPlace(1);
                                    } else if (i == 2) {
                                        playerStats.setNumberOfThirdPlace(1);
                                    }
                                    Main.db.insertPlayerStat(playerStats);
                                }

                                number++;
                                DailyWinner dailyWinner = new DailyWinner();
                                dailyWinner.setId(number);
                                dailyWinner.setPlayerName(topPlayers.get(i).getNomePlayer());
                                dailyWinner.setNomeChallenge(Main.instance.getDailyChallenge().getChallengeName());
                                if (Main.instance.getConfigGesture().isRankingReward()) {
                                    dailyWinner.setId(number);
                                    dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(placeInTop));
                                    Main.db.insertDailyWinner(dailyWinner);
                                } else {
                                    for (int x = 0; x < Main.instance.getDailyChallenge().getRewards().size(); x++) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.instance.getDailyChallenge().getRewards().get(x));
                                        Main.db.insertDailyWinner(dailyWinner);
                                        number++;
                                    }
                                }
                            }
                        }
                        if (resetPoints) {
                            Main.db.clearChallengers();
                            Main.instance.getDailyChallenge().clearPlayers();
                        }
                        ReloadUtils.reload();
                    });
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("schedule")) {
                    if (args.length != 3) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcSchedule));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Challenge challenge1 = Main.instance.getConfigGesture().getChallenges().get(args[2]);
                    if (args[1].equalsIgnoreCase("add")) {
                        if (!p.hasPermission("vc.schedule.add.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        if (challenge1 == null && !args[2].equalsIgnoreCase("random")) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGesture().getChallengesEvent().entrySet()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                            }
                            sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        } else {
                            if (args[2].equalsIgnoreCase("random")) {
                                List<Challenge> scheduledChallenges = Main.db.getChallenges();
                                ArrayList<Challenge> remainChallenges = new ArrayList<>();
                                boolean find;
                                for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGesture().getChallenges().entrySet()) {
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
                                    sender.sendMessage(ColorUtils.applyColor(addError));
                                    if (debugCommand) {
                                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                        debug.debug();
                                    }
                                    return;
                                } else {
                                    Collections.shuffle(remainChallenges);
                                    challenge1 = remainChallenges.get(0);
                                }
                            }
                            if (Main.db.isChallengePresent(challenge1.getChallengeName())) {
                                sender.sendMessage(ColorUtils.applyColor(addError));
                            } else {
                                Challenge finalChallenge = challenge1;
                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                                    Main.db.insertChallenge(finalChallenge.getChallengeName(), finalChallenge.getTimeChallenge());
                                    sender.sendMessage(ColorUtils.applyColor(addSuccess));
                                    ReloadUtils.reload();
                                });
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (!p.hasPermission("vc.schedule.remove.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        if (challenge1 == null) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Challenge challenge : Main.db.getChallenges()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getChallengeName())).append("\n");
                            }
                            p.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        } else {
                            if (Main.instance.getDailyChallenge().getChallengeName().equalsIgnoreCase(args[2])) {
                                p.sendMessage(ColorUtils.applyColor(scheduleError));
                            } else {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                                    Main.db.deleteChallengeWithName(args[2]);
                                    p.sendMessage(ColorUtils.applyColor(removeSuccess));
                                    ReloadUtils.reload();
                                });
                            }
                        }
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (!p.hasPermission("vc.help.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    String finale = "\n\n&e&l" + Main.instance.getName() + "&7 ● Version " + Main.instance.getDescription().getVersion()
                            + " created by &a&leliotesta98" + "\n&r\n";
                    if (p.hasPermission("vc.challenge.command")) {
                        finale = finale + commandVcChallenge + "\n";
                    }
                    if (p.hasPermission("vc.clear.command")) {
                        finale = finale + commandVcClear + "\n";
                    }
                    if (p.hasPermission("vc.event.start.command") || p.hasPermission("vc.event.stop.command")) {
                        finale = finale + commandVcEvent + "\n";
                    }
                    if (p.hasPermission("vc.list.command")) {
                        finale = finale + commandVcList + "\n";
                    }
                    if (p.hasPermission("vc.next.command")) {
                        finale = finale + commandVcNextHelp + "\n";
                    }
                    if (p.hasPermission("vc.points.command")) {
                        finale = finale + commandVcPointsHelp + "\n";
                    }
                    if (p.hasPermission("vc.reload.command")) {
                        finale = finale + commandVcReloadHelp + "\n";
                    }
                    if (p.hasPermission("vc.reward.command")) {
                        finale = finale + commandVcReward + "\n";
                    }
                    if (p.hasPermission("vc.schedule.add.command") || p.hasPermission("vc.schedule.remove.command")) {
                        finale = finale + commandVcSchedule + "\n";
                    }
                    if (p.hasPermission("vc.top.command")) {
                        finale = finale + commandVcTopHelp + "\n";
                    }
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    p.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("points")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("vc.points.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length > 2) {
                        p.sendMessage(ColorUtils.applyColor(commandVcPointsHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length == 1) {
                        long points = Main.instance.getDailyChallenge().getPointFromPLayerName(p.getName());
                        p.sendMessage(ColorUtils.applyColor(pointsInfo.replace("{player}", pointsPlayerPlaceholder).replace("{number}", "" + points)));
                    } else {
                        if (!p.hasPermission("vc.points.admin.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        long points = Main.instance.getDailyChallenge().getPointFromPLayerName(args[1]);
                        p.sendMessage(ColorUtils.applyColor(pointsInfo.replace("{player}", args[1]).replace("{number}", "" + points)));
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("event")) {
                    if (args.length != 2) {
                        p.sendMessage(ColorUtils.applyColor(commandVcEvent));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args[1].equalsIgnoreCase("stop")) {
                        if (!p.hasPermission("vc.event.stop.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        if (!Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                            p.sendMessage(ColorUtils.applyColor(alreadyStopEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {

                            List<Challenger> topPlayers = Main.instance.getDailyChallenge().getTopPlayers(numberOfPlayerRewarded);

                            Main.db.removeTopYesterday();
                            Main.db.saveTopYesterday(topPlayers);
                            if (Main.instance.getConfigGesture().isBackupEnabled()) {
                                Main.db.backupDb(Main.instance.getConfigGesture().getNumberOfFilesInFolderForBackup());
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
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    } else if (args[1].equalsIgnoreCase("random")) {
                        if (Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                            p.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        Random random = new Random();
                        int sizeChallenges = random.nextInt(Main.instance.getConfigGesture().getChallengesEvent().size());
                        int i = 0;
                        Challenge challengeSelected = null;
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGesture().getChallengesEvent().entrySet()) {
                            if (i == sizeChallenges) {
                                challengeSelected = challenge.getValue();
                                break;
                            }
                            i++;
                        }
                        Challenge finalChallengeSelected = challengeSelected;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            Main.db.insertChallengeEvent(finalChallengeSelected.getChallengeName(),
                                    finalChallengeSelected.getTimeChallenge());
                            Main.db.saveOldPointsForChallengeEvents();
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    } else if (Main.instance.getConfigGesture().getChallengesEvent().get(args[1]) != null) {
                        if (!p.hasPermission("vc.event.start.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        if (Main.db.getChallenges().get(0).getChallengeName().contains("Event_")) {
                            p.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            Main.db.insertChallengeEvent(args[1],
                                    Main.instance.getConfigGesture().getChallenges().get(args[1]).getTimeChallenge());
                            Main.db.saveOldPointsForChallengeEvents();
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    } else {
                        if (!p.hasPermission("vc.event.start.command") || !p.hasPermission("vc.event.stop.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        StringBuilder send = new StringBuilder("\n");
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGesture().getChallengesEvent().entrySet()) {
                            send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                        }
                        p.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reward")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("vc.reward.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length > 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcReward));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    List<DailyWinner> winners = Main.db.getDailyWinners();
                    if (winners.isEmpty()) {
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    } else {
                        DailyGiveWinners.getRewardsAtPlayers(p, winners);
                    }
                } else if (args[0].equalsIgnoreCase("top")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("vc.top.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length > 2) {
                        p.sendMessage(ColorUtils.applyColor(commandVcTopHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    List<Challenger> top = new ArrayList<>();
                    if (args.length == 1) {
                        if (Main.instance.getConfigGesture().isYesterdayTop()) {
                            top = Main.db.getTopYesterday();
                        } else {
                            top = Main.instance.getDailyChallenge().getTopPlayers(numberOfTop);
                        }
                    } else if (args[1].equalsIgnoreCase("yesterday")) {
                        if (!p.hasPermission("vc.top.yesterday.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug();
                            }
                            return;
                        }
                        top = Main.db.getTopYesterday();
                    }
                    p.sendMessage(ColorUtils.applyColor(actuallyInTop));
                    int i = 1;
                    while (!top.isEmpty()) {
                        p.sendMessage(ColorUtils.applyColor(Main.instance.getConfigGesture().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", MoneyUtils.transform(top.get(0).getPoints()))));
                        top.remove(0);
                        i++;
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("vc.reload.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcReloadHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        sender.sendMessage(ColorUtils.applyColor("&6Reloading..."));
                        ReloadUtils.reload();
                        sender.sendMessage(ColorUtils.applyColor("&aReloaded!"));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug();
                        }
                    });
                } else {
                    String finale = "\n\n&e&l" + Main.instance.getName() + "&7 ● Version " + Main.instance.getDescription().getVersion()
                            + " created by &a&leliotesta98" + "\n&r\n";
                    if (p.hasPermission("vc.challenge.command")) {
                        finale = finale + commandVcChallenge + "\n";
                    }
                    if (p.hasPermission("vc.clear.command")) {
                        finale = finale + commandVcClear + "\n";
                    }
                    if (p.hasPermission("vc.event.start.command") || p.hasPermission("vc.event.stop.command")) {
                        finale = finale + commandVcEvent + "\n";
                    }
                    if (p.hasPermission("vc.list.command")) {
                        finale = finale + commandVcList + "\n";
                    }
                    if (p.hasPermission("vc.next.command")) {
                        finale = finale + commandVcNextHelp + "\n";
                    }
                    if (p.hasPermission("vc.points.command")) {
                        finale = finale + commandVcPointsHelp + "\n";
                    }
                    if (p.hasPermission("vc.reload.command")) {
                        finale = finale + commandVcReloadHelp + "\n";
                    }
                    if (p.hasPermission("vc.reward.command")) {
                        finale = finale + commandVcReward + "\n";
                    }
                    if (p.hasPermission("vc.schedule.add.command") || p.hasPermission("vc.schedule.remove.command")) {
                        finale = finale + commandVcSchedule + "\n";
                    }
                    if (p.hasPermission("vc.top.command")) {
                        finale = finale + commandVcTopHelp + "\n";
                    }
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    p.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug();
                    }
                }
            });
        }
        return false;
    }
}
