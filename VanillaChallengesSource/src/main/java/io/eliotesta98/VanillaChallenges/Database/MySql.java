package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Core.Main;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import jdk.jfr.internal.Logger;
import java.sql.*;

public class MySql extends Database {

    public static MySql instance = null;

    public MySql(String absolutePath) throws SQLException {
        setPrefix(Main.instance.getConfigGesture().getMySqlPrefix());
        createConnection(absolutePath);
        initialize();
    }

    @Override
    public void createConnection(String absolutePath) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Logger.log(LogTag.JFR, LogLevel.ERROR, e.getMessage());
        }
        try {
            Connection connection = DriverManager.
                    getConnection(absolutePath, Main.instance.getConfigGesture().getUsername(), Main.instance.getConfigGesture().getPassword());
            setConnection(connection);
        } catch (SQLException e) {
            Logger.log(LogTag.JFR, LogLevel.ERROR, e.getMessage());
        }
    }
}
