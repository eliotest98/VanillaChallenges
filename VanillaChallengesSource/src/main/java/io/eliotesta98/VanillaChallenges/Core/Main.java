package io.eliotesta98.VanillaChallenges.Core;

import io.eliotesta98.VanillaChallenges.Database.*;
import io.eliotesta98.VanillaChallenges.Events.*;
import io.eliotesta98.VanillaChallenges.Events.Challenges.*;
import io.eliotesta98.VanillaChallenges.Interfaces.GuiEvent;
import io.eliotesta98.VanillaChallenges.Interfaces.Interface;
import io.eliotesta98.VanillaChallenges.Modules.CubeGenerator.CubeGeneratorEvent;
import io.eliotesta98.VanillaChallenges.Modules.Lands.LandsUtils;
import io.eliotesta98.VanillaChallenges.Modules.PlaceholderApi.ExpansionPlaceholderAPI;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Events;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import io.eliotesta98.VanillaChallenges.Comandi.Commands;
import io.eliotesta98.VanillaChallenges.Utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.*;
import org.bukkit.command.*;

public class Main extends JavaPlugin {
    public static Main instance;
    public SoundManager SoundManager;
    private ConfigGestion config;
    private Challenge dailyChallenge;
    public static ExpansionPlaceholderAPI EPAPI;
    public static Database db;
    public static boolean challengeSelected = true;
    public static boolean version113 = true;

    @Override
    public void onLoad() {
        instance = this;

        // Load libraries where Spigot does not do this automatically
        loadLibraries();
    }

