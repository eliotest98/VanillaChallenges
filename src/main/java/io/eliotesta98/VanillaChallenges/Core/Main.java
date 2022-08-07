package io.eliotesta98.VanillaChallenges.Core;

import io.eliotesta98.VanillaChallenges.Database.*;
import io.eliotesta98.VanillaChallenges.Events.*;
import io.eliotesta98.VanillaChallenges.Utils.DailyGiveWinners;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import io.eliotesta98.VanillaChallenges.Comandi.Commands;
import io.eliotesta98.VanillaChallenges.Utils.*;

import java.io.*;

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
    private OnlinePointsGive onlinePointsEvent;
    public static ExpansionPlaceholderAPI EPAPI;
    private String typeChallenge = "";
    public static Database db;

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
            String splits = "bho";
            String[] strings = splits.split(":");
            cfg.syncWithConfig(configFile, this.getResource(configname), strings);
        } catch (IOException e) {
            e.printStackTrace();
        }

        config = new ConfigGestion(YamlConfiguration.loadConfiguration(configFile));
        // RUNNABLE PER CARICARE LE DIPENDENZE ALLA FINE DELL'AVVIO DEL SERVER :D
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    if (getConfigGestion().getHooks().get("PlaceholderAPI")) {
                        Main.EPAPI = new ExpansionPlaceholderAPI().getInstance();
                        Main.EPAPI.register();
                        Bukkit.getServer().getConsoleSender().sendMessage(
                                ChatColor.translateAlternateColorCodes('&', "&aAdded compatibility to &fPlaceholderApi&a!"));
                    }
                }
            }
        });
        getServer().getConsoleSender().sendMessage("§aConfiguration Loaded!");
        getServer().getConsoleSender().sendMessage("§6Connection to database!");
        if (config.getDatabase().equalsIgnoreCase("H2")) {
            getServer().getConsoleSender().sendMessage("HikariCP Folder '" + com.zaxxer.hikari.HikariConfig.class
                    .getProtectionDomain().getCodeSource().getLocation().getPath() + "'");
            db = new H2Database();
        } else {
            db = new YamlDB();
        }
        getServer().getConsoleSender().sendMessage("§aDatabase connected!");

        typeChallenge = db.insertDailyChallenges();
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
        } else if(typeChallenge.equalsIgnoreCase("DyerChallenge")) {
            Bukkit.getServer().getPluginManager().registerEvents(new DyeEvent(), this);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No DailyChallenge selected control config.yml!");
        }
        Bukkit.getServer().getPluginManager().registerEvents(new DailyGiveWinners(), this);
        db.loadPlayersPoints();
        checkDay = new CheckDay();
        // ogni 60 minuti
        checkDay.start(20 * 60 * 60);
        brodcastDailyChallenge = new BrodcastDailyChallenge();
        if (config.getTimeBrodcastMessageTitle() != 0) {
            brodcastDailyChallenge.start((long) config.getTimeBrodcastMessageTitle() * 60 * 20);
        }
        if (config.isActiveOnlinePoints()) {
            onlinePointsEvent = new OnlinePointsGive();
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
        if (config.getTimeBrodcastMessageTitle() != 0) {
            brodcastDailyChallenge.stop();
        }
        if (config.isActiveOnlinePoints()) {
            onlinePointsEvent.stop();
        }
        if (typeChallenge.equalsIgnoreCase("ChatChallenge")) {
            ChatEvent.stop();
        }
        if (typeChallenge.equalsIgnoreCase("InventoryConditionChallenge")) {
            InventoryCheck.stop();
        }
        if (typeChallenge.equalsIgnoreCase("")) {
            ItemCollector.stop();
        }
        if (getConfigGestion().getHooks().get("PlaceholderAPI")) {
            try {
                Main.EPAPI.getInstance().unregister();
            } catch (Exception e) {
            }
        }
        dailyChallenge.clearPlayers();
        db.disconnect();
        if (config.getDebug().get("Disabled")) {
            debugsistem.addLine("Disabled execution time= " + (System.currentTimeMillis() - tempo));
            debugsistem.debug("Disabled");
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

}
