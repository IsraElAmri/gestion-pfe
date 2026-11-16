package tn.pfe.gestion.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DbConnection {
    private static final String PROPS_PATH = "/db.properties";

    private DbConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Properties props = loadProps();
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Missing db.url in " + PROPS_PATH);
        }
        if (user == null) {
            user = "";
        }
        if (password == null) {
            password = "";
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static Properties loadProps() {
        Properties props = new Properties();
        try (InputStream in = DbConnection.class.getResourceAsStream(PROPS_PATH)) {
            if (in == null) {
                throw new IllegalStateException("Cannot find " + PROPS_PATH + " on classpath");
            }
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + PROPS_PATH, e);
        }
    }
}

