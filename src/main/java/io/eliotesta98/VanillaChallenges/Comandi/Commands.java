package io.eliotesta98.VanillaChallenges.Comandi;

import io.eliotesta98.VanillaChallenges.Database.Challenger;
import io.eliotesta98.VanillaChallenges.Database.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import io.eliotesta98.VanillaChallenges.Utils.ReloadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;

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

    private final String pointsInfo = Main.instance.getConfigGestion().getMessages().get("pointsInfo");
    private final String topPlayers = Main.instance.getConfigGestion().getMessages().get("topPlayers");

    private final boolean debugCommand = Main.instance.getConfigGestion().getDebug().get("Commands");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {// TODO console
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
                @Override
                public void run() {
                    DebugUtils debug = new DebugUtils();
                    long tempo = System.currentTimeMillis();
                    if (!command.getName().equalsIgnoreCase("vc")) {// comando se esiste
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', errorCommandNotFound));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    } else if (args.length == 0) {// se non ha scritto args
                        String finale = "&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
                        finale = finale + commandVcClear + "\n";
                        finale = finale + commandVcHelpHelp + "\n";
                        finale = finale + commandVcNextHelp + "\n";
                        finale = finale + commandVcPointsHelp + "\n";
                        finale = finale + commandVcReloadHelp + "\n";
                        finale = finale + commandVcTopHelp + "\n";
                        finale = finale + commandFooter;
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    } else if (args[0].equalsIgnoreCase("clear")) {
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcClear));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                H2Database.instance.clearAll();
                                ReloadUtil.reload();
                            }
                        });
                        //TODO next
                    } else if (args[0].equalsIgnoreCase("next")) {
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcNextHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                H2Database.instance.deleteChallengeWithName(Main.currentlyChallengeDB.getNomeChallenge());
                                ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                                while (!topPlayers.isEmpty()) {
                                    DailyWinner dailyWinner = new DailyWinner();
                                    dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                                    dailyWinner.setNomeChallenge(Main.currentlyChallengeDB.getNomeChallenge());
                                    dailyWinner.setReward(Main.dailyChallenge.getReward());
                                    H2Database.instance.insertDailyWinner(dailyWinner);
                                    topPlayers.remove(0);
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
                    // TODO vc help
                    else if (args[0].

                            equalsIgnoreCase("help")) {
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcHelpHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        String finale = "&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
                        finale = finale + commandVcClear + "\n";
                        finale = finale + commandVcHelpHelp + "\n";
                        finale = finale + commandVcNextHelp + "\n";
                        finale = finale + commandVcPointsHelp + "\n";
                        finale = finale + commandVcReloadHelp + "\n";
                        finale = finale + commandVcTopHelp + "\n";
                        finale = finale + commandFooter;
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                        //TODO points
                    } else if (args[0].

                            equalsIgnoreCase("points")) {
                        if (args.length > 2) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcReloadHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length == 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', errorYouAreNotAPlayer));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        } else {
                            int points = Main.instance.getDailyChallenge().getPointFromPLayerName(args[1]);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', pointsInfo.replace("{player}", args[1]).replace("{number}", "" + points)));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                    }
                    // TODO vc reload
                    else if (args[0].

                            equalsIgnoreCase("reload")) {
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcReloadHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Reloading..."));
                                ReloadUtil.reload();
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded!"));
                                if (debugCommand) {
                                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                    debug.debug("Commands");
                                }
                                return;
                            }
                        });
                        return;
                    } else if (args[0].

                            equalsIgnoreCase("top")) {
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcTopHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(3);
                        int i = 1;
                        while (!top.isEmpty()) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', topPlayers.replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + top.get(0).getPoints())));
                            top.remove(0);
                            i++;
                        }
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    } else {
                        String finale = "&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
                        finale = finale + commandVcClear + "\n";
                        finale = finale + commandVcHelpHelp + "\n";
                        finale = finale + commandVcNextHelp + "\n";
                        finale = finale + commandVcPointsHelp + "\n";
                        finale = finale + commandVcReloadHelp + "\n";
                        finale = finale + commandVcTopHelp + "\n";
                        finale = finale + commandFooter;
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                }
            });
        } else {// TODO player
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
                @Override
                public void run() {
                    final Player p = (Player) sender;
                    DebugUtils debug = new DebugUtils();
                    long tempo = System.currentTimeMillis();
                    if (!command.getName().equalsIgnoreCase("vc")) {// comando se esiste
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', errorCommandNotFound));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    } else if (args.length == 0) {
                        String finale = "&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
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
                        if (p.hasPermission("vc.top.command")) {
                            finale = finale + commandVcTopHelp + "\n";
                        }
                        finale = finale + commandFooter;
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', finale));
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                        //TODO clear
                    } else if (args[0].equalsIgnoreCase("clear")) {
                        if (!p.hasPermission("vc.clear.command")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcClear));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                H2Database.instance.clearAll();
                                ReloadUtil.reload();
                            }
                        });
                        //TODO next
                    } else if (args[0].equalsIgnoreCase("next")) {
                        if (!p.hasPermission("vc.next.command")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcNextHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                H2Database.instance.deleteChallengeWithName(Main.currentlyChallengeDB.getNomeChallenge());
                                ArrayList<Challenger> topPlayers = Main.dailyChallenge.getTopPlayers(3);
                                while (!topPlayers.isEmpty()) {
                                    DailyWinner dailyWinner = new DailyWinner();
                                    dailyWinner.setPlayerName(topPlayers.get(0).getNomePlayer());
                                    dailyWinner.setNomeChallenge(Main.currentlyChallengeDB.getNomeChallenge());
                                    dailyWinner.setReward(Main.dailyChallenge.getReward());
                                    H2Database.instance.insertDailyWinner(dailyWinner);
                                    topPlayers.remove(0);
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
                    // TODO cg help
                    else if (args[0].equalsIgnoreCase("help")) {
                        if (!p.hasPermission("vc.help.command")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcHelpHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        } else {
                            String finale = "&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                    + " created by eliotesta98" + "\n\n";
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
                            if (p.hasPermission("vc.top.command")) {
                                finale = finale + commandVcTopHelp + "\n";
                            }
                            finale = finale + commandFooter;
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', finale));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        //TODO points
                    } else if (args[0].equalsIgnoreCase("points")) {
                        // controllo se ha il permesso
                        if (!p.hasPermission("vc.points.command")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length > 2) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcPointsHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length == 1) {
                            int points = Main.instance.getDailyChallenge().getPointFromPLayerName(p.getName());
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', pointsInfo.replace("{player}", "Your").replace("{number}", "" + points)));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        } else {
                            if (!p.hasPermission("vc.points.admin.command")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', errorNoPerms));
                                if (debugCommand) {
                                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                    debug.debug("Commands");
                                }
                                return;
                            }
                            int points = Main.instance.getDailyChallenge().getPointFromPLayerName(args[1]);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', pointsInfo.replace("{player}", args[1]).replace("{number}", "" + points)));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                    }     //TODO top
                    else if (args[0].equalsIgnoreCase("top")) {
                        // controllo se ha il permesso
                        if (!p.hasPermission("vc.top.command")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length != 1) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcTopHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(3);
                        int i = 1;
                        while (!top.isEmpty()) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', topPlayers.replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + top.get(0).getPoints())));
                            top.remove(0);
                            i++;
                        }
                        if (debugCommand) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    // TODO cg reload
                    else if (args[0].equalsIgnoreCase("reload")) {
                        // controllo se ha il permesso
                        if (!p.hasPermission("vc.reload.command")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', errorNoPerms));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        if (args.length != 1) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandVcReloadHelp));
                            if (debugCommand) {
                                debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                debug.debug("Commands");
                            }
                            return;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Reloading..."));
                                ReloadUtil.reload();
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded!"));
                                if (debugCommand) {
                                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                                    debug.debug("Commands");
                                }
                                return;
                            }
                        });
                        return;
                    } else {
                        String finale = "&e&lVanillaChallenges &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98" + "\n\n";
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
                        if (p.hasPermission("vc.top.command")) {
                            finale = finale + commandVcTopHelp + "\n";
                        }
                        finale = finale + commandFooter;
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', finale));
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
