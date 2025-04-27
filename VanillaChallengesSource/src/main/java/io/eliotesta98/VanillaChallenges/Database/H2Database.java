package io.eliotesta98.VanillaChallenges.Database;

import java.io.File;
import java.sql.*;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import jdk.jfr.internal.Logger;

public class H2Database extends Database {

    public static H2Database instance = null;

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
            Logger.log(LogTag.JFR, LogLevel.ERROR, e.getMessage());
        }
        try {
            Connection connection = DriverManager.
                    getConnection("jdbc:h2:" + absolutePath + File.separator + "vanillachallenges;mode=MySQL;");
            setConnection(connection);
        } catch (SQLException e) {
            Logger.log(LogTag.JFR, LogLevel.ERROR, e.getMessage());
        }
    }

}
