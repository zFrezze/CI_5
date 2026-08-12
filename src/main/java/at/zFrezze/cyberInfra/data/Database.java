package at.zFrezze.cyberInfra.data;

import at.zFrezze.cyberInfra.CyberInfra;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Database {

    private final CyberInfra main;
    private Connection connection;

    public Database(CyberInfra main) {
        this.main = main;
    }

    public void connect() throws SQLException {
        if (!main.getDataFolder().exists()) {
            main.getDataFolder().mkdirs();
        }

        File dbFile = new File(main.getDataFolder(), "database.db");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL");
            stmt.execute("PRAGMA foreign_keys=ON");
        }
    }

    public void createTable() {
        String tokensSql = "CREATE TABLE IF NOT EXISTS tokens (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "token INT NOT NULL DEFAULT 0, " +
                "language VARCHAR(8) NOT NULL DEFAULT 'en')";

        String homesSql = "CREATE TABLE IF NOT EXISTS homes (" +
                "uuid VARCHAR(36) NOT NULL, " +
                "name VARCHAR(32) NOT NULL, " +
                "world VARCHAR(64) NOT NULL, " +
                "x DOUBLE NOT NULL, " +
                "y DOUBLE NOT NULL, " +
                "z DOUBLE NOT NULL, " +
                "yaw FLOAT NOT NULL, " +
                "pitch FLOAT NOT NULL, " +
                "PRIMARY KEY (uuid, name))";

        String rulesSql = "CREATE TABLE IF NOT EXISTS rules_accepted (" +
                "uuid VARCHAR(36) PRIMARY KEY)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(tokensSql);
            stmt.execute(homesSql);
            stmt.execute(rulesSql);
        } catch (SQLException e) {
            main.getLogger().severe("Table creation failed: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                main.getLogger().severe("Failed to disconnect database: " + e.getMessage());
            }
        }
    }

    public boolean hasAcceptedRules(UUID uuid) {
        String sql = "SELECT uuid FROM rules_accepted WHERE uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            main.getLogger().severe("Rules check failed for " + uuid + ": " + e.getMessage());
            return false;
        }
    }

    public void setRulesAccepted(UUID uuid) {
        String sql = "INSERT OR IGNORE INTO rules_accepted (uuid) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            main.getLogger().severe("Rules save failed for " + uuid + ": " + e.getMessage());
        }
    }
}