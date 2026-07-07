package at.zFrezze.cyberInfra;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class CyberInfra extends JavaPlugin {

    private TokenManager tokenManager;
    private Database database;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        database = new Database(this);
        try {
            database.connect();
        } catch (SQLException e) {
            getLogger().severe("Database connection failed: " + e.getMessage());
            getLogger().severe("Plugin will get disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Database connection successful!");

        this.tokenManager = new TokenManager(this);

        Bukkit.getPluginManager().registerEvents(new JoinListener(tokenManager), this);
        Bukkit.getPluginManager().registerEvents(new TokenListener(this, tokenManager), this);

        new TokenCraft(this).registerRecipe();

        TokenCommand tokenCommand = new TokenCommand(tokenManager);
        getCommand("token").setExecutor(tokenCommand);
        getCommand("token").setTabCompleter(tokenCommand);

        WithdrawCommand withdrawCommand = new WithdrawCommand(tokenManager);
        getCommand("withdraw").setExecutor(withdrawCommand);
        getCommand("withdraw").setTabCompleter(withdrawCommand);

    }

    @Override
    public void onDisable() {
        database.disconnect();
    }
}
