package at.zFrezze.cyberInfra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;

public final class CyberInfra extends JavaPlugin {

    private PlayerManager playerManager;
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
        database.createTable();

        getLogger().info("Database connection successful!");

        this.playerManager = new PlayerManager(this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> playerManager.loadPlayer(uuid));
        }

        Bukkit.getPluginManager().registerEvents(new JoinListener(this, playerManager), this);
        Bukkit.getPluginManager().registerEvents(new TokenListener(this, playerManager), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);

        new TokenCraft(this).registerRecipe();

        TokenCommand tokenCommand = new TokenCommand(playerManager);
        getCommand("token").setExecutor(tokenCommand);
        getCommand("token").setTabCompleter(tokenCommand);

        WithdrawCommand withdrawCommand = new WithdrawCommand(playerManager);
        getCommand("withdraw").setExecutor(withdrawCommand);
        getCommand("withdraw").setTabCompleter(withdrawCommand);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> playerManager.saveAll(), 6000L, 6000L);
    }

    public Database getDatabase() {
        return database;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public void onDisable() {
        if (playerManager != null) {
            playerManager.saveAll();
        }
        database.disconnect();
    }
}