    public void onEnable() {
        DebugUtils debugsistem = new DebugUtils("Enabled");
        long tempo = System.currentTimeMillis();

        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 17661; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        getServer().getConsoleSender()
                .sendMessage("\n\n\n§a ___ ___                __  __  __          ______  __            __  __                                    \n" +
                        "§a|   |   |.---.-..-----.|__||  ||  |.---.-. |      ||  |--..---.-.|  ||  |.-----..-----..-----..-----..-----.\n" +
                        "§a|   |   ||  _  ||     ||  ||  ||  ||  _  | |   ---||     ||  _  ||  ||  ||  -__||     ||  _  ||  -__||__ --|\n" +
                        "§a \\_____/ |___._||__|__||__||__||__||___._| |______||__|__||___._||__||__||_____||__|__||___  ||_____||_____|\n" +
                        "§a                                                                                       |_____|              \n"
                        + "§a  \r\n" + "§a  \r\n" + "§e  Version " + getDescription().getVersion() + " \r\n"
                        + "§e© Developed by §feliotesta98 & xSavior_of_God §ewith §4<3 \r\n \r\n \r\n");

        SoundManager = new SoundManager();

        if (getServer().getVersion().contains("1.8") || getServer().getVersion().contains("1.9") ||
                getServer().getVersion().contains("1.10") || getServer().getVersion().contains("1.11") ||
                getServer().getVersion().contains("1.12")) {
            version113 = false;
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
            String splits = "bho";
            String[] strings = splits.split(":");
            cfg.syncWithConfig(configFile, this.getResource(configname), strings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            config = new ConfigGestion(YamlConfiguration.loadConfiguration(configFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // RUNNABLE PER CARICARE LE DIPENDENZE ALLA FINE DELL'AVVIO DEL SERVER :D
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                if (getConfigGestion().getHooks().get("PlaceholderAPI")) {
                    Main.EPAPI = new ExpansionPlaceholderAPI().getInstance();
                    Main.EPAPI.register();
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fPlaceholderApi&a!"));
                }
            } else {
                getConfigGestion().getHooks().replace("PlaceholderAPI", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("CubeGenerator")) {
                if (getConfigGestion().getHooks().get("CubeGenerator")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fCubeGenerator&a!"));
                }
            } else {
                getConfigGestion().getHooks().replace("CubeGenerator", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention")) {
                if (getConfigGestion().getHooks().get("GriefPrevention")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fGriefPrevention&a!"));
                }
            } else {
                getConfigGestion().getHooks().replace("GriefPrevention", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Tombs")) {
                if (getConfigGestion().getHooks().get("Tombs")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fTombs&a!"));
                }
            } else {
                getConfigGestion().getHooks().replace("Tombs", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Lands")) {
                if (getConfigGestion().getHooks().get("Lands")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fLands&a!"));
                    LandsUtils.setLandsIntegration();
                }
            } else {
                getConfigGestion().getHooks().replace("Lands", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
                if (getConfigGestion().getHooks().get("WorldGuard")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fWorldGuard&a!"));
                }
            } else {
                getConfigGestion().getHooks().replace("WorldGuard", false);
            }
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
                if (getConfigGestion().getHooks().get("SuperiorSkyblock2")) {
                    Bukkit.getServer().getConsoleSender().sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fSuperiorSkyblock2&a!"));
                }
            } else {
                getConfigGestion().getHooks().replace("SuperiorSkyblock2", false);
            }
        });
        getServer().getConsoleSender().sendMessage("§aConfiguration Loaded!");
        getServer().getConsoleSender().sendMessage("§6Connection to database!");
        if (getConfigGestion().getDatabase().equalsIgnoreCase("H2")) {
            try {
                db = new H2Database(getDataFolder().getAbsolutePath());
            } catch (Exception ex) {
                getServer().getConsoleSender().sendMessage("§cTry to restore the database");
                restoreDatabase();
                ex.printStackTrace();
                return;
            }
        } else {
            db = new YamlDB();
        }
        getServer().getConsoleSender().sendMessage("§aDatabase connected!");
        // control if challenges is on db but is disabled on config
        db.controlIfChallengeExist(config.getControlIfChallengeExist());
        // select challenge
        String typeChallenge = db.insertDailyChallenges();
        if (typeChallenge.equalsIgnoreCase("BlockPlaceChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new BlockPlaceEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("BlockBreakChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("CraftingChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new CraftingEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("CookerChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new FurnaceBurnEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("ConsumeChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ItemConsumeEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("CollectorExpChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ExpCollector(), this);
        } else if (typeChallenge.equalsIgnoreCase("KillChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new KillMobEvent(), this);
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
        } else if (typeChallenge.equalsIgnoreCase("SprintChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new SprintEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("MoveChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new MoveEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("DamageChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new DamageEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("SneakChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new SneakEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("ItemBreakChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ItemBreakEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("SpongeAbsorbChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new SpongeAbsorbeEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("HarvestChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new HarvestEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("EggThrowerChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new EggThrowEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("EnchantChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new EnchantEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("ChatChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ChatEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("ItemCollectionChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new ItemCollector(), this);
        } else if (typeChallenge.equalsIgnoreCase("InventoryConditionChallenge")) {
            new InventoryCheck();
        } else if (typeChallenge.equalsIgnoreCase("VehicleMoveChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new VehicleMoveEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("JumpChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new JumpEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("DyerChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new DyeEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("CubeGeneratorChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new CubeGeneratorEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("DropperChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new DropperEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("HealthChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new HealthRegenEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("AFKChallenge")) {
            new AFKCheck();
        } else if (typeChallenge.equalsIgnoreCase("MissionChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new SuperiorSkyBlock2Events(), this);
        } else if (typeChallenge.equalsIgnoreCase("SensorChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new GameBlockEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("PrimerChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new PrimeEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("FireCatcherChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new FireCatcher(), this);
        } else if (typeChallenge.equalsIgnoreCase("EntityCatcherChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new EntityCatcherEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("LeashChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new LeashEvent(), this);
        } else if (typeChallenge.equalsIgnoreCase("SleepChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new SleepEvent(), this);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No DailyChallenge selected control the configurations files and restart the plugin!");
            challengeSelected = false;
        }

        if (challengeSelected) {
            Bukkit.getServer().getPluginManager().registerEvents(new DailyGiveWinners(), this);
            Bukkit.getServer().getPluginManager().registerEvents(new GuiEvent(), this);
            db.loadPlayersPoints();
            config.getTasks().checkStartDay();
            if (config.getTimeBrodcastMessageTitle() != 0) {
                config.getTasks().broadcast(((long) config.getTimeBrodcastMessageTitle() * 60 * 20)
                        , dailyChallenge
                        , config.getMessages().get("ActuallyInTop")
                        , config.getMessages().get("PointsEveryMinutes")
                        , config.getMessages().get("PointsRemainForBoosting")
                        , config.getMessages().get("PointsRemainForBoostingSinglePlayer")
                        , config.getNumberOfTop()
                );
            }
            if (config.isActiveOnlinePoints()) {
                config.getTasks().onlinePoints(config.getMinutesOnlinePoints(), config.getPointsOnlinePoints());
            }
        }

        getCommand("vc").setExecutor((CommandExecutor) new Commands());
        if (config.getDebug().get("Enabled")) {
            debugsistem.addLine("Enabled execution time= " + (System.currentTimeMillis() - tempo));
            debugsistem.debug();
        }
    }

    public void onDisable() {
        DebugUtils debugSystem = new DebugUtils("Disabled");
        long tempo = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "VanillaChallenges has been disabled, §cBye bye! §e:(");
        config.getTasks().stopAllTasks();
        if (getConfigGestion().getHooks().get("PlaceholderAPI")) {
            try {
                Main.EPAPI.getInstance().unregister();
            } catch (Exception e) {
            }
        }
        if (challengeSelected) {
            dailyChallenge.clearPlayers();
            //close interfaces of interfaces
            for (Map.Entry<String, Interface> interfaces : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
                interfaces.getValue().closeAllInventories();
            }
        }
        db.disconnect();
        if (config.getDebug().get("Disabled")) {
            debugSystem.addLine("Disabled execution time= " + (System.currentTimeMillis() - tempo));
            debugSystem.debug();
        }
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

    private void loadLibraries() {
        final List<Library> libraries = new ArrayList<>();

        boolean oldVersion = getServer().getVersion().contains("1.8") || getServer().getVersion().contains("1.9")
                || getServer().getVersion().contains("1.10") || getServer().getVersion().contains("1.11")
                || getServer().getVersion().contains("1.12") || getServer().getVersion().contains("1.13")
                || getServer().getVersion().contains("1.14") || getServer().getVersion().contains("1.15")
                || getServer().getVersion().contains("1.16");

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
        ReloadUtils.reload();
    }
}
