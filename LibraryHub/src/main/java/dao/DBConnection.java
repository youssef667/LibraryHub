package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Factory-style JDBC connection helper.
 * Every DAO calls getConnection() to open a fresh connection,
 * uses it, then closes it (try-with-resources) when done.
 * No connection is held open or reused across calls.
 *
 * Credentials are loaded from db.properties on the classpath —
 * NOT hardcoded here — so this file is safe to commit to GitHub.
 * db.properties itself is excluded via .gitignore; only
 * db.properties.example (with placeholder values) is committed,
 * so anyone cloning the repo knows what file to create locally.
 */
public class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    // Loaded once when the class is first used, not on every call.
    static {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "db.properties not found on classpath. " +
                                "Copy db.properties.example to src/main/resources/db.properties " +
                                "(or resources/db.properties, depending on your project layout) " +
                                "and fill in your real credentials."
                );
            }
            props.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties: " + e.getMessage(), e);
        }

        URL = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");
    }

    // Private constructor: this class is never instantiated,
    // it only exposes static utility methods.
    private DBConnection() {
    }

    /**
     * Opens and returns a new JDBC connection to the librasys database.
     * Caller is responsible for closing it (ideally via try-with-resources).
     *
     * @return an open Connection
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}