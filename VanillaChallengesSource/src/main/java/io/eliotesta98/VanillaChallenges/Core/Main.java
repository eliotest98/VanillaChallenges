package io.eliotesta98.VanillaChallenges.Core;

import com.HeroxWar.HeroxCore.CommentedConfiguration;
import com.HeroxWar.HeroxCore.ReloadGesture;
import com.HeroxWar.HeroxCore.Utils.Version;
import io.eliotesta98.VanillaChallenges.Database.*;
import io.eliotesta98.VanillaChallenges.Events.*;
import io.eliotesta98.VanillaChallenges.Events.Challenges.*;
import io.eliotesta98.VanillaChallenges.Events.Challenges.ItemCollector.ItemCollector;
import io.eliotesta98.VanillaChallenges.Interfaces.GuiEvent;
import io.eliotesta98.VanillaChallenges.Interfaces.Interface;
import io.eliotesta98.VanillaChallenges.Modules.CubeGenerator.CubeGeneratorEvent;
import io.eliotesta98.VanillaChallenges.Modules.Lands.LandsUtils;
import io.eliotesta98.VanillaChallenges.Modules.PlaceholderApi.ExpansionPlaceholderAPI;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Events;
import jdk.internal.net.http.common.Log;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import io.eliotesta98.VanillaChallenges.Comandi.Commands;
import io.eliotesta98.VanillaChallenges.Utils.*;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.*;

