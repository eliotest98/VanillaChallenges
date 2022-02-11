package io.eliotesta98.VanillaChallenges.Database;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class H2Database {

    public static Connection connection = null;
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds = null;
    public static H2Database instance = null;

    public H2Database(final String AbsolutePath) throws SQLException, ClassNotFoundException {
        instance = this;
        createConnection(AbsolutePath);
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
    }

    public void clearChallenges(){
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
        }
    }

    public void clearChallengers(){
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
        }
    }

    public void clearDailyWinners(){
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
        }
    }

    public void clearAll(){
        clearChallenges();
        clearChallengers();
        clearDailyWinners();
    }

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
        }
        return dailyWinners;
    }

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
        }
    }

    public void deleteDailyWinnerWithId(int id) {
        new Thread(() -> {
            try {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("DELETE FROM DailyWinner WHERE `ID`='" + id + "'");
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
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
        }
        return challengeDBS;
    }

    public void deleteChallengeWithName(String nomeChallenge) {
        new Thread(() -> {
            try {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("DELETE FROM Challenge WHERE `NomeChallenge`='" + nomeChallenge + "'");
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
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
        }
    }

    public void updateChallenge(String challengeName, int time) {
        new Thread(() -> {
            try {
                PreparedStatement preparedStatement =
                        connection.prepareStatement("UPDATE Challenge SET TimeResume = '" + time + "' WHERE NomeChallenge = '" + challengeName + "'");
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
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
        }
        return points;
    }

    public void deleteChallengerWithName(String nomePlayer) {
        new Thread(() -> {
            try {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("DELETE FROM Challenger WHERE `PlayerName`='" + nomePlayer + "'");
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

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
        }
    }

    public void updateChallenger(String playerName, long points) {
        new Thread(() -> {
            try {
                PreparedStatement preparedStatement =
                        connection.prepareStatement("UPDATE Challenger SET Points = '" + points + "' WHERE PlayerName = '" + playerName + "'");
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void createConnection(final String AbsolutePath) {
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:" + AbsolutePath + File.separator + "vanillachallenges;mode=MySQL;");
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void disconnect() {
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
