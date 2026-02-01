package io.eliotesta98.VanillaChallenges.Database;

import com.HeroxWar.HeroxCore.MessageGesture;
import com.HeroxWar.HeroxCore.TimeGesture.Time;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.MoneyUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;

public abstract class Database {

    private final List<Challenger> playerPoints = new ArrayList<>();
    private final List<Challenge> challenges = new ArrayList<>();
    private final List<DailyWinner> dailyWinners = new ArrayList<>();
    private final List<Challenger> topYesterday = new ArrayList<>();
    private final List<Challenger> oldPoints = new ArrayList<>();
    private final List<PlayerStats> stats = new ArrayList<>();
    private Time peacefulTime = new Time(-1, ':');
    private String prefix = "";
    private Connection connection;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Database.class.getName());

    public abstract void createConnection(String absolutePath) throws SQLException;

    public void initialize() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + prefix + "Challenge (`NomeChallenge` VARCHAR(100) NOT NULL PRIMARY KEY, `TimeResume` INT(15) NOT NULL);");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + prefix + "Challenger (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + prefix + "ChallengerEvent (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + prefix + "DailyWinner (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `NomeChallenge` VARCHAR(100) NOT NULL, `PlayerName` VARCHAR(100) NOT NULL, `Reward` VARCHAR(100) NOT NULL);");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + prefix + "TopYesterday (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `PlayerName` VARCHAR(100) NOT NULL, `Points` INT(15) NOT NULL);");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + prefix + "Statistic (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `NumberVictories` INT(10) NOT NULL, `NumberFirstPlace` INT(10) NOT NULL, `NumberSecondPlace` INT(10) NOT NULL, `NumberThirdPlace` INT(10) NOT NULL);");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        preparedStatement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + prefix + "PeacefulTime (`ID` INT(1) NOT NULL AUTO_INCREMENT PRIMARY KEY, `Time` INT(100) NOT NULL);");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        selectAllChallenges();
        selectAllChallengers();
        selectAllStats();
        selectAllDailyWinners();
        selectAllChallengersTopYesterday();
        selectAllOldChallengers();
        loadPeacefulTime();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public List<PlayerStats> getAllStats() {
        return stats;
    }

    public List<Challenger> getTopYesterday() {
        return topYesterday;
    }

    public List<Challenger> getPlayerPoints() {
        return playerPoints;
    }

    public List<Challenger> getOldPoints() {
        return oldPoints;
    }

    public List<DailyWinner> getDailyWinners() {
        return dailyWinners;
    }

    public Time getPeacefulTime() {
        return peacefulTime;
    }

    public void addPlayerPoints(Challenger challenger) {
        this.playerPoints.add(challenger);
    }

    public void addChallenge(Challenge challenge) {
        this.challenges.add(challenge);
    }

    public void setPeacefulTime(Time time) {
        this.peacefulTime = time;
    }

    public void addChallenge(Challenge challenge, int index) {
        this.challenges.add(index, challenge);
    }

    public void addDailyWinner(DailyWinner dailyWinner) {
        this.dailyWinners.add(dailyWinner);
    }

    public void addOldPoints(Challenger challenger) {
        this.oldPoints.add(challenger);
    }

    public void addTopYesterday(Challenger challenger) {
        this.topYesterday.add(challenger);
    }

    public void addStat(PlayerStats playerStats) {
        this.stats.add(playerStats);
    }

    public void removeStat(String playerName) {
        this.stats.removeIf((PlayerStats playerStat) -> playerStat.getPlayerName().equalsIgnoreCase(playerName));
    }

    public void removeChallenge(String challengeName) {
        this.challenges.removeIf((Challenge challenge) -> challenge.getChallengeName().equalsIgnoreCase(challengeName));
    }

    public void removePlayer(String playerName) {
        this.playerPoints.removeIf((Challenger challenger) -> challenger.getNomePlayer().equalsIgnoreCase(playerName));
    }

    public void removeDailyWinner(int id) {
        dailyWinners.removeIf((DailyWinner dailyWinner) -> dailyWinner.getId() == id);
    }

    public void updatePlayer(String playerName, long points) {
        removePlayer(playerName);
        addPlayerPoints(new Challenger(playerName, points));
    }

    public void updateChallengeTime(String challengeName, long timeRemain) {
        for (Challenge challenge : challenges) {
            if (challenge.getChallengeName().equalsIgnoreCase(challengeName)) {
                challenge.setTimeChallenge(new Time(timeRemain, ':'));
            }
        }
    }

    public void selectAllChallenges() {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + prefix + "Challenge");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Challenge challengeDB = new Challenge();
                // TODO to remove (this is for servers with hours configured Ex. 19)
                long timeResume = resultSet.getInt("TimeResume");
                if (timeResume < 1000) {
                    timeResume = timeResume * 60 * 60 * 1000;
                }
                challengeDB.setTimeChallenge(new Time(timeResume, ':'));
                challengeDB.setChallengeName(resultSet.getString("NomeChallenge"));
                challenges.add(challengeDB);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void selectAllStats() {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + prefix + "Statistic");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final PlayerStats playerStats = new PlayerStats();
                playerStats.setPlayerName(resultSet.getString("PlayerName"));
                playerStats.setNumberOfVictories(resultSet.getInt("NumberVictories"));
                playerStats.setNumberOfFirstPlace(resultSet.getInt("NumberFirstPlace"));
                playerStats.setNumberOfSecondPlace(resultSet.getInt("NumberSecondPlace"));
                playerStats.setNumberOfThirdPlace(resultSet.getInt("NumberThirdPlace"));
                stats.add(playerStats);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void selectAllChallengers() {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + prefix + "Challenger");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Challenger point = new Challenger();
                point.setPoints(resultSet.getInt("Points"));
                point.setNomePlayer(resultSet.getString("PlayerName"));
                playerPoints.add(point);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void selectAllDailyWinners() {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + prefix + "DailyWinner");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final DailyWinner dailyWinner = new DailyWinner();
                dailyWinner.setId(resultSet.getInt("ID"));
                dailyWinner.setNomeChallenge(resultSet.getString("NomeChallenge"));
                dailyWinner.setPlayerName(resultSet.getString("PlayerName"));
                dailyWinner.setReward(resultSet.getString("Reward"));
                dailyWinners.add(dailyWinner);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void selectAllChallengersTopYesterday() {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + prefix + "TopYesterday");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Challenger point = new Challenger();
                point.setPoints(resultSet.getInt("Points"));
                point.setNomePlayer(resultSet.getString("PlayerName"));
                topYesterday.add(point);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void selectAllOldChallengers() {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + prefix + "ChallengerEvent");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Challenger point = new Challenger();
                point.setPoints(resultSet.getInt("Points"));
                point.setNomePlayer(resultSet.getString("PlayerName"));
                oldPoints.add(point);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void loadPeacefulTime() {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + prefix + "PeacefulTime");
            resultSet = preparedStatement.executeQuery();
            int time = -1;
            while (resultSet.next()) {
                time = resultSet.getInt("Time");
            }
            if (time == -1) {
                insertPeacefulTime(new Time(0, ':'));
            } else {
                peacefulTime = new Time((long) time, ':');
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void insertPeacefulTime(Time time) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + prefix + "PeacefulTime (Time) VALUES ('"
                            + time.getMilliseconds() + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
            setPeacefulTime(time);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void backupDb(int numberOfFiles) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String data = sdf.format(timestamp);
        File configFile = new File(Main.instance.getDataFolder() +
                File.separator + "backup", data + ".yml");
        if (!configFile.exists()) {
            try {
                File folder = new File(Main.instance.getDataFolder() +
                        File.separator + "backup");
                boolean folderCreate = folder.mkdir();
                if (!folderCreate) {
                    if (folder.listFiles().length >= numberOfFiles) {
                        java.util.Date finalDate = null;
                        int number = 0;
                        for (int i = 0; i < folder.listFiles().length; i++) {
                            try {
                                Date date = sdf.parse(folder.listFiles()[i].getName());
                                if (finalDate == null) {
                                    finalDate = date;
                                    number = i;
                                } else {
                                    int result = finalDate.compareTo(date);
                                    if (result > 0) {
                                        finalDate = date;
                                        number = i;
                                    }
                                }
                            } catch (ParseException e) {
                                logger.log(Level.WARNING, e.getMessage());
                            }
                        }
                        folder.listFiles()[number].delete();
                    }
                }
                configFile.createNewFile();
                YamlConfiguration file = YamlConfiguration.loadConfiguration(configFile);
                for (Challenger playerPoint : playerPoints) {
                    file.set("Points." + playerPoint.getNomePlayer(), playerPoint.getPoints());
                }
                for (Challenge challenge : challenges) {
                    file.set("Challenges." + challenge.getChallengeName(), challenge.getTimeChallenge().getMilliseconds());
                }
                for (DailyWinner dailyWinner : dailyWinners) {
                    file.set("DailyWinners." + dailyWinner.getId() + ".PlayerName", dailyWinner.getPlayerName());
                    file.set("DailyWinners." + dailyWinner.getId() + ".NomeChallenge", dailyWinner.getNomeChallenge());
                    file.set("DailyWinners." + dailyWinner.getId() + ".Reward", dailyWinner.getReward());
                }
                for (Challenger challenger : topYesterday) {
                    file.set("TopYesterday." + challenger.getNomePlayer(), challenger.getPoints());
                }
                for (PlayerStats playerStats : stats) {
                    file.set("Statistic." + playerStats.getPlayerName() + ".NumberVictories", playerStats.getNumberOfVictories());
                    file.set("Statistic." + playerStats.getPlayerName() + ".NumberFirstPlace", playerStats.getNumberOfFirstPlace());
                    file.set("Statistic." + playerStats.getPlayerName() + ".NumberSecondPlace", playerStats.getNumberOfSecondPlace());
                    file.set("Statistic." + playerStats.getPlayerName() + ".NumberThirdPlace", playerStats.getNumberOfThirdPlace());
                }
                file.save(configFile);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
    }

    public void clearAll() {
        clearChallenges();
        clearChallengers();
        clearChallengersOld();
        clearDailyWinners();
        removeTopYesterday();
        clearStats();
        clearPeacefulTime();
    }

    // Challenges Querys

    public String insertDailyChallenges() {
        int count = 1;
        String schedulerType = Main.instance.getConfigGestion().getChallengeGeneration();
        List<String> keys = new ArrayList<>(Main.instance.getConfigGestion().getChallenges().keySet());
        if (challenges.isEmpty()) {
            String nome = "nobody";
            if (schedulerType.equalsIgnoreCase("Random")) {
                Collections.shuffle(keys);
                Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(keys.get(0));
                Main.instance.setDailyChallenge(challenge);
                nome = challenge.getTypeChallenge();
            } else if (schedulerType.equalsIgnoreCase("Single")) {
                Collections.shuffle(keys);
                Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(keys.get(0));
                Main.instance.setDailyChallenge(challenge);
                return challenge.getTypeChallenge();
            } else if (schedulerType.equalsIgnoreCase("Normal")) {
                for (String key : keys) {
                    Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(key);
                    if (count == 1) {
                        Main.instance.setDailyChallenge(challenge);
                        nome = challenge.getTypeChallenge();
                    }
                    challenges.add(challenge);
                    count++;
                }
            } else if (schedulerType.equalsIgnoreCase("Nothing")) {
                clearChallenges();
                return nome;
            }
            saveChallenges();
            return nome;
        } else {
            if (schedulerType.equalsIgnoreCase("Single")) {
                challenges.clear();
                clearChallenges();
                Collections.shuffle(keys);
                Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(keys.get(0));
                Main.instance.setDailyChallenge(challenge);
                return challenge.getTypeChallenge();
            } else if (schedulerType.equalsIgnoreCase("Nothing")) {
                if (!checkIfEventChallenge()) {
                    challenges.clear();
                    clearChallenges();
                    return "nobody";
                }
            }
            for (int i = 0; i < challenges.size(); i++) {
                if (challenges.get(i).getTimeChallenge().getMilliseconds() <= 0) {
                    deleteChallengeWithName(challenges.get(i).getChallengeName());
                    challenges.remove(i);
                } else {
                    if (challenges.get(i).getChallengeName().contains("Event_")) {
                        Challenge challenge = Main.instance.getConfigGestion().getChallengesEvent().get(challenges.get(i).getChallengeName().replace("Event_", ""));
                        challenge.setTimeChallenge(challenges.get(i).getTimeChallenge());
                        Main.instance.setDailyChallenge(challenge);
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Vanilla Challenges] " + challenges.size() + " challenges remain on DB");
                        return Main.instance.getDailyChallenge().getTypeChallenge();
                    }
                    Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(challenges.get(i).getChallengeName());
                    challenge.setTimeChallenge(challenges.get(i).getTimeChallenge());
                    Main.instance.setDailyChallenge(challenge);
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Vanilla Challenges] " + challenges.size() + " challenges remain on DB");
                    return Main.instance.getDailyChallenge().getTypeChallenge();
                }
            }
            return "nobody";
        }
    }

    // This method has been invoked when the database doesn't have any challenge scheduled
    public void saveChallenges() {
        for (Challenge challenge : challenges) {
            insertChallenge(challenge.getChallengeName(), challenge.getTimeChallenge().getMilliseconds());
        }
    }

    // This method has been invoked when from a command is invoked the adding of a new Event Challenge
    public void insertChallengeEvent(String challengeName, long timeResume) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + prefix + "Challenge (NomeChallenge,TimeResume) VALUES ('"
                            + "Event_" + challengeName + "','" + timeResume
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
            Challenge challenge = Main.instance.getConfigGestion().getChallengesEvent().get(challengeName).cloneChallenge();
            challenge.setChallengeName("Event_" + challengeName);
            challenge.setTimeChallenge(new Time(timeResume, ':'));
            addChallenge(challenge, 0);
            clearChallengesFromFile();
            for (Challenge challenge1 : getChallenges()) {
                insertChallenge(challenge1.getChallengeName(), challenge1.getTimeChallenge().getMilliseconds());
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void clearChallengesFromFile() {
        clearChallenges();
    }

    public void clearChallenges() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE " + prefix + "Challenge");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "Challenge (`NomeChallenge` VARCHAR(100) NOT NULL PRIMARY KEY, `TimeResume` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void clearChallengersOld() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE " + prefix + "ChallengerEvent");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "ChallengerEvent (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            oldPoints.clear();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void deleteChallengeWithName(String challengeName) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM " + prefix + "Challenge WHERE `NomeChallenge`='" + challengeName + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            removeChallenge(challengeName);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void insertChallenger(String playerName, long points) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + prefix + "Challenger (PlayerName,Points) VALUES ('"
                            + playerName + "','" + points
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
            addPlayerPoints(new Challenger(playerName, points));
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void updateChallenge(String challengeName, long timeResume) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE " + prefix + "Challenge SET TimeResume = '" + timeResume + "' WHERE NomeChallenge = '" + challengeName + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            updateChallengeTime(challengeName, timeResume);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void updatePeacefulTime(Time time) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE " + prefix + "PeacefulTime SET Time = '" + time.getMilliseconds() + "' WHERE ID = '" + "1" + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            setPeacefulTime(time);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void controlIfChallengeExist(List<String> controlIfChallengeExist) {
        for (String challengeName : controlIfChallengeExist) {
            for (Challenge challenge : challenges) {
                if (challenge.getChallengeName().equalsIgnoreCase(challengeName)) {
                    challenges.remove(challenge);
                    deleteChallengeWithName(challengeName);
                    break;
                }
            }
        }
    }

    public boolean checkPeacefulTime() {
        return peacefulTime.getMilliseconds() > 0;
    }

    public void insertChallenge(String challengeName, long timeResume) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + prefix + "Challenge (NomeChallenge,TimeResume) VALUES ('"
                            + challengeName + "','" + timeResume
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public boolean isChallengePresent(String challengeName) {
        return challenges.stream().anyMatch((Challenge challenge) -> challenge.getChallengeName().equalsIgnoreCase(challengeName));
    }

    // Players Querys

    public void loadPlayersPoints() {
        Main.instance.getDailyChallenge().setPlayers(playerPoints);
        Main.instance.getDailyChallenge().savePoints();
        List<Challenger> top = Main.instance.getDailyChallenge().getTopPlayers(Main.instance.getConfigGestion().getNumberOfTop());
        int i = 1;
        for (Challenger challenger : top) {
            MessageGesture.sendMessage(Bukkit.getServer().getConsoleSender(), Main.instance.getConfigGestion().getMessages().get("TopPlayers" + i)
                    .replace("{number}", "" + i)
                    .replace("{player}", challenger.getNomePlayer())
                    .replace("{points}", MoneyUtils.transform(challenger.getPoints())));
            i++;
        }
    }

    public boolean isPresent(String playerName) {
        return playerPoints.stream().anyMatch((Challenger challenger) -> challenger.getNomePlayer().equalsIgnoreCase(playerName));
    }

    public void clearChallengers() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DROP TABLE " + prefix + "Challenger");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "Challenger (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            playerPoints.clear();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void saveOldPointsForChallengeEvents() {
        HashMap<String, Long> copyMap = new HashMap<>(Main.instance.getDailyChallenge().getPlayers());
        for (Map.Entry<String, Long> player : copyMap.entrySet()) {
            try {
                if (player.getValue() > 0) {
                    insertChallengerEvent(player.getKey(), player.getValue());
                }
            } catch (Exception ex) {
                Bukkit.getServer().getConsoleSender().sendMessage("Save Points Event: " + ex.getMessage());
            }
        }
        clearChallengers();
    }

    public void insertChallengerEvent(String playerName, long points) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + prefix + "ChallengerEvent (PlayerName,Points) VALUES ('"
                            + playerName + "','" + points
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
            oldPoints.add(new Challenger(playerName, points));
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void deleteChallengerWithName(String nomePlayer) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM " + prefix + "Challenger WHERE `PlayerName`='" + nomePlayer + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            removePlayer(nomePlayer);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void updateChallenger(String playerName, long points) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE " + prefix + "Challenger SET Points = '" + points + "' WHERE PlayerName = '" + playerName + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            updatePlayer(playerName, points);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void resumeOldPoints() {
        clearChallengers();
        for (Challenger challenger : oldPoints) {
            insertChallenger(challenger.getNomePlayer(), challenger.getPoints());
        }
        clearChallengersOld();
    }

    // Statistics Querys

    public void insertPlayerStat(PlayerStats playerStats) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + prefix + "Statistic (PlayerName,NumberVictories,NumberFirstPlace,NumberSecondPlace,NumberThirdPlace) VALUES ('"
                            + playerStats.getPlayerName() + "','" + playerStats.getNumberOfVictories() + "','"
                            + playerStats.getNumberOfFirstPlace() + "','" + playerStats.getNumberOfSecondPlace() + "','"
                            + playerStats.getNumberOfThirdPlace()
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
            addStat(playerStats);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void deletePlayerStatWithPlayerName(String playerName) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM " + prefix + "Statistic WHERE `PlayerName`='" + playerName + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            removeStat(playerName);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void updatePlayerStat(PlayerStats playerStats) {
        for (PlayerStats playerStats1 : stats) {
            if (playerStats1.getPlayerName().equalsIgnoreCase(playerStats.getPlayerName())) {
                deletePlayerStatWithPlayerName(playerStats1.getPlayerName());
                insertPlayerStat(playerStats);
                return;
            }
        }
        insertPlayerStat(playerStats);
    }

    public PlayerStats getStatsPlayer(String playerName) {
        for (PlayerStats playerStats : stats) {
            if (playerStats.getPlayerName().equalsIgnoreCase(playerName)) {
                return playerStats;
            }
        }
        return null;
    }

    public List<PlayerStats> getTopVictories() {
        List<PlayerStats> top = new ArrayList<>(stats);
        top.sort(Comparator.comparing(PlayerStats::getNumberOfVictories));
        Collections.reverse(top);
        return top;
    }

    public List<PlayerStats> getTopFirstPlace() {
        List<PlayerStats> top = new ArrayList<>(stats);
        top.sort(Comparator.comparing(PlayerStats::getNumberOfFirstPlace));
        Collections.reverse(top);
        return top;
    }

    public List<PlayerStats> getTopSecondPlace() {
        List<PlayerStats> top = new ArrayList<>(stats);
        top.sort(Comparator.comparing(PlayerStats::getNumberOfSecondPlace));
        Collections.reverse(top);
        return top;
    }

    public List<PlayerStats> getTopThirdPlace() {
        List<PlayerStats> top = new ArrayList<>(stats);
        top.sort(Comparator.comparing(PlayerStats::getNumberOfThirdPlace));
        Collections.reverse(top);
        return top;
    }

    public void clearStats() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE " + prefix + "Statistic");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "Statistic (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `NumberVictories` INT(10) NOT NULL, `NumberFirstPlace` INT(10) NOT NULL, `NumberSecondPlace` INT(10) NOT NULL, `NumberThirdPlace` INT(10) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            stats.clear();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void clearPeacefulTime() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE " + prefix + "PeacefulTime");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "PeacefulTime (`ID` INT(1) NOT NULL AUTO_INCREMENT PRIMARY KEY, `Time` INT(100) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            stats.clear();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public boolean isPlayerHaveStats(String playerName) {
        return stats.stream().anyMatch((PlayerStats playerStats) -> playerStats.getPlayerName().equalsIgnoreCase(playerName));
    }

    // Top Yesterday Querys

    public void removeTopYesterday() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DROP TABLE " + prefix + "TopYesterday");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "TopYesterday (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `PlayerName` VARCHAR(100) NOT NULL, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            topYesterday.clear();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void saveTopYesterday(List<Challenger> newTopYesterday) {
        for (Challenger challenger : newTopYesterday) {
            insertChallengerTopYesterday(challenger.getNomePlayer(), challenger.getPoints());
        }
    }

    public void insertChallengerTopYesterday(String playerName, long points) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + prefix + "TopYesterday (PlayerName,Points) VALUES ('"
                            + playerName + "','" + points
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
            addTopYesterday(new Challenger(playerName, points));
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    // Daily Top Querys

    public int lastDailyWinnerId() {
        if (dailyWinners.isEmpty()) {
            return 1;
        }
        return Collections.max(dailyWinners, Comparator.comparing(DailyWinner::getId)).getId();
    }

    public void insertDailyWinner(DailyWinner dailyWinner) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + prefix + "DailyWinner (NomeChallenge,PlayerName,Reward) VALUES ('"
                            + dailyWinner.getNomeChallenge() + "','" + dailyWinner.getPlayerName() + "','" + dailyWinner.getReward()
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
            addDailyWinner(dailyWinner);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void deleteDailyWinnerWithId(int id) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM " + prefix + "DailyWinner WHERE `ID`='" + id + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            removeDailyWinner(id);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void clearDailyWinners() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE " + prefix + "DailyWinner");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + prefix + "DailyWinner (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `NomeChallenge` VARCHAR(100) NOT NULL, `PlayerName` VARCHAR(100) NOT NULL, `Reward` VARCHAR(100) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public boolean checkIfEventChallenge() {
        if (challenges.isEmpty()) {
            return false;
        }
        return challenges.get(0).getChallengeName().contains("Event_");
    }

}
