package io.eliotesta98.VanillaChallenges.Core;

import com.HeroxWar.HeroxCore.MessageGesture.MessageGesturePaper;
import com.HeroxWar.HeroxCore.ReloadGesture;
import com.HeroxWar.HeroxCore.TimeGesture.Time;
import com.HeroxWar.HeroxCore.Utils.Library;
import com.HeroxWar.HeroxCore.Utils.Metrics;
import com.HeroxWar.HeroxCore.Utils.UpdateChecker;
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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import io.eliotesta98.VanillaChallenges.Commands.Commands;
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
    private ConfigGestion config;
    private Challenge dailyChallenge;
    public static ExpansionPlaceholderAPI EPAPI;
    public static Database db;
    public static boolean challengeSelected = true;
    public static Listener currentListener = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());
    public static Version version;
    public static MessageGesturePaper messageGesturePaper;
    private List<String> libraryLegacyMessages = new ArrayList<>();
    public static boolean mockTest = false;
    private PointsReconvert pointsReconvert;

    @Override
    public void onLoad() {
        instance = this;
        if (getClassLoader().getClass().getName().startsWith("org.mockbukkit.mockbukkit")) {
            mockTest = true;
        }
        version = new Version();
        // Load libraries where Spigot does not do this automatically
        libraryLegacyMessages = loadLibraries();
    }

    public void onEnable() {
        if(mockTest) {
            new Metrics(this, 17661);
        }

        messageGesturePaper = new MessageGesturePaper(true, false, instance);

        for(String message: libraryLegacyMessages) {
            messageGesturePaper.sendMessage(message);
        }
        libraryLegacyMessages.clear();

        messageGesturePaper.sendMessage("\n\n\n&a ___ ___                __  __  __          ______  __            __  __                                    \n" +
                "&a|   |   |.---.-..-----.|__||  ||  |.---.-. |      ||  |--..---.-.|  ||  |.-----..-----..-----..-----..-----.\n" +
                "&a|   |   ||  _  ||     ||  ||  ||  ||  _  | |   ---||     ||  _  ||  ||  ||  -__||     ||  _  ||  -__||__ --|\n" +
                "&a \\_____/ |___._||__|__||__||__||__||___._| |______||__|__||___._||__||__||_____||__|__||___  ||_____||_____|\n" +
                "&a                                                                                       |_____|              \n"
                + "&a  \r\n" + "&a  \r\n" + "&e  Version " + getDescription().getVersion() + " \r\n"
                + "&e© Developed by &feliotesta98 & xSavior_of_God &ewith &4<3 \r\n \r\n \r\n");

        if (version.isInRange(8, 12)) {
            messageGesturePaper.sendMessage("&6Server version registered < 1.13");
        } else {
            messageGesturePaper.sendMessage("&6Server version registered > 1.12");
        }
        messageGesturePaper.sendMessage("Version Detected: &c" + version.getFormattedServerVersion());

        messageGesturePaper.sendMessage("&6Loading config...");
        config = new ConfigGestion(this.getDataFolder().getPath(), "config.yml");

        loadConfigs();
    }

    public void loadConfigs() {
        // RUNNABLE PER CARICARE LE DIPENDENZE ALLA FINE DELL'AVVIO DEL SERVER :D
        getServer().getScheduler().runTask(this, () -> {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                if (config.getHooks().get("PlaceholderAPI")) {
                    Main.EPAPI = new ExpansionPlaceholderAPI().getInstance();
                    Main.EPAPI.register();
                    messageGesturePaper.sendMessage("&aAdded compatibility to &fPlaceholderApi&a!");
                    messageGesturePaper.setPlaceholderAPIEnabled(true);
                }
            } else {
                config.getHooks().replace("PlaceholderAPI", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("CubeGenerator")) {
                if (config.getHooks().get("CubeGenerator")) {
                    messageGesturePaper.sendMessage("&aAdded compatibility to &fCubeGenerator&a!");
                }
            } else {
                config.getHooks().replace("CubeGenerator", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention")) {
                if (config.getHooks().get("GriefPrevention")) {
                    messageGesturePaper.sendMessage( "&aAdded compatibility to &fGriefPrevention&a!");
                }
            } else {
                config.getHooks().replace("GriefPrevention", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Tombs")) {
                if (config.getHooks().get("Tombs")) {
                    messageGesturePaper.sendMessage("&aAdded compatibility to &fTombs&a!");
                }
            } else {
                config.getHooks().replace("Tombs", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Lands")) {
                if (config.getHooks().get("Lands")) {
                    messageGesturePaper.sendMessage("&aAdded compatibility to &fLands&a!");
                    LandsUtils.setLandsIntegration();
                }
            } else {
                config.getHooks().replace("Lands", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
                if (config.getHooks().get("WorldGuard")) {
                    messageGesturePaper.sendMessage("&aAdded compatibility to &fWorldGuard&a!");
                }
            } else {
                config.getHooks().replace("WorldGuard", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
                if (config.getHooks().get("SuperiorSkyblock2")) {
                    messageGesturePaper.sendMessage("&aAdded compatibility to &fSuperiorSkyblock2&a!");
                }
            } else {
                config.getHooks().replace("SuperiorSkyblock2", false);
            }
        });
        messageGesturePaper.sendMessage("&aConfiguration Loaded!");
        messageGesturePaper.sendMessage("&6Connection to database!");
        if (config.getDatabase().equalsIgnoreCase("H2")) {
            try {
                db = new H2Database(getDataFolder().getAbsolutePath());
            } catch (Exception e) {
                messageGesturePaper.sendMessage("&cTry to restore the database");
                restoreDatabase();
                logger.log(Level.WARNING, e.getMessage());
                return;
            }
        } else if (config.getDatabase().equalsIgnoreCase("MySql")) {
            try {
                db = new MySql(config.getUrl());
            } catch (SQLException e) {
                messageGesturePaper.sendMessage("&cError Database not connected!");
                logger.log(Level.SEVERE, e.getMessage());
                Main.instance.onDisable();
            }
        } else {
            db = new YamlDB();
        }
        messageGesturePaper.sendMessage("&aDatabase connected!");
        new UpdateChecker(instance, 101426).getVersion(version1 -> {
            if (!instance.getDescription().getVersion().equals(version1)) {
                messageGesturePaper.sendMessage("&cNew Update available for VanillaChallenges!");
            }
        });

        Bukkit.getServer().getPluginManager().registerEvents(new DailyGiveWinners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new GuiEvent(), this);
        if(config.isReconvert()) {
            pointsReconvert = new PointsReconvert();
            Bukkit.getServer().getPluginManager().registerEvents(pointsReconvert, this);
        }

        pluginStartingProcess();

        getCommand("vanillachallenges").setExecutor(new Commands());
    }

    public void onDisable() {
        DebugUtils debugSystem = new DebugUtils("Disabled");
        long tempo = System.currentTimeMillis();
        messageGesturePaper.sendMessage("&aVanillaChallenges has been disabled, &cBye bye! &e:(");
        unload();
        if (config.getDebug().get("Disabled")) {
            debugSystem.addLine("Disabled execution time= " + (System.currentTimeMillis() - tempo));
            debugSystem.debug();
        }
    }

    public void unload() {
        config.getTasks().stopAllTasks();
        if (config.getHooks().get("PlaceholderAPI")) {
            try {
                Main.EPAPI.getInstance().unregister();
            } catch (Exception ignore) {
            }
        }
        if (challengeSelected) {
            dailyChallenge.clearPlayers();
            //close interfaces of interfaces
            for (Map.Entry<String, Interface> interfaces : config.getInterfaces().entrySet()) {
                interfaces.getValue().closeAllInventories();
            }
        }
        if(config.isReconvert()) {
            pointsReconvert.unregister();
        }
        db.disconnect();
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

    public void setDailyChallenge(Challenge dailyChallenge) {
        this.dailyChallenge = dailyChallenge;
    }

    private List<String> loadLibraries() {
        final List<Library> libraries = new ArrayList<>();

        boolean oldVersion = version.isInRange(8, 16);

        List<String> messagesToSend = new ArrayList<>();

        if (oldVersion) {
            messagesToSend.add("Loading legacy libraries...");
            Reader targetReader = new InputStreamReader(getResource("plugin.yml"));

            YamlConfiguration pluginFile = YamlConfiguration.loadConfiguration(targetReader);
            for (final String libraryPath : pluginFile.getStringList("legacy-libraries")) {
                final Library library = Library.fromMavenRepo(libraryPath);
                messagesToSend.add("Loading library " + libraryPath);
                libraries.add(library);
            }

            for (final Library library : libraries)
                library.load(Main.class.getClassLoader());
            messagesToSend.add("Legacy libraries loaded!");
        }
        return messagesToSend;
    }

    public void restoreDatabase() {
        File db = new File(Main.instance.getDataFolder(), "vanillachallenges.mv.db");
        File dbNew = new File(Main.instance.getDataFolder(), "vanillachallengesOld.mv.db");
        if (db.renameTo(dbNew)) {
            messageGesturePaper.sendMessage("&cOlder Database Successfully Renamed");
        } else {
            messageGesturePaper.sendMessage("&cOlder Database Not Successfully Deleted");
            onDisable();
            return;
        }
        ReloadGesture.reload(instance.getName());
    }

    public void pluginStartingProcess() {
        // control if challenges is on db but is disabled on config
        db.controlIfChallengeExist(config.getControlIfChallengeExist());
        // control of peacefullTime
        boolean peacefullTime = db.checkPeacefulTime();
        // default selection is no challenge
        String typeChallenge = "nobody";
        // reset of variable for a new Challenge
        challengeSelected = true;
        currentListener = null;
        boolean skipCheck = false;
        // if the peacefulTime is finish
        if (!peacefullTime) {
            // select a challenge
            db.insertDailyChallenges();
            if (getDailyChallenge() != null) {
                typeChallenge = getDailyChallenge().getTypeChallenge();
            }
        } else {
            config.getTasks().peacefulTimeTask();
        }

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
            if (!peacefullTime) {
                messageGesturePaper.sendMessage("&cNo Scheduler enabled, remember for use the plugin now you have to use vc event command for start a challenge!");
            } else {
                Time time = Main.db.getPeacefulTime();
                messageGesturePaper.sendMessage(Bukkit.getConsoleSender(), config.getMessages().get("Cooldown")
                        .replace("{hours}", time.getHours() + "")
                        .replace("{minutes}", time.getMinutes() + "")
                        .replace("{seconds}", time.getSeconds() + ""));
            }
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
