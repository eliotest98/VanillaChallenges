package io.eliotesta98.VanillaChallenges.Comandi;

import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import io.eliotesta98.VanillaChallenges.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.eliotesta98.VanillaChallenges.Core.Main;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Commands implements CommandExecutor {

    private final String errorYouAreNotAPlayer = Main.instance.getConfigGestion().getMessages().get("Errors.YouAreNotAPlayer");
    private final String errorCommandNotFound = Main.instance.getConfigGestion().getMessages().get("Errors.CommandNotFound");
    private final String errorNoPerms = Main.instance.getConfigGestion().getMessages().get("Errors.NoPerms");
    private final String alreadyStartEvent = Main.instance.getConfigGestion().getMessages().get("Errors.AlreadyStartEvent");
    private final String alreadyStopEvent = Main.instance.getConfigGestion().getMessages().get("Errors.AlreadyStopEvent");
    private final String scheduleError = Main.instance.getConfigGestion().getMessages().get("Errors.Schedule");
    private final String addError = Main.instance.getConfigGestion().getMessages().get("Errors.Add");

    private final String addSuccess = Main.instance.getConfigGestion().getMessages().get("Success.Add");
    private final String removeSuccess = Main.instance.getConfigGestion().getMessages().get("Success.Remove");

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

    private final String pointsInfo = Main.instance.getConfigGestion().getMessages().get("PointsInfo");
    private final String actuallyInTop = Main.instance.getConfigGestion().getMessages().get("ActuallyInTop");
    private final String pointsadd = Main.instance.getConfigGestion().getMessages().get("PointsAdd");
    private final String pointsremove = Main.instance.getConfigGestion().getMessages().get("PointsRemove");

    private final String challengeList = Main.instance.getConfigGestion().getMessages().get("ChallengeList");
    private final String challengeOfList = Main.instance.getConfigGestion().getMessages().get("ChallengeofList");

    private final boolean debugCommand = Main.instance.getConfigGestion().getDebug().get("Commands");
    private final boolean resetPoints = Main.instance.getConfigGestion().isResetPointsAtNewChallenge();
    private final String challengeReward = Main.instance.getConfigGestion().getMessages().get("ChallengeReward");

    private final int numberOfTop = Main.instance.getConfigGestion().getNumberOfTop();
    private final boolean rankingReward = Main.instance.getConfigGestion().isRankingReward();
    private final boolean randomReward = Main.instance.getConfigGestion().isRandomReward();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                DebugUtils debug = new DebugUtils();
                long tempo = System.currentTimeMillis();
                if (!Main.challengeSelected) {
                    sender.sendMessage(ChatColor.RED + "No DailyChallenge selected control the configurations files and restart the plugin!");
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                    return;
                }
                if (!command.getName().equalsIgnoreCase("vc")) {// comando se esiste
                    sender.sendMessage(ColorUtils.applyColor(errorCommandNotFound));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
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
                    finale = finale + commandVcReward + "\n";
                    finale = finale + commandVcTopHelp + "\n";
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("add")) {
                    if (args.length == 1 || args.length == 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcEconomyChallenge));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Main.dailyChallenge.incrementCommands(args[1], Long.parseLong(args[2]));
                    sender.sendMessage(ColorUtils.applyColor(pointsadd.replace("{points}", args[2]).replace("{player}", args[1])));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("event")) {
                    if (args.length != 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcEvent));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args[1].equalsIgnoreCase("stop")) {
                        if (!Main.db.getAllChallenges().get(0).getChallengeName().contains("Event_")) {
                            sender.sendMessage(ColorUtils.applyColor(alreadyStopEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {

                            ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(numberOfTop);

                            Main.db.removeTopYesterday();
                            Main.db.saveTopYesterday(topPlayers);
                            if (Main.instance.getConfigGestion().isBackupEnabled()) {
                                Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                            }
                            int number = Main.db.lastDailyWinnerId();
                            Random random = new Random();
                            if (Main.dailyChallenge.isMinimumPointsReached()) {
                                for (int z = 0; z < topPlayers.size(); z++) {
                                    int placeInTop = z;
                                    int rewardsSize = Main.dailyChallenge.getRewards().size();
                                    if (z >= rewardsSize) {
                                        placeInTop = rewardsSize - 1;
                                    }
                                    number++;
                                    DailyWinner dailyWinner = new DailyWinner();
                                    dailyWinner.setPlayerName(topPlayers.get(z).getNomePlayer());
                                    dailyWinner.setNomeChallenge(Main.dailyChallenge.getChallengeName());
                                    if (rankingReward) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.dailyChallenge.getRewards().get(placeInTop));
                                        Main.db.insertDailyWinner(dailyWinner);
                                    } else {
                                        if (randomReward) {
                                            dailyWinner.setId(number);
                                            dailyWinner.setReward(Main.dailyChallenge.getRewards().get(random.nextInt(rewardsSize)));
                                            Main.db.insertDailyWinner(dailyWinner);
                                        } else {
                                            for (int i = 0; i < rewardsSize; i++) {
                                                dailyWinner.setId(number);
                                                dailyWinner.setReward(Main.dailyChallenge.getRewards().get(i));
                                                Main.db.insertDailyWinner(dailyWinner);
                                                number++;
                                            }
                                        }
                                    }
                                }
                            }
                            Main.db.deleteChallengeWithName(Main.db.getAllChallenges().get(0).getChallengeName());
                            Main.db.resumeOldPoints();
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    } else if (args[1].equalsIgnoreCase("random")) {
                        if (Main.db.getAllChallenges().get(0).getChallengeName().contains("Event_")) {
                            sender.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Random random = new Random();
                        int sizeChallenges = random.nextInt(Main.instance.getConfigGestion().getChallengesEvent().size());
                        int i = 0;
                        Challenge challengeSelected = null;
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
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
                            debug.debug("Commands");
                        }
                    } else if (Main.instance.getConfigGestion().getChallengesEvent().get(args[1]) != null) {
                        if (Main.db.getAllChallenges().get(0).getChallengeName().contains("Event_")) {
                            sender.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            Main.db.insertChallengeEvent(args[1],
                                    Main.instance.getConfigGestion().getChallenges().get(args[1]).getTimeChallenge());
                            Main.db.saveOldPointsForChallengeEvents();
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    } else {
                        StringBuilder send = new StringBuilder("\n");
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
                            send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                        }
                        send.append("Random (Random Challenge)");
                        sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 1 || args.length == 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcEconomyChallenge));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Main.dailyChallenge.incrementCommands(args[1], -Long.parseLong(args[2]));
                    sender.sendMessage(ColorUtils.applyColor(pointsremove.replace("{points}", args[2]).replace("{player}", args[1])));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("challenge")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcChallenge));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Main.dailyChallenge.message(sender);
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("clear")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcClear));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
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
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (Main.db.getAllChallenges().get(0).getChallengeName().contains("Event_")) {
                        sender.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {

                        ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(Main.instance.getConfigGestion().getNumberOfTop());
                        Main.db.deleteChallengeWithName(Main.dailyChallenge.getChallengeName());
                        Main.db.removeTopYesterday();
                        Main.db.saveTopYesterday(topPlayers);
                        if (Main.instance.getConfigGestion().isBackupEnabled()) {
                            Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                        }
                        int number = Main.db.lastDailyWinnerId();
                        if (Main.dailyChallenge.isMinimumPointsReached()) {
                            for (int i = 0; i < topPlayers.size(); i++) {
                                int placeInTop = i;
                                if (i >= Main.dailyChallenge.getRewards().size()) {
                                    placeInTop = Main.dailyChallenge.getRewards().size() - 1;
                                }
                                number++;
                                DailyWinner dailyWinner = new DailyWinner();
                                dailyWinner.setId(number);
                                dailyWinner.setPlayerName(topPlayers.get(i).getNomePlayer());
                                dailyWinner.setNomeChallenge(Main.dailyChallenge.getChallengeName());
                                if (Main.instance.getConfigGestion().isRankingReward()) {
                                    dailyWinner.setId(number);
                                    dailyWinner.setReward(Main.dailyChallenge.getRewards().get(placeInTop));
                                    Main.db.insertDailyWinner(dailyWinner);
                                } else {
                                    for (int x = 0; x < Main.dailyChallenge.getRewards().size(); x++) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.dailyChallenge.getRewards().get(x));
                                        Main.db.insertDailyWinner(dailyWinner);
                                        number++;
                                    }
                                }
                            }
                        }
                        if (resetPoints) {
                            Main.db.clearChallengers();
                            Main.dailyChallenge.clearPlayers();
                        }
                        ReloadUtils.reload();
                    });
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("schedule")) {
                    if (args.length != 3) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcSchedule));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Challenge challenge1 = Main.instance.getConfigGestion().getChallenges().get(args[2]);
                    if (args[1].equalsIgnoreCase("add")) {
                        if (challenge1 == null && !args[2].equalsIgnoreCase("random")) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                            }
                            send.append("Random (Random Challenge)");
                            sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        } else {
                            if (args[2].equalsIgnoreCase("random")) {
                                ArrayList<Challenge> scheduledChallenges = Main.db.getAllChallenges();
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
                                    sender.sendMessage(ColorUtils.applyColor(addError));
                                    if (debugCommand) {
                                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                        debug.debug("Commands");
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
                            for (Challenge challenge : Main.db.getAllChallenges()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getChallengeName())).append("\n");
                            }
                            sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        } else {
                            if (Main.dailyChallenge.getChallengeName().equalsIgnoreCase(args[2])) {
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
                        debug.debug("Commands");
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
                    finale = finale + commandVcReward + "\n";
                    finale = finale + commandVcSchedule + "\n";
                    finale = finale + commandVcTopHelp + "\n";
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("points")) {
                    if (args.length > 2) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcReloadHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
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
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcReloadHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        sender.sendMessage(ColorUtils.applyColor("&6Reloading..."));
                        ReloadUtils.reload();
                        sender.sendMessage(ColorUtils.applyColor("&aReloaded!"));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    });
                } else if (args[0].equalsIgnoreCase("reward")) {
                    sender.sendMessage(ColorUtils.applyColor(errorYouAreNotAPlayer));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(ColorUtils.applyColor(errorYouAreNotAPlayer));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("top")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcTopHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    sender.sendMessage(ColorUtils.applyColor(actuallyInTop));
                    ArrayList<Challenger> top;
                    if (!Main.instance.getConfigGestion().isYesterdayTop()) {
                        top = Main.dailyChallenge.getTopPlayers(Main.instance.getConfigGestion().getNumberOfTop());
                    } else {
                        top = Main.db.getAllChallengersTopYesterday();
                    }
                    int i = 1;
                    while (!top.isEmpty()) {
                        sender.sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + MoneyUtils.transform(top.get(0).getPoints()))));
                        top.remove(0);
                        i++;
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
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
                    finale = finale + commandVcReward + "\n";
                    finale = finale + commandVcSchedule + "\n";
                    finale = finale + commandVcTopHelp + "\n";
                    finale = finale + "&r\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
            });
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                final Player p = (Player) sender;
                DebugUtils debug = new DebugUtils();
                long tempo = System.currentTimeMillis();
                if (!Main.challengeSelected) {
                    p.sendMessage(ChatColor.RED + "No DailyChallenge selected control the configurations files and restart the plugin!");
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                    return;
                }
                if (!command.getName().equalsIgnoreCase("vc")) {// comando se esiste
                    p.sendMessage(ColorUtils.applyColor(errorCommandNotFound));
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
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
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("challenge")) {
                    if (!p.hasPermission("vc.challenge.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcChallenge));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Main.dailyChallenge.message(sender);
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("clear")) {
                    if (!p.hasPermission("vc.clear.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcClear));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
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
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcList));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        int size = Main.instance.getConfigGestion().getInterfaces().get("Challenges").getSizeModificableSlot();
                        ArrayList<Challenge> challenges = new ArrayList<>();
                        for (Challenge challenge : Main.db.getAllChallenges()) {
                            if (challenges.size() < size) {
                                challenges.add(challenge);
                            }
                        }
                        Main.instance.getConfigGestion().getInterfaces().get("Challenges").openInterface(
                                challenges, p, 1);
                    });
                } else if (args[0].equalsIgnoreCase("next")) {
                    if (!p.hasPermission("vc.next.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcNextHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (Main.db.getAllChallenges().get(0).getChallengeName().contains("Event_")) {
                        sender.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {

                        ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(Main.instance.getConfigGestion().getNumberOfTop());
                        Main.db.deleteChallengeWithName(Main.dailyChallenge.getChallengeName());
                        Main.db.removeTopYesterday();
                        Main.db.saveTopYesterday(topPlayers);
                        if (Main.instance.getConfigGestion().isBackupEnabled()) {
                            Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                        }
                        int number = Main.db.lastDailyWinnerId();
                        if (Main.dailyChallenge.isMinimumPointsReached()) {
                            for (int i = 0; i < topPlayers.size(); i++) {
                                int placeInTop = i;
                                if (i >= Main.dailyChallenge.getRewards().size()) {
                                    placeInTop = Main.dailyChallenge.getRewards().size() - 1;
                                }
                                number++;
                                DailyWinner dailyWinner = new DailyWinner();
                                dailyWinner.setId(number);
                                dailyWinner.setPlayerName(topPlayers.get(i).getNomePlayer());
                                dailyWinner.setNomeChallenge(Main.dailyChallenge.getChallengeName());
                                if (Main.instance.getConfigGestion().isRankingReward()) {
                                    dailyWinner.setId(number);
                                    dailyWinner.setReward(Main.dailyChallenge.getRewards().get(placeInTop));
                                    Main.db.insertDailyWinner(dailyWinner);
                                } else {
                                    for (int x = 0; x < Main.dailyChallenge.getRewards().size(); x++) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.dailyChallenge.getRewards().get(x));
                                        Main.db.insertDailyWinner(dailyWinner);
                                        number++;
                                    }
                                }
                            }
                        }
                        if (resetPoints) {
                            Main.db.clearChallengers();
                            Main.dailyChallenge.clearPlayers();
                        }
                        ReloadUtils.reload();
                    });
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("schedule")) {
                    if (args.length != 3) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcSchedule));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Challenge challenge1 = Main.instance.getConfigGestion().getChallenges().get(args[2]);
                    if (args[1].equalsIgnoreCase("add")) {
                        if (!p.hasPermission("vc.schedule.add.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (challenge1 == null && !args[2].equalsIgnoreCase("random")) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                            }
                            sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        } else {
                            if (args[2].equalsIgnoreCase("random")) {
                                ArrayList<Challenge> scheduledChallenges = Main.db.getAllChallenges();
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
                                    sender.sendMessage(ColorUtils.applyColor(addError));
                                    if (debugCommand) {
                                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                        debug.debug("Commands");
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
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (challenge1 == null) {
                            StringBuilder send = new StringBuilder("\n");
                            for (Challenge challenge : Main.db.getAllChallenges()) {
                                send.append(challengeOfList.replace("{challenge}", challenge.getChallengeName())).append("\n");
                            }
                            sender.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        } else {
                            if (Main.dailyChallenge.getChallengeName().equalsIgnoreCase(args[2])) {
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
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (!p.hasPermission("vc.help.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
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
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("points")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("vc.points.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length > 2) {
                        p.sendMessage(ColorUtils.applyColor(commandVcPointsHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length == 1) {
                        long points = Main.instance.getDailyChallenge().getPointFromPLayerName(p.getName());
                        p.sendMessage(ColorUtils.applyColor(pointsInfo.replace("{player}", "You").replace("{number}", "" + points)));
                    } else {
                        if (!p.hasPermission("vc.points.admin.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        long points = Main.instance.getDailyChallenge().getPointFromPLayerName(args[1]);
                        p.sendMessage(ColorUtils.applyColor(pointsInfo.replace("{player}", args[1]).replace("{number}", "" + points)));
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("event")) {
                    if (args.length != 2) {
                        p.sendMessage(ColorUtils.applyColor(commandVcEvent));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args[1].equalsIgnoreCase("stop")) {
                        if (!p.hasPermission("vc.event.stop.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (!Main.db.getAllChallenges().get(0).getChallengeName().contains("Event_")) {
                            p.sendMessage(ColorUtils.applyColor(alreadyStopEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {

                            ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(numberOfTop);

                            Main.db.removeTopYesterday();
                            Main.db.saveTopYesterday(topPlayers);
                            if (Main.instance.getConfigGestion().isBackupEnabled()) {
                                Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                            }
                            int number = Main.db.lastDailyWinnerId();
                            Random random = new Random();
                            if (Main.dailyChallenge.isMinimumPointsReached()) {
                                for (int z = 0; z < topPlayers.size(); z++) {
                                    int placeInTop = z;
                                    int rewardsSize = Main.dailyChallenge.getRewards().size();
                                    if (z >= rewardsSize) {
                                        placeInTop = rewardsSize - 1;
                                    }
                                    number++;
                                    DailyWinner dailyWinner = new DailyWinner();
                                    dailyWinner.setPlayerName(topPlayers.get(z).getNomePlayer());
                                    dailyWinner.setNomeChallenge(Main.dailyChallenge.getChallengeName());
                                    if (rankingReward) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.dailyChallenge.getRewards().get(placeInTop));
                                        Main.db.insertDailyWinner(dailyWinner);
                                    } else {
                                        if (randomReward) {
                                            dailyWinner.setId(number);
                                            dailyWinner.setReward(Main.dailyChallenge.getRewards().get(random.nextInt(rewardsSize)));
                                            Main.db.insertDailyWinner(dailyWinner);
                                        } else {
                                            for (int i = 0; i < rewardsSize; i++) {
                                                dailyWinner.setId(number);
                                                dailyWinner.setReward(Main.dailyChallenge.getRewards().get(i));
                                                Main.db.insertDailyWinner(dailyWinner);
                                                number++;
                                            }
                                        }
                                    }
                                }
                            }
                            Main.db.deleteChallengeWithName(Main.db.getAllChallenges().get(0).getChallengeName());
                            Main.db.resumeOldPoints();
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    } else if (args[1].equalsIgnoreCase("random")) {
                        if (Main.db.getAllChallenges().get(0).getChallengeName().contains("Event_")) {
                            p.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Random random = new Random();
                        int sizeChallenges = random.nextInt(Main.instance.getConfigGestion().getChallengesEvent().size());
                        int i = 0;
                        Challenge challengeSelected = null;
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
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
                            debug.debug("Commands");
                        }
                    } else if (Main.instance.getConfigGestion().getChallengesEvent().get(args[1]) != null) {
                        if (!p.hasPermission("vc.event.start.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (Main.db.getAllChallenges().get(0).getChallengeName().contains("Event_")) {
                            p.sendMessage(ColorUtils.applyColor(alreadyStartEvent));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                            Main.db.insertChallengeEvent(args[1],
                                    Main.instance.getConfigGestion().getChallenges().get(args[1]).getTimeChallenge());
                            Main.db.saveOldPointsForChallengeEvents();
                            ReloadUtils.reload();
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    } else {
                        if (!p.hasPermission("vc.event.start.command") || !p.hasPermission("vc.event.stop.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        StringBuilder send = new StringBuilder("\n");
                        for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallengesEvent().entrySet()) {
                            send.append(challengeOfList.replace("{challenge}", challenge.getKey())).append("\n");
                        }
                        p.sendMessage(ColorUtils.applyColor(challengeList.replace("{challengeList}", send.toString())));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reward")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("vc.reward.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length > 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcReward));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    ArrayList<DailyWinner> winners = Main.db.getAllDailyWinners();
                    if (winners.isEmpty()) {
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    } else {
                        for (int i = 0; i < winners.size(); i++) {
                            if (winners.get(i).getPlayerName().equalsIgnoreCase(p.getName())) {
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
                                if (p.getInventory().firstEmpty() != -1
                                        && give
                                        && !reward[0].equalsIgnoreCase("[command]")) {
                                    ItemStack item = null;
                                    if (reward[0].contains("-")) {
                                        String[] splitItem = reward[0].split("-");
                                        item = new ItemStack(Material.getMaterial(splitItem[0]), 1, Short.parseShort(splitItem[1]));
                                        p.sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", reward[1]).replace("{item}", splitItem[0] + "-" + splitItem[1])));
                                    } else {
                                        item = new ItemStack(Material.getMaterial(reward[0]));
                                        p.sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", reward[1]).replace("{item}", reward[0])));
                                    }
                                    item.setAmount(Integer.parseInt(reward[1]));
                                    ItemStack finalItem = item;
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                                        p.getInventory().addItem(finalItem);
                                        Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
                                        winners.remove(number);
                                    });
                                } else {
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
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommandRefactor.replace("%player%", p.getName()));
                                        Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
                                        winners.remove(number);
                                    });
                                }
                                break;
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("top")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("vc.top.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandVcTopHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    p.sendMessage(ColorUtils.applyColor(actuallyInTop));
                    ArrayList<Challenger> top;
                    if (!Main.instance.getConfigGestion().isYesterdayTop()) {
                        top = Main.dailyChallenge.getTopPlayers(Main.instance.getConfigGestion().getNumberOfTop());
                    } else {
                        top = Main.db.getAllChallengersTopYesterday();
                    }
                    int i = 1;
                    while (!top.isEmpty()) {
                        p.sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + MoneyUtils.transform(top.get(0).getPoints()))));
                        top.remove(0);
                        i++;
                    }
                    if (debugCommand) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("vc.reload.command")) {
                        p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandVcReloadHelp));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        sender.sendMessage(ColorUtils.applyColor("&6Reloading..."));
                        ReloadUtils.reload();
                        sender.sendMessage(ColorUtils.applyColor("&aReloaded!"));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
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
                        debug.debug("Commands");
                    }
                }
            });
        }
        return false;
    }
}
