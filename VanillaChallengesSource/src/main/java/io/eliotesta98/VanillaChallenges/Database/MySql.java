package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Core.Main;

import java.sql.*;
import java.util.logging.Level;

public class MySql extends Database {

    public static MySql instance = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MySql.class.getName());

    public MySql(String absolutePath) throws SQLException {
        setPrefix(Main.instance.getConfigGestion().getMySqlPrefix());
        createConnection(absolutePath);
        initialize();
    }

    @Override
    public void createConnection(String absolutePath) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        Connection connection = DriverManager.
                getConnection(absolutePath, Main.instance.getConfigGestion().getUsername(), Main.instance.getConfigGestion().getPassword());
        setConnection(connection);
    }
}
