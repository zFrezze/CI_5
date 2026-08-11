package at.zFrezze.cyberInfra;

import at.zFrezze.cyberInfra.commands.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CyberInfra extends JavaPlugin {

    private PlayerManager playerManager;
    private Database database;
    private ConfirmGUI confirmGUI;

    public static final String CONFIRM_GUI_TITLE = ChatColor.DARK_GRAY + "Confirmation";


    @Override
    public void onEnable() {

        saveDefaultConfig();

        if (!validateConfig()) {
            getLogger().severe("Config not full — Plugin will get deactivated.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


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

        TokenCraft tokenCraft = new TokenCraft(this);
        tokenCraft.registerRecipe();

        this.playerManager = new PlayerManager(this, tokenCraft);
        this.confirmGUI = new ConfirmGUI(this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> playerManager.loadPlayer(uuid));
        }

        Bukkit.getPluginManager().registerEvents(new JoinListener(this, playerManager), this);
        Bukkit.getPluginManager().registerEvents(new TokenListener(playerManager, tokenCraft), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(confirmGUI, playerManager), this);

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

        HomeCommand homeCommand = new HomeCommand(confirmGUI, playerManager, this);
        getCommand("home").setExecutor(homeCommand);
        getCommand("home").setTabCompleter(homeCommand);

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

    public int getTeleportHomePrice() {return getConfig().getInt("homes.price-teleport");}

    @Override
    public void onDisable() {
        if (playerManager != null) {
            playerManager.saveAll();
        }
        database.disconnect();
    }

    private boolean validateConfig() {
        List<String> required = List.of(
                "token.skin-url",
                "homes.skin-url",
                "homes.price-set",
                "homes.price-remove",
                "homes.price-teleport"
        );

        List<String> missing = new ArrayList<>();
        for (String key : required) {
            if (!getConfig().contains(key)) {
                missing.add(key);
            }
        }

        if (!missing.isEmpty()) {
            getLogger().severe("Missing Config-Keys: " + String.join(", ", missing));
            return false;
        }

        if (getConfig().getInt("homes.price-set") < getConfig().getInt("homes.price-remove")) {
            getLogger().warning("price-set is smaller than price-remove → Override would be free!");
        }

        return true;
    }
}