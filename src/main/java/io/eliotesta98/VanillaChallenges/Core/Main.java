package io.eliotesta98.VanillaChallenges.Core;

import io.eliotesta98.VanillaChallenges.Database.ChallengeDB;
import io.eliotesta98.VanillaChallenges.Database.ConfigGestion;
import io.eliotesta98.VanillaChallenges.Events.*;
import io.eliotesta98.VanillaChallenges.Utils.DailyGiveWinners;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import io.eliotesta98.VanillaChallenges.Comandi.Commands;
import io.eliotesta98.VanillaChallenges.Database.H2Database;
import io.eliotesta98.VanillaChallenges.Utils.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import org.bukkit.*;
import org.bukkit.command.*;

public class Main extends JavaPlugin {
    public static Main instance;
    public SoundManager SoundManager;
    private ConfigGestion config;
    public static Challenge dailyChallenge;
    public static ChallengeDB currentlyChallengeDB;
    private CheckDay checkDay;
    private BrodcastDailyChallenge brodcastDailyChallenge;

    public void onEnable() {
        DebugUtils debugsistem = new DebugUtils();
        long tempo = System.currentTimeMillis();
        Main.instance = this;

        getServer().getConsoleSender()
                .sendMessage("\n\n\n§a ___ ___                __  __  __          ______  __            __  __                                    \n" +
                        "|   |   |.---.-..-----.|__||  ||  |.---.-. |      ||  |--..---.-.|  ||  |.-----..-----..-----..-----..-----.\n" +
                        "|   |   ||  _  ||     ||  ||  ||  ||  _  | |   ---||     ||  _  ||  ||  ||  -__||     ||  _  ||  -__||__ --|\n" +
                        " \\_____/ |___._||__|__||__||__||__||___._| |______||__|__||___._||__||__||_____||__|__||___  ||_____||_____|\n" +
                        "                                                                                       |_____|              \n"
                        + "§a  \r\n" + "§a  \r\n" + "§e  Version " + getDescription().getVersion() + " \r\n"
                        + "§e© Developed by §feliotesta98 & xSavior_of_God §ewith §4<3 \r\n \r\n \r\n");

        SoundManager = new SoundManager();

        this.getServer().getConsoleSender().sendMessage("§6Loading config...");

        File configFile = new File(this.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {

                this.saveResource("config.yml", false);
                inputStream = this.getResource("config.yml");

                // write the inputStream to a FileOutputStream
                outputStream = new FileOutputStream(configFile);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create config.yml!");
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        // outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(configFile);

        try {
            String configname;

            configname = "config.yml";

            //esempio
            String splits = "Configuration.Challenges:bho";
            String[] strings = splits.split(":");
            cfg.syncWithConfig(configFile, this.getResource(configname), strings);
        } catch (IOException e) {
            e.printStackTrace();
        }

        config = new ConfigGestion(YamlConfiguration.loadConfiguration(configFile));

        getServer().getConsoleSender().sendMessage("§aConfiguration Loaded!");
        getServer().getConsoleSender().sendMessage("§6Connection to database!");
        try {
            getServer().getConsoleSender().sendMessage("HikariCP Folder '" + com.zaxxer.hikari.HikariConfig.class
                    .getProtectionDomain().getCodeSource().getLocation().getPath().toString() + "'");
            new H2Database(getDataFolder().getAbsolutePath());
            getServer().getConsoleSender().sendMessage("§aDatabase connected!");
        } catch (ClassNotFoundException | SQLException e) {
            getServer().getConsoleSender().sendMessage("Folder: '" + com.zaxxer.hikari.HikariConfig.class
                    .getProtectionDomain().getCodeSource().getLocation().getPath().toString() + "'");
            getServer().getConsoleSender().sendMessage("§cError Database not connected!");
            e.printStackTrace();
            onDisable();
            return;
        } catch (Exception ex) {
            getServer().getConsoleSender().sendMessage("Folder: '" + com.zaxxer.hikari.HikariConfig.class
                    .getProtectionDomain().getCodeSource().getLocation().getPath().toString() + "'");
            ex.printStackTrace();
            onDisable();
            return;
        }
        String typeChallenge = insertDailyChallenges();
        if (typeChallenge.equalsIgnoreCase("BlockPlaceChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new BlockPlaceEvent(),this);
        } else if (typeChallenge.equalsIgnoreCase("BlockBreakChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakEvent(),this);
        } else if (typeChallenge.equalsIgnoreCase("CraftingChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new CraftingEvent(),this);
        } else if (typeChallenge.equalsIgnoreCase("CookerChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new FurnaceBurnEvent(),this);
        } else if (typeChallenge.equalsIgnoreCase("ConsumeChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ItemConsumeEvent(),this);
        } else if (typeChallenge.equalsIgnoreCase("CollectorExpChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ExpCollector(),this);
        } else if (typeChallenge.equalsIgnoreCase("KillChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new KillMobEvent(),this);
        } else if (typeChallenge.equalsIgnoreCase("BreedChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new BreedEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("FeedChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new EatEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("ShootChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ShootArrowEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("JumpWithHorseChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new JumpHorseEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("ColorSheepChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ColorSheepEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("RaidChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new RaidEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("FishingChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new FishEvent(), this);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No DailyChallenge selected control config.yml!");
        }
        //Bukkit.getServer().getPluginManager().registerEvents(new Event(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DailyGiveWinners(), this);
        loadPlayersPoints();
        checkDay = new CheckDay();
        // ogni 60 minuti
        checkDay.start(20 * 60 * 60);
        brodcastDailyChallenge = new BrodcastDailyChallenge();
        if(config.getTimeBrodcastMessageTitle() != 0) {
            brodcastDailyChallenge.start((long) config.getTimeBrodcastMessageTitle() *60*20);
        }
        getCommand("vc").setExecutor((CommandExecutor) new Commands());
        if (config.getDebug().get("Enabled")) {
            debugsistem.addLine("Enabled execution time= " + (System.currentTimeMillis() - tempo));
            debugsistem.debug("Enabled");
        }
    }

    public void onDisable() {
        DebugUtils debugsistem = new DebugUtils();
        long tempo = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "VanillaChallenges has been disabled, §cBye bye! §e:(");
        if (checkDay != null) {
            checkDay.stop();
        }
        if(config.getTimeBrodcastMessageTitle() != 0) {
            brodcastDailyChallenge.stop();
        }
        dailyChallenge.clearPlayers();
        H2Database.disconnect();
        if (config.getDebug().get("Disabled")) {
            debugsistem.addLine("Disabled execution time= " + (System.currentTimeMillis() - tempo));
            debugsistem.debug("Disabled");
        }
    }

    public String insertDailyChallenges() {
        ArrayList<ChallengeDB> challenges = H2Database.instance.getAllChallenges();
        int count = 1;
        if (challenges.isEmpty()) {
            String nome = "nessuno";
            for (Map.Entry<String, Challenge> challenge : config.getChallenges().entrySet()) {
                if (count == 1) {
                    dailyChallenge = challenge.getValue();
                    nome = challenge.getValue().getTypeChallenge();
                    currentlyChallengeDB = new ChallengeDB(challenge.getKey(), 86400);
                }
                H2Database.instance.insertChallenge(challenge.getKey(), 86400);
                count++;
            }
            return nome;
        } else {
            while (!challenges.isEmpty()) {
                if (challenges.get(0).getTimeResume() <= 0) {
                    H2Database.instance.deleteChallengeWithName(challenges.get(0).getNomeChallenge());
                    challenges.remove(0);
                } else {
                    currentlyChallengeDB = challenges.get(0);
                    dailyChallenge = config.getChallenges().get(challenges.get(0).getNomeChallenge());
                    return dailyChallenge.getTypeChallenge();
                }
            }
            return "nessuno";
        }
    }

    public void loadPlayersPoints() {
        dailyChallenge.setPlayers(H2Database.instance.getAllChallengers());
        dailyChallenge.savePoints();
    }

    public ConfigGestion getConfigGestion() {
        return config;
    }

    public void setConfigGestion(ConfigGestion config) {
        this.config = config;
    }

    public Challenge getDailyChallenge() {
        return dailyChallenge;
    }

}
