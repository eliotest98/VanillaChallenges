package io.eliotesta98.VanillaChallenges.Database;

import com.HeroxWar.HeroxCore.TimeGesture.Time;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class YamlDB extends Database {

    private FileConfiguration file;
    private File configFile;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(YamlDB.class.getName());

    public YamlDB() {
        initialize();
    }

    @Override
    public void initialize() {
        configFile = new File(Main.instance.getDataFolder(), "database.yml");

        if (!configFile.exists()) {
            boolean create = false;
            try {
                create = configFile.createNewFile();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
            if (create) {
                file = YamlConfiguration.loadConfiguration(configFile);
            } else {
                int peacefulTime = file.getInt("PeacefulTime", 0);
                setPeacefulTime(new Time((long) peacefulTime, ':'));
                for (String playerName : file.getConfigurationSection("Points").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("Points." + playerName));
                    addPlayerPoints(challenger);
                }
                for (String playerName : file.getConfigurationSection("PointsLastChallenge").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("PointsLastChallenge." + playerName));
                    addOldPoints(challenger);
                }
                for (String challenge : file.getConfigurationSection("Challenges").getKeys(false)) {
                    Challenge challengeDB = new Challenge();
                    challengeDB.setChallengeName(challenge);
                    long timeResume = file.getInt("Challenges." + challenge);
                    // TODO to remove (this is for servers with hours configured Ex. 19)
                    if (timeResume < 1000) {
                        timeResume = timeResume * 60 * 60 * 1000;
                    }
                    challengeDB.setTimeChallenge(new Time(timeResume, ':'));
                    addChallenge(challengeDB);
                }
                for (String number : file.getConfigurationSection("DailyWinners").getKeys(false)) {
                    DailyWinner dailyWinner = new DailyWinner(Integer.parseInt(number),
                            file.getString("DailyWinners." + number + ".PlayerName"),
                            file.getString("DailyWinners." + number + ".NomeChallenge"),
                            file.getString("DailyWinners." + number + ".Reward")
                    );
                    addDailyWinner(dailyWinner);
                }
                for (String playerName : file.getConfigurationSection("TopYesterday").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("TopYesterday." + playerName));
                    addTopYesterday(challenger);
                }
                for (String playerName : file.getConfigurationSection("Statistic").getKeys(false)) {
                    PlayerStats playerStats = new PlayerStats(playerName,
                            file.getInt("Statistic." + playerName + ".NumberVictories"),
                            file.getInt("Statistic." + playerName + ".NumberFirstPlace"),
                            file.getInt("Statistic." + playerName + ".NumberSecondPlace"),
                            file.getInt("Statistic." + playerName + ".NumberThirdPlace")
                    );
                    addStat(playerStats);
                }
            }
        } else {
            file = YamlConfiguration.loadConfiguration(configFile);

            int peacefulTime = file.getInt("PeacefulTime", 0);
            setPeacefulTime(new Time((long) peacefulTime, ':'));
            if (file.getConfigurationSection("Points") != null) {
                for (String playerName : file.getConfigurationSection("Points").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("Points." + playerName));
                    addPlayerPoints(challenger);
                }
            }
            if (file.getConfigurationSection("PointsLastChallenge") != null) {
                for (String playerName : file.getConfigurationSection("PointsLastChallenge").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("PointsLastChallenge." + playerName));
                    addOldPoints(challenger);
                }
            }
            if (file.getConfigurationSection("Challenges") != null) {
                for (String challenge : file.getConfigurationSection("Challenges").getKeys(false)) {
                    Challenge challengeDB = new Challenge();
                    challengeDB.setChallengeName(challenge);
                    long timeResume = file.getInt("Challenges." + challenge);
                    // TODO to remove (this is for servers with hours configured Ex. 19)
                    if (timeResume < 1000) {
                        timeResume = timeResume * 60 * 60 * 1000;
                    }
                    challengeDB.setTimeChallenge(new Time(timeResume, ':'));
                    addChallenge(challengeDB);
                }
            }
            if (file.getConfigurationSection("DailyWinners") != null) {
                for (String number : file.getConfigurationSection("DailyWinners").getKeys(false)) {
                    DailyWinner dailyWinner = new DailyWinner(Integer.parseInt(number), file.getString("DailyWinners." + number + ".PlayerName"), file.getString("DailyWinners." + number + ".NomeChallenge"), file.getString("DailyWinners." + number + ".Reward"));
                    addDailyWinner(dailyWinner);
                }
            }
            if (file.getConfigurationSection("TopYesterday") != null) {
                for (String playerName : file.getConfigurationSection("TopYesterday").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("TopYesterday." + playerName));
                    addTopYesterday(challenger);
                }
            }
            if (file.getConfigurationSection("Statistic") != null) {
                for (String playerName : file.getConfigurationSection("Statistic").getKeys(false)) {
                    PlayerStats playerStats = new PlayerStats(playerName,
                            file.getInt("Statistic." + playerName + ".NumberVictories"),
                            file.getInt("Statistic." + playerName + ".NumberFirstPlace"),
                            file.getInt("Statistic." + playerName + ".NumberSecondPlace"),
                            file.getInt("Statistic." + playerName + ".NumberThirdPlace")
                    );
                    addStat(playerStats);
                }
            }
        }
    }

    @Override
    public void createConnection(String absolutePath) {
    }

    @Override
    public void saveTopYesterday(List<Challenger> newTopYesterday) {
        for (Challenger challenger : newTopYesterday) {
            file.set("TopYesterday." + challenger.getNomePlayer(), challenger.getPoints());
            addTopYesterday(challenger);
        }
        saveFile();
    }

    @Override
    public void removeTopYesterday() {
        for (Challenger challenger : getTopYesterday()) {
            file.set("TopYesterday." + challenger.getNomePlayer(), null);
        }
        getTopYesterday().clear();
        saveFile();
    }

    @Override
    public void saveChallenges() {
        for (Challenge challenge : getChallenges()) {
            file.set("Challenges." + challenge.getChallengeName(), challenge.getTimeChallenge().getMilliseconds());
        }
        saveFile();
    }

    @Override
    public void disconnect() {
    }

    @Override
    public void insertPlayerStat(PlayerStats playerStats) {
        file.set("Statistic." + playerStats.getPlayerName() + ".NumberVictories", playerStats.getNumberOfVictories());
        file.set("Statistic." + playerStats.getPlayerName() + ".NumberFirstPlace", playerStats.getNumberOfFirstPlace());
        file.set("Statistic." + playerStats.getPlayerName() + ".NumberSecondPlace", playerStats.getNumberOfSecondPlace());
        file.set("Statistic." + playerStats.getPlayerName() + ".NumberThirdPlace", playerStats.getNumberOfThirdPlace());
        addStat(playerStats);
        saveFile();
    }

    @Override
    public void insertPeacefulTime(Time time) {
        file.set("PeacefulTime", time.getMilliseconds());
        setPeacefulTime(time);
        saveFile();
    }

    @Override
    public void updatePeacefulTime(Time time) {
        file.set("PeacefulTime", time.getMilliseconds());
        setPeacefulTime(time);
        saveFile();
    }

    @Override
    public void deletePlayerStatWithPlayerName(String playerName) {
        file.set("Statistic." + playerName, null);
        removeStat(playerName);
        saveFile();
    }

    @Override
    public void deleteChallengeWithName(String challengeName) {
        file.set("Challenges." + challengeName, null);
        removeChallenge(challengeName);
        saveFile();
    }

    @Override
    public void updateChallenge(String nomeChallenge, long timeRemain) {
        file.set("Challenges." + nomeChallenge, timeRemain);
        updateChallengeTime(nomeChallenge, timeRemain);
        saveFile();
    }

    @Override
    public void updateChallenger(String playerName, long points) {
        file.set("Points." + playerName, points);
        updatePlayer(playerName, points);
        saveFile();
    }

    @Override
    public void insertChallengerEvent(String playerName, long value) {
        file.set("PointsLastChallenge." + playerName, (int) value);
        addOldPoints(new Challenger(playerName, value));
        saveFile();
    }

    @Override
    public void insertChallengerTopYesterday(String playerName, long points) {
        file.set("TopYesterday." + playerName, points);
        addTopYesterday(new Challenger(playerName, points));
        saveFile();
    }

    @Override
    public void insertChallenger(String playerName, long value) {
        addPlayerPoints(new Challenger(playerName, value));
        file.set("Points." + playerName, (int) value);
        saveFile();
    }

    @Override
    public void clearChallengesFromFile() {
        file.set("Challenges", null);
        saveFile();
    }

    @Override
    public void clearStats() {
        file.set("Statistic", null);
        getAllStats().clear();
        saveFile();
    }

    @Override
    public void insertChallenge(String challengeName, long timeResume) {
        file.set("Challenges." + challengeName, timeResume);
        saveFile();
    }

    @Override
    public void clearChallengersOld() {
        file.set("PointsLastChallenge", null);
        getOldPoints().clear();
        saveFile();
    }

    @Override
    public void deleteChallengerWithName(String playerName) {
        file.set("Points." + playerName, null);
        removePlayer(playerName);
        saveFile();
    }

    @Override
    public void clearChallengers() {
        file.set("Points", null);
        getPlayerPoints().clear();
        saveFile();
    }

    @Override
    public void clearChallenges() {
        file.set("Challenges", null);
        getChallenges().clear();
        saveFile();
    }

    @Override
    public void clearDailyWinners() {
        file.set("DailyWinners", null);
        getDailyWinners().clear();
        saveFile();
    }

    @Override
    public void clearPeacefulTime() {
        file.set("PeacefulTime", null);
        setPeacefulTime(new Time(0, ':'));
        saveFile();
    }

    @Override
    public void deleteDailyWinnerWithId(int id) {
        file.set("DailyWinners." + id, null);
        removeDailyWinner(id);
        saveFile();
    }

    @Override
    public void insertDailyWinner(DailyWinner dailyWinner) {
        file.set("DailyWinners." + dailyWinner.getId() + ".PlayerName", dailyWinner.getPlayerName());
        file.set("DailyWinners." + dailyWinner.getId() + ".NomeChallenge", dailyWinner.getNomeChallenge());
        file.set("DailyWinners." + dailyWinner.getId() + ".Reward", dailyWinner.getReward());
        addDailyWinner(dailyWinner);
        saveFile();
    }

    @Override
    public void insertChallengeEvent(String challengeName, long timeResume) {
        Challenge challenge = Main.instance.getConfigGesture().getChallengesEvent().get(challengeName).cloneChallenge();
        challenge.setChallengeName("Event_" + challengeName);
        challenge.setTimeChallenge(new Time(timeResume, ':'));
        addChallenge(challenge, 0);
        clearChallengesFromFile();
        for (Challenge challenge1 : getChallenges()) {
            insertChallenge(challenge1.getChallengeName(), challenge1.getTimeChallenge().getMilliseconds());
        }
        saveFile();
    }

    @Override
    public void clearAll() {
        configFile.delete();
    }

    public void saveFile() {
        try {
            file.save(configFile);
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

}
