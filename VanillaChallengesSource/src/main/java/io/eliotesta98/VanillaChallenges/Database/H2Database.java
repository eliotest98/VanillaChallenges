package io.eliotesta98.VanillaChallenges.Database;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.MoneyUtils;
import io.eliotesta98.VanillaChallenges.Utils.ReloadUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class H2Database implements Database {

    public static Connection connection = null;
    public static H2Database instance = null;

    public H2Database(String absolutePath) {
        initialize(absolutePath);
    }

    @Override
    public void initialize(String AbsolutePath) {
        instance = this;
        createConnection(AbsolutePath);
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Challenge (`NomeChallenge` VARCHAR(100) NOT NULL PRIMARY KEY, `TimeResume` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Challenger (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ChallengerEvent (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS DailyWinner (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `NomeChallenge` VARCHAR(100) NOT NULL, `PlayerName` VARCHAR(100) NOT NULL, `Reward` VARCHAR(100) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS TopYesterday (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `PlayerName` VARCHAR(100) NOT NULL, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Statistic (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `NumberVictories` INT(10) NOT NULL, `NumberFirstPlace` INT(10) NOT NULL, `NumberSecondPlace` INT(10) NOT NULL, `NumberThirdPlace` INT(10) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            Main.instance.getServer().getConsoleSender().sendMessage("§cError Database not connected!");
            e.printStackTrace();
            Main.instance.onDisable();
        }
    }

    @Override
    public String insertDailyChallenges() {
        ArrayList<Challenge> challenges = H2Database.instance.getAllChallenges();
        int count = 1;
        if (challenges.isEmpty()) {
            String nome = "nessuno";
            ArrayList<String> keys = new ArrayList<>(Main.instance.getConfigGestion().getChallenges().keySet());
            if (Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Random")) {
                Collections.shuffle(keys);
            } else if (Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Single")) {
                Collections.shuffle(keys);
                Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(keys.get(0));
                return challenge.getChallengeName();
            }
            for (String key : keys) {
                Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(key);

                if (count == 1) {
                    Main.instance.setDailyChallenge(challenge);
                    nome = challenge.getTypeChallenge();
                }
                H2Database.instance.insertChallenge(challenge.getChallengeName(), challenge.getTimeChallenge());
                count++;
            }
            return nome;
        } else {
            for (int i = 0; i < challenges.size(); i++) {
                if (challenges.get(i).getTimeChallenge() <= 0) {
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
            return "nessuno";
        }
    }

    @Override
    public void loadPlayersPoints() {
        Main.instance.getDailyChallenge().setPlayers(H2Database.instance.getAllChallengers());
        Main.instance.getDailyChallenge().savePoints();
        ArrayList<Challenger> top = Main.instance.getDailyChallenge().getTopPlayers(Main.instance.getConfigGestion().getNumberOfRewardPlayer());
        int i = 1;
        while (!top.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + MoneyUtils.transform(top.get(0).getPoints()))));
            top.remove(0);
            i++;
        }
    }

    @Override
    public void clearChallengesFromFile() {
        ArrayList<Challenge> challenges = getAllChallenges();
        challenges.add(0, challenges.get(challenges.size() - 1));
        challenges.remove(challenges.size() - 1);
        clearChallenges();
        for (Challenge challenge : challenges) {
            insertChallenge(challenge.getChallengeName(), challenge.getTimeChallenge());
        }
    }

    public void clearChallenges() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE Challenge");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Challenge (`NomeChallenge` VARCHAR(100) NOT NULL PRIMARY KEY, `TimeResume` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    public void clearChallengersOld() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE ChallengerEvent");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ChallengerEvent (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
    public void clearChallengers() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE Challenger");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Challenger (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearDailyWinners() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE DailyWinner");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS DailyWinner (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `NomeChallenge` VARCHAR(100) NOT NULL, `PlayerName` VARCHAR(100) NOT NULL, `Reward` VARCHAR(100) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeTopYesterday() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE TopYesterday");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS TopYesterday (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `PlayerName` VARCHAR(100) NOT NULL, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearStats() {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE Statistic");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Statistic (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `NumberVictories` INT(10) NOT NULL, `NumberFirstPlace` INT(10) NOT NULL, `NumberSecondPlace` INT(10) NOT NULL, `NumberThirdPlace` INT(10) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveTopYesterday(ArrayList<Challenger> newTopYesterday) {
        for (Challenger challenger : newTopYesterday) {
            insertChallengerTopYesterday(challenger.getNomePlayer(), challenger.getPoints());
        }
    }

    @Override
    public void clearAll() {
        clearChallenges();
        clearChallengers();
        clearChallengersOld();
        clearDailyWinners();
        removeTopYesterday();
        clearStats();
    }

    @Override
    public ArrayList<PlayerStats> getAllPlayerStats() {
        ArrayList<PlayerStats> stats = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Statistic");
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
            e.printStackTrace();
        }
        return stats;
    }

    @Override
    public boolean isPlayerHaveStats(String playerName) {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Statistic WHERE PlayerName = '" + playerName + "'");
            resultSet = preparedStatement.executeQuery();
            boolean result = resultSet.next();
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public PlayerStats getStatsPlayer(String playerName) {
        ResultSet resultSet;
        PlayerStats playerStats = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Statistic WHERE PlayerName = '" + playerName + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                playerStats = new PlayerStats();
                playerStats.setPlayerName(playerName);
                playerStats.setNumberOfVictories(resultSet.getInt("NumberVictories"));
                playerStats.setNumberOfFirstPlace(resultSet.getInt("NumberFirstPlace"));
                playerStats.setNumberOfSecondPlace(resultSet.getInt("NumberSecondPlace"));
                playerStats.setNumberOfThirdPlace(resultSet.getInt("NumberThirdPlace"));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerStats;
    }

    @Override
    public ArrayList<PlayerStats> getTopVictories() {
        ArrayList<PlayerStats> stats = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Statistic ORDER BY NumberVictories");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PlayerStats playerStats = new PlayerStats();
                playerStats.setPlayerName(resultSet.getString("PlayerName"));
                playerStats.setNumberOfVictories(resultSet.getInt("NumberVictories"));
                playerStats.setNumberOfFirstPlace(resultSet.getInt("NumberFirstPlace"));
                playerStats.setNumberOfSecondPlace(resultSet.getInt("NumberSecondPlace"));
                playerStats.setNumberOfThirdPlace(resultSet.getInt("NumberThirdPlace"));
                stats.add(playerStats);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.reverse(stats);
        return stats;
    }

    @Override
    public ArrayList<PlayerStats> getTopFirstPlace() {
        ArrayList<PlayerStats> stats = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Statistic ORDER BY NumberFirstPlace");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PlayerStats playerStats = new PlayerStats();
                playerStats.setPlayerName(resultSet.getString("PlayerName"));
                playerStats.setNumberOfVictories(resultSet.getInt("NumberVictories"));
                playerStats.setNumberOfFirstPlace(resultSet.getInt("NumberFirstPlace"));
                playerStats.setNumberOfSecondPlace(resultSet.getInt("NumberSecondPlace"));
                playerStats.setNumberOfThirdPlace(resultSet.getInt("NumberThirdPlace"));
                stats.add(playerStats);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.reverse(stats);
        return stats;
    }

    @Override
    public ArrayList<PlayerStats> getTopSecondPlace() {
        ArrayList<PlayerStats> stats = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Statistic ORDER BY NumberSecondPlace");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PlayerStats playerStats = new PlayerStats();
                playerStats.setPlayerName(resultSet.getString("PlayerName"));
                playerStats.setNumberOfVictories(resultSet.getInt("NumberVictories"));
                playerStats.setNumberOfFirstPlace(resultSet.getInt("NumberFirstPlace"));
                playerStats.setNumberOfSecondPlace(resultSet.getInt("NumberSecondPlace"));
                playerStats.setNumberOfThirdPlace(resultSet.getInt("NumberThirdPlace"));
                stats.add(playerStats);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.reverse(stats);
        return stats;
    }

    @Override
    public ArrayList<PlayerStats> getTopThirdPlace() {
        ArrayList<PlayerStats> stats = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Statistic ORDER BY NumberThirdPlace");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PlayerStats playerStats = new PlayerStats();
                playerStats.setPlayerName(resultSet.getString("PlayerName"));
                playerStats.setNumberOfVictories(resultSet.getInt("NumberVictories"));
                playerStats.setNumberOfFirstPlace(resultSet.getInt("NumberFirstPlace"));
                playerStats.setNumberOfSecondPlace(resultSet.getInt("NumberSecondPlace"));
                playerStats.setNumberOfThirdPlace(resultSet.getInt("NumberThirdPlace"));
                stats.add(playerStats);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.reverse(stats);
        return stats;
    }

    @Override
    public void insertPlayerStat(PlayerStats playerStats) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO Statistic (PlayerName,NumberVictories,NumberFirstPlace,NumberSecondPlace,NumberThirdPlace) VALUES ('"
                            + playerStats.getPlayerName() + "','" + playerStats.getNumberOfVictories() + "','"
                            + playerStats.getNumberOfFirstPlace() + "','" + playerStats.getNumberOfSecondPlace() + "','"
                            + playerStats.getNumberOfThirdPlace()
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePlayerStatWithPlayerName(String playerName) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM Statistic WHERE `PlayerName`='" + playerName + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePlayerStat(PlayerStats playerStats) {
        ArrayList<PlayerStats> stats = getAllPlayerStats();
        while (!stats.isEmpty()) {
            if (stats.get(0).getPlayerName().equalsIgnoreCase(playerStats.getPlayerName())) {
                deletePlayerStatWithPlayerName(stats.get(0).getPlayerName());
                insertPlayerStat(playerStats);
                return;
            }
            stats.remove(0);
        }
        insertPlayerStat(playerStats);
    }

    @Override
    public ArrayList<DailyWinner> getAllDailyWinners() {
        ArrayList<DailyWinner> dailyWinners = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM DailyWinner");
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
            e.printStackTrace();
            ReloadUtils.reload();
        }
        return dailyWinners;
    }

    @Override
    public void insertDailyWinner(DailyWinner dailyWinner) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO DailyWinner (NomeChallenge,PlayerName,Reward) VALUES ('"
                            + dailyWinner.getNomeChallenge() + "','" + dailyWinner.getPlayerName() + "','" + dailyWinner.getReward()
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
    public void deleteDailyWinnerWithId(int id) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM DailyWinner WHERE `ID`='" + id + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
    public void updateDailyWinner(DailyWinner dailyWinner) {
        ArrayList<DailyWinner> winners = getAllDailyWinners();
        while (!winners.isEmpty()) {
            if (winners.get(0).getPlayerName().equalsIgnoreCase(dailyWinner.getPlayerName())) {
                deleteDailyWinnerWithId(winners.get(0).getId());
                insertDailyWinner(dailyWinner);
                return;
            }
            winners.remove(0);
        }
        insertDailyWinner(dailyWinner);
    }

    public ArrayList<Challenge> getAllChallenges() {
        ArrayList<Challenge> challengeDBS = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Challenge");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Challenge challengeDB = new Challenge();
                challengeDB.setTimeChallenge(resultSet.getInt("TimeResume"));
                challengeDB.setChallengeName(resultSet.getString("NomeChallenge"));
                challengeDBS.add(challengeDB);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
        return challengeDBS;
    }

    @Override
    public void deleteChallengeWithName(String nomeChallenge) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM Challenge WHERE `NomeChallenge`='" + nomeChallenge + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
    public void insertChallengeEvent(String challengeName, int time) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO Challenge (NomeChallenge,TimeResume) VALUES ('"
                            + "Event_" + challengeName + "','" + time
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
        clearChallengesFromFile();
    }

    @Override
    public void insertChallenge(String challengeName, int timeResume) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO Challenge (NomeChallenge,TimeResume) VALUES ('"
                            + challengeName + "','" + timeResume
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
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

    @Override
    public void resumeOldPoints() {
        ArrayList<Challenger> oldPoints = getAllOldChallengers();
        clearChallengers();
        for (Challenger challenger : oldPoints) {
            insertChallenger(challenger.getNomePlayer(), challenger.getPoints());
        }
        clearChallengersOld();
    }

    @Override
    public ArrayList<Challenger> getAllOldChallengers() {
        ArrayList<Challenger> points = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ChallengerEvent");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Challenger point = new Challenger();
                point.setPoints(resultSet.getInt("Points"));
                point.setNomePlayer(resultSet.getString("PlayerName"));
                points.add(point);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }

    @Override
    public boolean isChallengePresent(String challengeName) {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Challenge WHERE `NomeChallenge`='" + challengeName + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
        return false;
    }

    @Override
    public void updateChallenge(String challengeName, int time) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE Challenge SET TimeResume = '" + time + "' WHERE NomeChallenge = '" + challengeName + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    public ArrayList<Challenger> getAllChallengers() {
        ArrayList<Challenger> points = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Challenger");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Challenger point = new Challenger();
                point.setPoints(resultSet.getInt("Points"));
                point.setNomePlayer(resultSet.getString("PlayerName"));
                points.add(point);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
        return points;
    }

    @Override
    public boolean isPresent(String playerName) {
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `PlayerName` FROM Challenger WHERE `PlayerName` ='" + playerName + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
        return false;
    }

    public void deleteChallengerWithName(String nomePlayer) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM Challenger WHERE `PlayerName`='" + nomePlayer + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    public void insertChallengerEvent(String playerName, long points) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO ChallengerEvent (PlayerName,Points) VALUES ('"
                            + playerName + "','" + points
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
    public void insertChallenger(String playerName, long points) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO Challenger (PlayerName,Points) VALUES ('"
                            + playerName + "','" + points
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
    public void updateChallenger(String playerName, long points) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE Challenger SET Points = '" + points + "' WHERE PlayerName = '" + playerName + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
    public ArrayList<Challenger> getAllChallengersTopYesterday() {
        ArrayList<Challenger> points = new ArrayList<>();
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM TopYesterday");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Challenger point = new Challenger();
                point.setPoints(resultSet.getInt("Points"));
                point.setNomePlayer(resultSet.getString("PlayerName"));
                points.add(point);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
        return points;
    }

    public void insertChallengerTopYesterday(String playerName, long points) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO TopYesterday (PlayerName,Points) VALUES ('"
                            + playerName + "','" + points
                            + "')");
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtils.reload();
        }
    }

    @Override
    public int lastDailyWinnerId() {
        return 0;
    }

    @Override
    public void backupDb(int numberOfBackupFiles) {
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
                    if (folder.listFiles().length >= numberOfBackupFiles) {
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
                                e.printStackTrace();
                            }
                        }
                        folder.listFiles()[number].delete();
                    }
                }
                configFile.createNewFile();
                YamlConfiguration file = YamlConfiguration.loadConfiguration(configFile);
                for (Map.Entry<String, Long> players : Main.instance.getDailyChallenge().getPlayers().entrySet()) {
                    file.set("Points." + players.getKey(), players.getValue());
                }
                for (Challenge challenge : getAllChallenges()) {
                    file.set("Challenges." + challenge.getChallengeName(), challenge.getTimeChallenge());
                }
                for (DailyWinner dailyWinner : getAllDailyWinners()) {
                    file.set("DailyWinners." + dailyWinner.getId() + ".PlayerName", dailyWinner.getPlayerName());
                    file.set("DailyWinners." + dailyWinner.getId() + ".NomeChallenge", dailyWinner.getNomeChallenge());
                    file.set("DailyWinners." + dailyWinner.getId() + ".Reward", dailyWinner.getReward());
                }
                for (Challenger top : getAllChallengersTopYesterday()) {
                    file.set("TopYesterday." + top.getNomePlayer(), top.getPoints());
                }
                file.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void controlIfChallengeExist(ArrayList<String> controlIfChallengeExist) {
        for (String challengeName : controlIfChallengeExist) {
            for (Challenge challenge : getAllChallenges()) {
                if (challenge.getChallengeName().equalsIgnoreCase(challengeName)) {
                    deleteChallengeWithName(challengeName);
                    break;
                }
            }
        }
    }

    public void createConnection(final String absolutePath) {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.
                    getConnection("jdbc:h2:" + absolutePath + File.separator + "vanillachallenges;mode=MySQL;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        instance = null;
        connection = null;
    }

}