public class Main extends JavaPlugin {
    public static Main instance;
    private ConfigGesture config;
    private Challenge dailyChallenge;
    public static ExpansionPlaceholderAPI EPAPI;
    public static Database db;
    public static boolean challengeSelected = true;
    public static Listener currentListener = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());
    public static Version version;

    @Override
    public void onLoad() {
        instance = this;
        version = new Version();
        // Load libraries where Spigot does not do this automatically
        loadLibraries();
    }

    public void onEnable() {
        DebugUtils debugSystem = new DebugUtils("Enabled");
        long tempo = System.currentTimeMillis();

        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 17661; // <-- Replace with the id of your plugin!
        new Metrics(this, pluginId);

        getServer().getConsoleSender()
                .sendMessage("\n\n\n§a ___ ___                __  __  __          ______  __            __  __                                    \n" +
                        "§a|   |   |.---.-..-----.|__||  ||  |.---.-. |      ||  |--..---.-.|  ||  |.-----..-----..-----..-----..-----.\n" +
                        "§a|   |   ||  _  ||     ||  ||  ||  ||  _  | |   ---||     ||  _  ||  ||  ||  -__||     ||  _  ||  -__||__ --|\n" +
                        "§a \\_____/ |___._||__|__||__||__||__||___._| |______||__|__||___._||__||__||_____||__|__||___  ||_____||_____|\n" +
                        "§a                                                                                       |_____|              \n"
                        + "§a  \r\n" + "§a  \r\n" + "§e  Version " + getDescription().getVersion() + " \r\n"
                        + "§e© Developed by §feliotesta98 & xSavior_of_God §ewith §4<3 \r\n \r\n \r\n");

        if (version.isInRange(8, 12)) {
            this.getServer().getConsoleSender().sendMessage("§6Server version registered < 1.13");
        } else {
            this.getServer().getConsoleSender().sendMessage("§6Server version registered > 1.12");
        }

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

                int read;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

            } catch (IOException e) {
                Log.logError(e);
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create config.yml!");
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.logError(e);
                    }
                }
                if (outputStream != null) {
                    try {
                        // outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        Log.logError(e);
                    }

                }
            }
        }

        CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(configFile);

        try {
            String configName;

            configName = "config.yml";

            //esempio
            String splits = "bho";
            String[] strings = splits.split(":");
            cfg.syncWithConfig(configFile, this.getResource(configName), strings);
        } catch (IOException e) {
            Log.logError(e);
        }
        try {
            config = new ConfigGesture(YamlConfiguration.loadConfiguration(configFile));
        } catch (IOException e) {
            Log.logError(e);
        }
        // RUNNABLE PER CARICARE LE DIPENDENZE ALLA FINE DELL'AVVIO DEL SERVER :D
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                if (getConfigGesture().getHooks().get("PlaceholderAPI")) {
                    Main.EPAPI = new ExpansionPlaceholderAPI().getInstance();
                    Main.EPAPI.register();
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fPlaceholderApi&a!"));
                }
            } else {
                getConfigGesture().getHooks().replace("PlaceholderAPI", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("CubeGenerator")) {
                if (getConfigGesture().getHooks().get("CubeGenerator")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fCubeGenerator&a!"));
                }
            } else {
                getConfigGesture().getHooks().replace("CubeGenerator", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention")) {
                if (getConfigGesture().getHooks().get("GriefPrevention")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fGriefPrevention&a!"));
                }
            } else {
                getConfigGesture().getHooks().replace("GriefPrevention", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Tombs")) {
                if (getConfigGesture().getHooks().get("Tombs")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fTombs&a!"));
                }
            } else {
                getConfigGesture().getHooks().replace("Tombs", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Lands")) {
                if (getConfigGesture().getHooks().get("Lands")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fLands&a!"));
                    LandsUtils.setLandsIntegration();
                }
            } else {
                getConfigGesture().getHooks().replace("Lands", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
                if (getConfigGesture().getHooks().get("WorldGuard")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fWorldGuard&a!"));
                }
            } else {
                getConfigGesture().getHooks().replace("WorldGuard", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
                if (getConfigGesture().getHooks().get("SuperiorSkyblock2")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fSuperiorSkyblock2&a!"));
                }
            } else {
                getConfigGesture().getHooks().replace("SuperiorSkyblock2", false);
            }
        });
        getServer().getConsoleSender().sendMessage("§aConfiguration Loaded!");
        getServer().getConsoleSender().sendMessage("§6Connection to database!");
        if (getConfigGesture().getDatabase().equalsIgnoreCase("H2")) {
            try {
                db = new H2Database(getDataFolder().getAbsolutePath());
            } catch (Exception e) {
                getServer().getConsoleSender().sendMessage("§cTry to restore the database");
                restoreDatabase();
                logger.log(Level.WARNING, e.getMessage());
                return;
            }
        } else if (getConfigGesture().getDatabase().equalsIgnoreCase("MySql")) {
            try {
                db = new MySql(config.getUrl());
            } catch (SQLException e) {
                Main.instance.getServer().getConsoleSender().sendMessage("§cError Database not connected!");
                Log.logError(e.getMessage());
                Main.instance.onDisable();
            }
        } else {
            db = new YamlDB();
        }
        getServer().getConsoleSender().sendMessage("§aDatabase connected!");
        new UpdateChecker(instance, 101426).getVersion(version1 -> {
            if (!instance.getDescription().getVersion().equals(version1)) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "New Update available for VanillaChallenges!");
            }
        });

        Bukkit.getServer().getPluginManager().registerEvents(new DailyGiveWinners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new GuiEvent(), this);

        pluginStartingProcess();

        getCommand("vanillachallenges").setExecutor(new Commands());
        if (config.getDebug().get("Enabled")) {
            debugSystem.addLine("Enabled execution time= " + (System.currentTimeMillis() - tempo));
            debugSystem.debug();
        }
    }

    public void onDisable() {
        DebugUtils debugSystem = new DebugUtils("Disabled");
        long tempo = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "VanillaChallenges has been disabled, §cBye bye! §e:(");
        config.getTasks().stopAllTasks();
        if (getConfigGesture().getHooks().get("PlaceholderAPI")) {
            try {
                Main.EPAPI.getInstance().unregister();
            } catch (Exception ignore) {
            }
        }
        if (challengeSelected) {
            dailyChallenge.clearPlayers();
            //close interfaces of interfaces
            for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGesture().getInterfaces().entrySet()) {
                interfaces.getValue().closeAllInventories();
            }
        }
        db.disconnect();
        if (config.getDebug().get("Disabled")) {
            debugSystem.addLine("Disabled execution time= " + (System.currentTimeMillis() - tempo));
            debugSystem.debug();
        }
    }

    public ConfigGesture getConfigGesture() {
        return config;
    }

    public Challenge getDailyChallenge() {
        return dailyChallenge;
    }

    public void setDailyChallenge(Challenge dailyChallenge) {
        this.dailyChallenge = dailyChallenge;
    }

    private void loadLibraries() {
        final List<Library> libraries = new ArrayList<>();

        boolean oldVersion = version.isInRange(8, 16);

        if (oldVersion) {
            Bukkit.getConsoleSender().sendMessage("Loading legacy libraries...");
            Reader targetReader = new InputStreamReader(getResource("plugin.yml"));

            YamlConfiguration pluginFile = YamlConfiguration.loadConfiguration(targetReader);
            for (final String libraryPath : pluginFile.getStringList("legacy-libraries")) {
                final Library library = Library.fromMavenRepo(libraryPath);
                Bukkit.getConsoleSender().sendMessage("Loading library " + libraryPath);
                libraries.add(library);
            }

            for (final Library library : libraries)
                library.load();
            Bukkit.getConsoleSender().sendMessage("Legacy libraries loaded!");
        }
    }

    public void restoreDatabase() {
        File db = new File(Main.instance.getDataFolder(), "vanillachallenges.mv.db");
        File dbNew = new File(Main.instance.getDataFolder(), "vanillachallengesOld.mv.db");
        if (db.renameTo(dbNew)) {
            getServer().getConsoleSender().sendMessage("§cOlder Database Successfully Renamed");
        } else {
            getServer().getConsoleSender().sendMessage("§cOlder Database Not Successfully Deleted");
            onDisable();
            return;
        }
        ReloadGesture.reload(instance.getName());
    }

    public void pluginStartingProcess() {
        // control if challenges is on db but is disabled on config
        db.controlIfChallengeExist(config.getControlIfChallengeExist());
        // select a challenge
        String typeChallenge = db.insertDailyChallenges();
        // reset of variable for a new Challenge
        challengeSelected = true;
        boolean skipCheck = false;
        if (typeChallenge.equalsIgnoreCase("BlockPlaceChallenge")) {
            currentListener = new BlockPlaceEvent();
        } else if (typeChallenge.equalsIgnoreCase("BlockBreakChallenge")) {
            currentListener = new BlockBreakEvent();
        } else if (typeChallenge.equalsIgnoreCase("CraftingChallenge")) {
            currentListener = new CraftingEvent();
        } else if (typeChallenge.equalsIgnoreCase("CookerChallenge")) {
            currentListener = new FurnaceBurnEvent();
        } else if (typeChallenge.equalsIgnoreCase("ConsumeChallenge")) {
            currentListener = new ItemConsumeEvent();
        } else if (typeChallenge.equalsIgnoreCase("CollectorExpChallenge")) {
            currentListener = new ExpCollector();
        } else if (typeChallenge.equalsIgnoreCase("KillChallenge")) {
            currentListener = new KillMobEvent();
        } else if (typeChallenge.equalsIgnoreCase("BreedChallenge")) {
            currentListener = new BreedEvent();
        } else if (typeChallenge.equalsIgnoreCase("FeedChallenge")) {
            currentListener = new EatEvent();
        } else if (typeChallenge.equalsIgnoreCase("ShootChallenge")) {
            currentListener = new ShootArrowEvent();
        } else if (typeChallenge.equalsIgnoreCase("JumpWithHorseChallenge")) {
            currentListener = new JumpHorseEvent();
        } else if (typeChallenge.equalsIgnoreCase("ColorSheepChallenge")) {
            currentListener = new ColorSheepEvent();
        } else if (typeChallenge.equalsIgnoreCase("RaidChallenge")) {
            currentListener = new RaidEvent();
        } else if (typeChallenge.equalsIgnoreCase("FishingChallenge")) {
            currentListener = new FishEvent();
        } else if (typeChallenge.equalsIgnoreCase("SprintChallenge")) {
            currentListener = new SprintEvent();
        } else if (typeChallenge.equalsIgnoreCase("MoveChallenge")) {
            currentListener = new MoveEvent();
        } else if (typeChallenge.equalsIgnoreCase("DamageChallenge")) {
            currentListener = new DamageEvent();
        } else if (typeChallenge.equalsIgnoreCase("SneakChallenge")) {
            currentListener = new SneakEvent();
        } else if (typeChallenge.equalsIgnoreCase("ItemBreakChallenge")) {
            currentListener = new ItemBreakEvent();
        } else if (typeChallenge.equalsIgnoreCase("SpongeAbsorbChallenge")) {
            currentListener = new SpongeAbsorbeEvent();
        } else if (typeChallenge.equalsIgnoreCase("HarvestChallenge")) {
            currentListener = new HarvestEvent();
        } else if (typeChallenge.equalsIgnoreCase("EggThrowerChallenge")) {
            currentListener = new EggThrowEvent();
        } else if (typeChallenge.equalsIgnoreCase("EnchantChallenge")) {
            currentListener = new EnchantEvent();
        } else if (typeChallenge.equalsIgnoreCase("ChatChallenge")) {
            currentListener = new ChatEvent();
        } else if (typeChallenge.equalsIgnoreCase("ItemCollectionChallenge")) {
            currentListener = new ItemCollector();
        } else if (typeChallenge.equalsIgnoreCase("InventoryConditionChallenge")) {
            new InventoryCheck();
            skipCheck = true;
        } else if (typeChallenge.equalsIgnoreCase("VehicleMoveChallenge")) {
            currentListener = new VehicleMoveEvent();
        } else if (typeChallenge.equalsIgnoreCase("JumpChallenge")) {
            currentListener = new JumpEvent();
        } else if (typeChallenge.equalsIgnoreCase("DyerChallenge")) {
            currentListener = new DyeEvent();
        } else if (typeChallenge.equalsIgnoreCase("CubeGeneratorChallenge")) {
            currentListener = new CubeGeneratorEvent();
        } else if (typeChallenge.equalsIgnoreCase("DropperChallenge")) {
            currentListener = new DropperEvent();
        } else if (typeChallenge.equalsIgnoreCase("HealthChallenge")) {
            currentListener = new HealthRegenEvent();
        } else if (typeChallenge.equalsIgnoreCase("AFKChallenge")) {
            new AFKCheck();
            skipCheck = true;
        } else if (typeChallenge.equalsIgnoreCase("MissionChallenge")) {
            currentListener = new SuperiorSkyBlock2Events();
        } else if (typeChallenge.equalsIgnoreCase("SensorChallenge")) {
            currentListener = new GameBlockEvent();
        } else if (typeChallenge.equalsIgnoreCase("PrimerChallenge")) {
            currentListener = new PrimeEvent();
        } else if (typeChallenge.equalsIgnoreCase("FireCatcherChallenge")) {
            currentListener = new FireCatcher();
        } else if (typeChallenge.equalsIgnoreCase("EntityCatcherChallenge")) {
            currentListener = new EntityCatcherEvent();
        } else if (typeChallenge.equalsIgnoreCase("LeashChallenge")) {
            currentListener = new LeashEvent();
        } else if (typeChallenge.equalsIgnoreCase("SleepChallenge")) {
            currentListener = new SleepEvent();
        } else if (typeChallenge.equalsIgnoreCase("WoolCutterChallenge")) {
            currentListener = new PlayerShearsEvent();
        } else if (typeChallenge.equalsIgnoreCase("RiptideChallenge")) {
            currentListener = new RiptideEvent();
        } else {
            challengeSelected = false;
        }

        if (skipCheck) {
            challengeSelected = true;
        } else if (currentListener == null) {
            challengeSelected = false;
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No DailyChallenge selected, if you use the plugin without a scheduling ignore this error, otherwise check the configurations files and restart the plugin!");
        } else {
            Bukkit.getServer().getPluginManager().registerEvents(currentListener, this);
        }

        if (challengeSelected) {
            db.loadPlayersPoints();
            config.getTasks().checkStartDay();
            if (config.getTimeBroadcastMessageTitle() != 0) {
                config.getTasks().broadcast(((long) config.getTimeBroadcastMessageTitle() * 60 * 20)
                        , config.getMessages().get("ActuallyInTop")
                        , config.getMessages().get("PointsEveryMinutes")
                        , config.getMessages().get("PointsRemainForBoosting")
                        , config.getMessages().get("PointsRemainForBoostingSinglePlayer")
                        , config.getNumberOfTop()
                        , config.getMessages().get("PointsRemainForReward")
                );
            }
            if (config.isActiveOnlinePoints()) {
                config.getTasks().onlinePoints(config.getMinutesOnlinePoints(), config.getPointsOnlinePoints());
            }
        }
    }

    public void unregisterCurrentListener() {
        HandlerList.unregisterAll(currentListener);
    }
}
