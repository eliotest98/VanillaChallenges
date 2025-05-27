package io.eliotesta98.VanillaChallenges.Database;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;

public class H2Database extends Database {

    public static H2Database instance = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(H2Database.class.getName());

    public H2Database(String absolutePath) {
        setPrefix("");
        createConnection(absolutePath);
        initialize();
    }

    @Override
    public void createConnection(String absolutePath) {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        try {
            Connection connection = DriverManager.
                    getConnection("jdbc:h2:" + absolutePath + File.separator + "vanillachallenges;mode=MySQL;");
            setConnection(connection);
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

}
