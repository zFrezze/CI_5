package at.zFrezze.cyberInfra;

import javax.print.DocFlavor;
import javax.xml.crypto.Data;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private CyberInfra main;

    private Connection connection;

    private final String HOST = main.getConfig().getString("database.host");
    private final int PORT = main.getConfig().getInt("database.port");
    private final String DATABASE = main.getConfig().getString("database.database");
    private final String USERNAME = main.getConfig().getString("database.username");
    private final String PASSWORD = main.getConfig().getString("database.password");

    Database(CyberInfra main) {
        this.main = main;
    }

    public void connect() throws SQLException {

        String url = main.getConfig().getString("database.url");

        connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false", USERNAME, PASSWORD);
    }

    public boolean isConnected() {
        return connection != null;
    }


    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


