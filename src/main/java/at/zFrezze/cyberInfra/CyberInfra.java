package at.zFrezze.cyberInfra;

import at.zFrezze.cyberInfra.commands.RemovehomeCommand;
import at.zFrezze.cyberInfra.commands.SethomeCommand;
import at.zFrezze.cyberInfra.commands.TokenCommand;
import at.zFrezze.cyberInfra.commands.WithdrawCommand;
import at.zFrezze.cyberInfra.data.Database;
import at.zFrezze.cyberInfra.data.PlayerManager;
import at.zFrezze.cyberInfra.listeners.DeathListener;
import at.zFrezze.cyberInfra.listeners.InventoryListener;
import at.zFrezze.cyberInfra.listeners.JoinListener;
import at.zFrezze.cyberInfra.listeners.TokenListener;
import at.zFrezze.cyberInfra.gui.ConfirmGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;

public final class CyberInfra extends JavaPlugin {

    private PlayerManager playerManager;
    private Database database;
    private ConfirmGUI confirmGUI;

    public static final String CONFIRM_GUI_TITLE = ChatColor.DARK_GRAY + "Bestätigung";


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
        this.confirmGUI = new ConfirmGUI(this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> playerManager.loadPlayer(uuid));
        }

        Bukkit.getPluginManager().registerEvents(new JoinListener(this, playerManager), this);
        Bukkit.getPluginManager().registerEvents(new TokenListener(this, playerManager), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(confirmGUI, playerManager), this);

        new TokenCraft(this).registerRecipe();

        TokenCommand tokenCommand = new TokenCommand(playerManager);
        getCommand("token").setExecutor(tokenCommand);
        getCommand("token").setTabCompleter(tokenCommand);

        WithdrawCommand withdrawCommand = new WithdrawCommand(playerManager);
        getCommand("withdraw").setExecutor(withdrawCommand);
        getCommand("withdraw").setTabCompleter(withdrawCommand);

        getCommand("sethome").setExecutor(new SethomeCommand(playerManager, this, confirmGUI));

        RemovehomeCommand removehomeCommand = new RemovehomeCommand(playerManager, this, confirmGUI);
        getCommand("removehome").setExecutor(removehomeCommand);
        getCommand("removehome").setTabCompleter(removehomeCommand);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> playerManager.saveAll(), 6000L, 6000L);
    }

    public Database getDatabase() {
        return database;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public int getSetHomePrice() {
        return getConfig().getInt("homes.price-set");
    }

    public int getRemoveHomePrice() {
        return getConfig().getInt("homes.price-remove");
    }

    public int getOverrideHomePrice() {
        return getSetHomePrice() - getRemoveHomePrice();
    }

    @Override
    public void onDisable() {
        if (playerManager != null) {
            playerManager.saveAll();
        }
        database.disconnect();
    }
}