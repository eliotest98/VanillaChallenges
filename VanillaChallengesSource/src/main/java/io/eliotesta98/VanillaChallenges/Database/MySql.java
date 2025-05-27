package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Core.Main;
import java.sql.*;
import java.util.logging.Level;

public class MySql extends Database {

    public static MySql instance = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MySql.class.getName());

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
            logger.log(Level.WARNING, e.getMessage());
        }
        try {
            Connection connection = DriverManager.
                    getConnection(absolutePath, Main.instance.getConfigGesture().getUsername(), Main.instance.getConfigGesture().getPassword());
            setConnection(connection);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }
}
