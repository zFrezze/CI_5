package at.zFrezze.cyberInfra.data;

import at.zFrezze.cyberInfra.CyberInfra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private final CyberInfra main;
    private Connection connection;

    private final String HOST;
    private final int PORT;
    private final String DATABASE;
    private final String USERNAME;
    private final String PASSWORD;

    public Database(CyberInfra main) {
        this.main = main;
        this.HOST = main.getConfig().getString("database.host");
        this.PORT = main.getConfig().getInt("database.port");
        this.DATABASE = main.getConfig().getString("database.database");
        this.USERNAME = main.getConfig().getString("database.username");
        this.PASSWORD = main.getConfig().getString("database.password");
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false",
                USERNAME, PASSWORD);
    }

    public void createTable() {
        String tokensSql = "CREATE TABLE IF NOT EXISTS tokens (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "token INT NOT NULL DEFAULT 0)";

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

        try (java.sql.Statement stmt = connection.createStatement()) {
            stmt.execute(tokensSql);
            stmt.execute(homesSql);
        } catch (SQLException e) {
            main.getLogger().severe("Table creation failed: " + e.getMessage());
        }
    }

    public boolean isConnected() {return connection != null;}
    public Connection getConnection() {return connection;}

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}