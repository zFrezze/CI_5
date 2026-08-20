package at.zFrezze.cyberInfra;

import at.zFrezze.cyberInfra.commands.*;
import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.data.Database;
import at.zFrezze.cyberInfra.data.PlayerManager;
import at.zFrezze.cyberInfra.data.TpaManager;
import at.zFrezze.cyberInfra.gui.ConfirmGUI;
import at.zFrezze.cyberInfra.gui.LanguageGUI;
import at.zFrezze.cyberInfra.listeners.*;
import org.bukkit.Bukkit;
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
    private ConfigManager configManager;
    private LanguageGUI languageGUI;
    private TpaManager tpaManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        saveResource("lang/en.yml", false);
        saveResource("lang/de.yml", false);
        saveResource("lang/at.yml", false);
        saveResource("lang/fr.yml", false);
        saveResource("lang/es.yml", false);
        saveResource("lang/ch.yml", false);
        saveResource("lang/pt.yml", false);
        saveResource("lang/lol.yml", false);


        configManager = new ConfigManager(this);

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
        this.confirmGUI = new ConfirmGUI(this, configManager, playerManager);
        this.tpaManager = new TpaManager(this, configManager, playerManager);

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> playerManager.loadPlayer(uuid));
        }

        this.languageGUI = new LanguageGUI(this);

        Bukkit.getPluginManager().registerEvents(new JoinListener(this, playerManager), this);
        Bukkit.getPluginManager().registerEvents(new TokenListener(playerManager, tokenCraft, configManager), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConfirmListener(confirmGUI, playerManager, configManager), this);
        Bukkit.getPluginManager().registerEvents(new LanguageListener(languageGUI, playerManager, configManager), this);

        TokenCommand tokenCommand = new TokenCommand(playerManager, configManager);
        getCommand("token").setExecutor(tokenCommand);
        getCommand("token").setTabCompleter(tokenCommand);

        WithdrawCommand withdrawCommand = new WithdrawCommand(playerManager, configManager);
        getCommand("withdraw").setExecutor(withdrawCommand);
        getCommand("withdraw").setTabCompleter(withdrawCommand);

        getCommand("sethome").setExecutor(new SethomeCommand(playerManager, this, confirmGUI, configManager));

        RemovehomeCommand removehomeCommand = new RemovehomeCommand(playerManager, this, confirmGUI, configManager);
        getCommand("removehome").setExecutor(removehomeCommand);
        getCommand("removehome").setTabCompleter(removehomeCommand);

        HomeCommand homeCommand = new HomeCommand(confirmGUI, playerManager, this, configManager);
        getCommand("home").setExecutor(homeCommand);
        getCommand("home").setTabCompleter(homeCommand);

        LanguageCommand languageCommand = new LanguageCommand(configManager, playerManager, this, languageGUI);
        getCommand("language").setExecutor(languageCommand);
        getCommand("language").setTabCompleter(languageCommand);

        TpaCommand tpaCommand = new TpaCommand(configManager, playerManager, tpaManager);
        getCommand("tpa").setExecutor(tpaCommand);
        getCommand("tpa").setTabCompleter(tpaCommand);

        TpacceptCommand tpacceptCommand = new TpacceptCommand(configManager, playerManager, tpaManager, this);
        getCommand("tpaccept").setExecutor(tpacceptCommand);
        getCommand("tpaccept").setTabCompleter(tpacceptCommand);

        TpdenyCommand tpdenyCommand = new TpdenyCommand(configManager, playerManager, tpaManager, this);
        getCommand("tpdeny").setExecutor(tpdenyCommand);
        getCommand("tpdeny").setTabCompleter(tpdenyCommand);

        TpcancelCommand tpcancelCommand = new TpcancelCommand(configManager, playerManager, tpaManager, this);
        getCommand("tpcancel").setExecutor(tpcancelCommand);
        getCommand("tpcancel").setTabCompleter(tpcancelCommand);

        TpahereCommand tpahereCommand = new TpahereCommand(configManager, playerManager, tpaManager);
        getCommand("tpahere").setExecutor(tpahereCommand);
        getCommand("tpahere").setTabCompleter(tpahereCommand);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> playerManager.saveAll(), 2400L, 2400L);
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

    public int getTeleportHomePrice() {
        return getConfig().getInt("homes.price-teleport");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

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
                "homes.price-teleport",
                "homes.max-homes.default",
                "homes.max-homes.vip",

                "language"
        );

        List<String> missing = new ArrayList<>();

        for (String key : required) {
            if (!getConfig().contains(key)) {
                missing.add(key);
            }
        }

        List<String> languages = List.of(
                "en", "de", "at", "ch", "fr", "es", "pt", "lol"
        );

        for (String language : languages) {
            String key = "language-skins." + language;

            if (!getConfig().contains(key) ||
                    getConfig().getString(key) == null ||
                    getConfig().getString(key).isBlank()) {
                missing.add(key);
            }
        }

        if (!missing.isEmpty()) {
            getLogger().severe(
                    "Missing Config-Keys: " + String.join(", ", missing)
            );
            return false;
        }

        if (getConfig().getInt("homes.price-set")
                < getConfig().getInt("homes.price-remove")) {

            getLogger().warning(
                    "price-set is smaller than price-remove → Override would be free!"
            );
        }

        return true;
    }

    public String getPrefix() {
        return this.getConfig().getString("prefix");
    }
}