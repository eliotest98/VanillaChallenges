package io.eliotesta98.VanillaChallenges.Database;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.MoneyUtils;
import io.eliotesta98.VanillaChallenges.Utils.ReloadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class H2Database implements Database {

    public static Connection connection = null;
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds = null;
    public static H2Database instance = null;

    public H2Database() {
        initialize(Main.instance.getDataFolder().getAbsolutePath());
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
                    "CREATE TABLE IF NOT EXISTS DailyWinner (`ID` INT(100) NOT NULL AUTO_INCREMENT PRIMARY KEY, `NomeChallenge` VARCHAR(100) NOT NULL, `PlayerName` VARCHAR(100) NOT NULL, `Reward` VARCHAR(100) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS TopYesterday (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            Main.instance.getServer().getConsoleSender().sendMessage("Folder: '" + com.zaxxer.hikari.HikariConfig.class
                    .getProtectionDomain().getCodeSource().getLocation().getPath() + "'");
            Main.instance.getServer().getConsoleSender().sendMessage("§cError Database not connected!");
            e.printStackTrace();
            Main.instance.onDisable();
        }
    }

    @Override
    public String insertDailyChallenges() {
        ArrayList<ChallengeDB> challenges = H2Database.instance.getAllChallenges();
        int count = 1;
        if (challenges.isEmpty()) {
            String nome = "nessuno";
            for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallenges().entrySet()) {
                if (count == 1) {
                    Main.dailyChallenge = challenge.getValue();
                    nome = challenge.getValue().getTypeChallenge();
                    Main.currentlyChallengeDB = new ChallengeDB(challenge.getKey(), 86400);
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
                    Main.currentlyChallengeDB = challenges.get(0);
                    Main.dailyChallenge = Main.instance.getConfigGestion().getChallenges().get(challenges.get(0).getNomeChallenge());
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Vanilla Challenges] " + challenges.size() + " challenges remain on DB");
                    return Main.dailyChallenge.getTypeChallenge();
                }
            }
            return "nessuno";
        }
    }

    @Override
    public void loadPlayersPoints() {
        Main.dailyChallenge.setPlayers(H2Database.instance.getAllChallengers());
        Main.dailyChallenge.savePoints();
        ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(3);
        int i = 1;
        while (!top.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + MoneyUtils.transform(top.get(0).getPoints()))));
            top.remove(0);
            i++;
        }
    }

    public void clearChallenges() {
        PreparedStatement preparedStatement = null;
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
            ReloadUtil.reload();
        }
    }

    @Override
    public void clearChallengers() {
        PreparedStatement preparedStatement = null;
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
            ReloadUtil.reload();
        }
    }

    public void clearDailyWinners() {
        PreparedStatement preparedStatement = null;
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
            ReloadUtil.reload();
        }
    }

    @Override
    public void removeTopYesterday() {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "DROP TABLE TopYesterday");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS TopYesterday (`PlayerName` VARCHAR(100) NOT NULL PRIMARY KEY, `Points` INT(15) NOT NULL);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtil.reload();
        }
    }

    @Override
    public void saveTopYesterday(ArrayList<Challenger> newTopYesterday) {
        for (int i = 0; i < newTopYesterday.size(); i++) {
            insertChallengerTopYesterday(newTopYesterday.get(0).getNomePlayer(), newTopYesterday.get(0).getPoints());
        }
    }

    @Override
    public void clearAll() {
        clearChallenges();
        clearChallengers();
        clearDailyWinners();
        removeTopYesterday();
    }

    @Override
    public ArrayList<DailyWinner> getAllDailyWinners() {
        ArrayList<DailyWinner> dailyWinners = new ArrayList<DailyWinner>();
        ResultSet resultSet = null;
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
            ReloadUtil.reload();
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
            ReloadUtil.reload();
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
            ReloadUtil.reload();
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

    public ArrayList<ChallengeDB> getAllChallenges() {
        ArrayList<ChallengeDB> challengeDBS = new ArrayList<ChallengeDB>();
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Challenge");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final ChallengeDB challengeDB = new ChallengeDB();
                challengeDB.setTimeResume(resultSet.getInt("TimeResume"));
                challengeDB.setNomeChallenge(resultSet.getString("NomeChallenge"));
                challengeDBS.add(challengeDB);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtil.reload();
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
            ReloadUtil.reload();
        }
    }

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
            ReloadUtil.reload();
        }
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
            ReloadUtil.reload();
        }
    }

    public ArrayList<Challenger> getAllChallengers() {
        ArrayList<Challenger> points = new ArrayList<Challenger>();
        ResultSet resultSet = null;
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
            ReloadUtil.reload();
        }
        return points;
    }

    @Override
    public boolean isPresent(String playerName) {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `PlayerName` FROM Challenger WHERE `PlayerName` ='" + playerName + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ReloadUtil.reload();
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
            ReloadUtil.reload();
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
            ReloadUtil.reload();
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
            ReloadUtil.reload();
        }
    }

    @Override
    public ArrayList<Challenger> getAllChallengersTopYesterday() {
        ArrayList<Challenger> points = new ArrayList<Challenger>();
        ResultSet resultSet = null;
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
            ReloadUtil.reload();
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
            ReloadUtil.reload();
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
                    if (folder.listFiles().length > numberOfBackupFiles) {
                        folder.listFiles()[0].delete();
                    }
                }
                configFile.createNewFile();
                YamlConfiguration file = YamlConfiguration.loadConfiguration(configFile);
                for (Map.Entry<String, Long> players : Main.dailyChallenge.getPlayers().entrySet()) {
                    file.set("Points." + players.getKey(), players.getValue());
                }
                for (ChallengeDB challenge : getAllChallenges()) {
                    file.set("Challenges." + challenge.getNomeChallenge(), challenge.getTimeResume());
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
                return;
            }
        }
    }

    public void createConnection(final String AbsolutePath) {
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:" + AbsolutePath + File.separator + "vanillachallenges;mode=MySQL;");
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ds.close();
        instance = null;
        connection = null;
        ds = null;
    }

}
