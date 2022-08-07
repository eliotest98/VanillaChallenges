package io.eliotesta98.VanillaChallenges.Comandi;

import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.MoneyUtils;
import io.eliotesta98.VanillaChallenges.Utils.ReloadUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Commands implements CommandExecutor {

    private final String errorYouAreNotAPlayer = Main.instance.getConfigGestion().getMessages().get("errorYouAreNotAPlayer");
    private final String errorCommandNotFound = Main.instance.getConfigGestion().getMessages().get("errorCommandNotFound");
    private final String errorNoPerms = Main.instance.getConfigGestion().getMessages().get("errorNoPerms");

    private final String commandVcHelpHelp = Main.instance.getConfigGestion().getMessages().get("commandVcHelpHelp");
    private final String commandFooter = Main.instance.getConfigGestion().getMessages().get("commandFooter");
    private final String commandVcReloadHelp = Main.instance.getConfigGestion().getMessages().get("commandVcReloadHelp");
    private final String commandVcNextHelp = Main.instance.getConfigGestion().getMessages().get("commandVcNextHelp");
    private final String commandVcPointsHelp = Main.instance.getConfigGestion().getMessages().get("commandVcPointsHelp");
    private final String commandVcTopHelp = Main.instance.getConfigGestion().getMessages().get("commandVcTopHelp");
    private final String commandVcClear = Main.instance.getConfigGestion().getMessages().get("commandVcClear");
    private final String commandVcChallenge = Main.instance.getConfigGestion().getMessages().get("commandVcChallenge");
    private final String commandVcEconomyChallenge = Main.instance.getConfigGestion().getMessages().get("commandVcEconomyChallenge");
    private final String commandVcReward = Main.instance.getConfigGestion().getMessages().get("commandVcReward");

    private final String pointsInfo = Main.instance.getConfigGestion().getMessages().get("pointsInfo");
    private final ArrayList<String> brodcastMessageTitle = Main.dailyChallenge.getTitle();
    private final String actuallyInTop = Main.instance.getConfigGestion().getMessages().get("actuallyInTop");
    private final String pointsadd = Main.instance.getConfigGestion().getMessages().get("pointsadd");
    private final String pointsremove = Main.instance.getConfigGestion().getMessages().get("pointsremove");

    private final boolean debugCommand = Main.instance.getConfigGestion().getDebug().get("Commands");
    private final boolean resetPoints = Main.instance.getConfigGestion().isResetPointsAtNewChallenge();
    private String challengeReward = Main.instance.getConfigGestion().getMessages().get("challengeReward");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
                @Override
                public void run() {
                    DebugUtils debug = new DebugUtils();
                    long tempo = System.currentTimeMillis();
                    if (!command.getName().equalsIgnoreCase("vc")) {// comando se esiste
                        sender.sendMessage(ColorUtils.applyColor(errorCommandNotFound));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args.length == 0) {// se non ha scritto args
                        String finale = "\n\n&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
                        finale = finale + commandVcChallenge + "\n";
                        finale = finale + commandVcClear + "\n";
                        finale = finale + commandVcHelpHelp + "\n";
                        finale = finale + commandVcNextHelp + "\n";
                        finale = finale + commandVcPointsHelp + "\n";
                        finale = finale + commandVcReloadHelp + "\n";
                        finale = finale + commandVcReward + "\n";
                        finale = finale + commandVcTopHelp + "\n";
                        finale = finale + "\n";
                        finale = finale + commandFooter;
                        finale = finale + "\n";
                        sender.sendMessage(ColorUtils.applyColor(finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("add")) {
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
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("remove")) {
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
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("challenge")) {
                        if (args.length != 1) {
                            sender.sendMessage(ColorUtils.applyColor(commandVcChallenge));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        int timeResume = (Main.currentlyChallengeDB.getTimeResume() / 60) / 60;
                        for (int i = 0; i < brodcastMessageTitle.size(); i++) {
                            sender.sendMessage(ColorUtils.applyColor(brodcastMessageTitle.get(i).replace("{hours}", timeResume + "")));
                        }
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("clear")) {
                        if (args.length != 1) {
                            sender.sendMessage(ColorUtils.applyColor(commandVcClear));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                Main.db.clearAll();
                                ReloadUtil.reload();
                            }
                        });
                    }
                    else if (args[0].equalsIgnoreCase("next")) {
                        if (args.length != 1) {
                            sender.sendMessage(ColorUtils.applyColor(commandVcNextHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                                Main.db.deleteChallengeWithName(Main.currentlyChallengeDB.getNomeChallenge());
                                Main.db.removeTopYesterday();
                                Main.db.saveTopYesterday(topPlayers);
                                if (Main.instance.getConfigGestion().isBackupEnabled()) {
                                    Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                                }
                                int number = Main.db.lastDailyWinnerId();
                                while (!topPlayers.isEmpty()) {
                                    number++;
                                    DailyWinner dailyWinner = new DailyWinner();
                                    dailyWinner.setId(number);
                                    dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                                    dailyWinner.setNomeChallenge(Main.currentlyChallengeDB.getNomeChallenge());
                                    for (int i = 0; i < Main.dailyChallenge.getRewards().size(); i++) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.dailyChallenge.getRewards().get(i));
                                        Main.db.insertDailyWinner(dailyWinner);
                                        number++;
                                    }
                                    Main.db.insertDailyWinner(dailyWinner);
                                    topPlayers.remove(0);
                                }
                                if (resetPoints) {
                                    Main.db.clearChallengers();
                                    Main.dailyChallenge.clearPlayers();
                                }
                                ReloadUtil.reload();
                            }
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("help")) {
                        if (args.length != 1) {
                            sender.sendMessage(ColorUtils.applyColor(commandVcHelpHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        String finale = "\n\n&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
                        finale = finale + commandVcChallenge + "\n";
                        finale = finale + commandVcClear + "\n";
                        finale = finale + commandVcHelpHelp + "\n";
                        finale = finale + commandVcNextHelp + "\n";
                        finale = finale + commandVcPointsHelp + "\n";
                        finale = finale + commandVcReloadHelp + "\n";
                        finale = finale + commandVcReward + "\n";
                        finale = finale + commandVcTopHelp + "\n";
                        finale = finale + "\n";
                        finale = finale + commandFooter;
                        finale = finale + "\n";
                        sender.sendMessage(ColorUtils.applyColor(finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("points")) {
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
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        } else {
                            long points = Main.instance.getDailyChallenge().getPointFromPLayerName(args[1]);
                            sender.sendMessage(ColorUtils.applyColor(pointsInfo.replace("{player}", args[1]).replace("{number}", "" + points)));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                    }
                    else if (args[0].equalsIgnoreCase("reload")) {
                        if (args.length != 1) {
                            sender.sendMessage(ColorUtils.applyColor(commandVcReloadHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                sender.sendMessage(ColorUtils.applyColor("&6Reloading..."));
                                ReloadUtil.reload();
                                sender.sendMessage(ColorUtils.applyColor("&aReloaded!"));
                                if (debugCommand) {
                                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                    debug.debug("Commands");
                                }
                                return;
                            }
                        });
                        return;
                    }
                    else if(args[0].equalsIgnoreCase("reward")) {
                        sender.sendMessage(ColorUtils.applyColor(errorYouAreNotAPlayer));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("top")) {
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
                            top = Main.dailyChallenge.getTopPlayers(3);
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
                        return;
                    }
                    else {
                        String finale = "\n\n&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
                        finale = finale + commandVcChallenge + "\n";
                        finale = finale + commandVcClear + "\n";
                        finale = finale + commandVcHelpHelp + "\n";
                        finale = finale + commandVcNextHelp + "\n";
                        finale = finale + commandVcPointsHelp + "\n";
                        finale = finale + commandVcReloadHelp + "\n";
                        finale = finale + commandVcReward + "\n";
                        finale = finale + commandVcTopHelp + "\n";
                        finale = finale + "\n";
                        finale = finale + commandFooter;
                        finale = finale + "\n";
                        sender.sendMessage(ColorUtils.applyColor(finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                }
            });
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
                @Override
                public void run() {
                    final Player p = (Player) sender;
                    DebugUtils debug = new DebugUtils();
                    long tempo = System.currentTimeMillis();
                    if (!command.getName().equalsIgnoreCase("vc")) {// comando se esiste
                        p.sendMessage(ColorUtils.applyColor(errorCommandNotFound));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args.length == 0) {
                        String finale = "\n\n&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
                        if (p.hasPermission("vc.challenge.command")) {
                            finale = finale + commandVcChallenge + "\n";
                        }
                        if (p.hasPermission("vc.clear.command")) {
                            finale = finale + commandVcClear + "\n";
                        }
                        finale = finale + commandVcHelpHelp + "\n";
                        if (p.hasPermission("vc.next.command")) {
                            finale = finale + commandVcNextHelp + "\n";
                        }
                        if (p.hasPermission("vc.points.command")) {
                            finale = finale + commandVcPointsHelp + "\n";
                        }
                        if (p.hasPermission("vc.reload.command")) {
                            finale = finale + commandVcReloadHelp + "\n";
                        }
                        if(p.hasPermission("vc.reward.command")) {
                            finale = finale + commandVcReward + "\n";
                        }
                        if (p.hasPermission("vc.top.command")) {
                            finale = finale + commandVcTopHelp + "\n";
                        }
                        finale = finale + "\n";
                        finale = finale + commandFooter;
                        finale = finale + "\n";
                        p.sendMessage(ColorUtils.applyColor(finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("challenge")) {
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
                        int timeResume = (Main.currentlyChallengeDB.getTimeResume() / 60) / 60;
                        for (int i = 0; i < brodcastMessageTitle.size(); i++) {
                            p.sendMessage(ColorUtils.applyColor(brodcastMessageTitle.get(i).replace("{hours}", timeResume + "")));
                        }
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("clear")) {
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
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                Main.db.clearAll();
                                ReloadUtil.reload();
                            }
                        });
                    }
                    else if (args[0].equalsIgnoreCase("next")) {
                        if (!p.hasPermission("vc.next.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length != 1) {
                            sender.sendMessage(ColorUtils.applyColor(commandVcNextHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                                Main.db.deleteChallengeWithName(Main.currentlyChallengeDB.getNomeChallenge());
                                Main.db.removeTopYesterday();
                                Main.db.saveTopYesterday(topPlayers);
                                if (Main.instance.getConfigGestion().isBackupEnabled()) {
                                    Main.db.backupDb(Main.instance.getConfigGestion().getNumberOfFilesInFolderForBackup());
                                }
                                int number = Main.db.lastDailyWinnerId();
                                while (!topPlayers.isEmpty()) {
                                    number++;
                                    DailyWinner dailyWinner = new DailyWinner();
                                    dailyWinner.setId(number);
                                    dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                                    dailyWinner.setNomeChallenge(Main.currentlyChallengeDB.getNomeChallenge());
                                    for (int i = 0; i < Main.dailyChallenge.getRewards().size(); i++) {
                                        dailyWinner.setId(number);
                                        dailyWinner.setReward(Main.dailyChallenge.getRewards().get(i));
                                        Main.db.insertDailyWinner(dailyWinner);
                                        number++;
                                    }
                                    Main.db.insertDailyWinner(dailyWinner);
                                    topPlayers.remove(0);
                                }
                                if (resetPoints) {
                                    Main.db.clearChallengers();
                                    Main.dailyChallenge.clearPlayers();
                                }
                                ReloadUtil.reload();
                            }
                        });
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("help")) {
                        if (!p.hasPermission("vc.help.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length != 1) {
                            sender.sendMessage(ColorUtils.applyColor(commandVcHelpHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        } else {
                            String finale = "\n\n&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                    + " created by eliotesta98" + "\n\n";
                            if (p.hasPermission("vc.challenge.command")) {
                                finale = finale + commandVcChallenge + "\n";
                            }
                            if (p.hasPermission("vc.clear.command")) {
                                finale = finale + commandVcClear + "\n";
                            }
                            finale = finale + commandVcHelpHelp + "\n";
                            if (p.hasPermission("vc.next.command")) {
                                finale = finale + commandVcNextHelp + "\n";
                            }
                            if (p.hasPermission("vc.points.command")) {
                                finale = finale + commandVcPointsHelp + "\n";
                            }
                            if (p.hasPermission("vc.reload.command")) {
                                finale = finale + commandVcReloadHelp + "\n";
                            }
                            if(p.hasPermission("vc.reward.command")) {
                                finale = finale + commandVcReward + "\n";
                            }
                            if (p.hasPermission("vc.top.command")) {
                                finale = finale + commandVcTopHelp + "\n";
                            }
                            finale = finale + "\n";
                            finale = finale + commandFooter;
                            finale = finale + "\n";
                            p.sendMessage(ColorUtils.applyColor(finale));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                    }
                    else if (args[0].equalsIgnoreCase("points")) {
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
                            p.sendMessage(ColorUtils.applyColor(pointsInfo.replace("{player}", "Your").replace("{number}", "" + points)));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
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
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                    }
                    else if(args[0].equalsIgnoreCase("reward")) {
                        // controllo se ha il permesso
                        if (!p.hasPermission("vc.reward.command")) {
                            p.sendMessage(ColorUtils.applyColor(errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if(args.length > 1) {
                            p.sendMessage(ColorUtils.applyColor(commandVcReward));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        ArrayList<DailyWinner> winners = Main.db.getAllDailyWinners();
                        if(winners.isEmpty()) {
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        } else {
                            for (int i = 0; i < winners.size(); i++) {
                                if (winners.get(i).getPlayerName().equalsIgnoreCase(p.getName())) {
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
                                    if (p.getInventory().firstEmpty() != -1
                                            && give
                                            && !reward[0].equalsIgnoreCase("[command]")) {
                                        ItemStack item = new ItemStack(Material.getMaterial(reward[0]));
                                        item.setAmount(Integer.parseInt(reward[1]));
                                        p.sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", reward[1]).replace("{item}", reward[0])));
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                                            @Override
                                            public void run() {
                                                p.getInventory().addItem(item);
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
                                            p.sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", amount + "").replace("{item}", "")));
                                        } else {
                                            p.sendMessage(ColorUtils.applyColor(challengeReward.replace("{number}", amount + "").replace("{item}", material.toString())));
                                        }
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                                            @Override
                                            public void run() {
                                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), reward[1].replace("%player%", p.getName()));
                                                Main.db.deleteDailyWinnerWithId(winners.get(number).getId());
                                                winners.remove(number);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                    else if (args[0].equalsIgnoreCase("top")) {
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
                            top = Main.dailyChallenge.getTopPlayers(3);
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
                        return;
                    }
                    else if (args[0].equalsIgnoreCase("reload")) {
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
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                sender.sendMessage(ColorUtils.applyColor("&6Reloading..."));
                                ReloadUtil.reload();
                                sender.sendMessage(ColorUtils.applyColor("&aReloaded!"));
                                if (debugCommand) {
                                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                    debug.debug("Commands");
                                }
                                return;
                            }
                        });
                        return;
                    }
                    else {
                        String finale = "\n\n&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
                        if (p.hasPermission("vc.challenge.command")) {
                            finale = finale + commandVcChallenge + "\n";
                        }
                        if (p.hasPermission("vc.clear.command")) {
                            finale = finale + commandVcClear + "\n";
                        }
                        finale = finale + commandVcHelpHelp + "\n";
                        if (p.hasPermission("vc.next.command")) {
                            finale = finale + commandVcNextHelp + "\n";
                        }
                        if (p.hasPermission("vc.points.command")) {
                            finale = finale + commandVcPointsHelp + "\n";
                        }
                        if (p.hasPermission("vc.reload.command")) {
                            finale = finale + commandVcReloadHelp + "\n";
                        }
                        if(p.hasPermission("vc.reward.command")) {
                            finale = finale + commandVcReward + "\n";
                        }
                        if (p.hasPermission("vc.top.command")) {
                            finale = finale + commandVcTopHelp + "\n";
                        }
                        finale = finale + "\n";
                        finale = finale + commandFooter;
                        finale = finale + "\n";
                        p.sendMessage(ColorUtils.applyColor(finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                }
            });
        }
        return false;
    }
}
