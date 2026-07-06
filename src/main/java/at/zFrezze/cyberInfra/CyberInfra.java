package at.zFrezze.cyberInfra;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class CyberInfra extends JavaPlugin {

    private TokenManager tokenManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.tokenManager = new TokenManager(this);

        Bukkit.getPluginManager().registerEvents(new JoinListener(tokenManager), this);
        Bukkit.getPluginManager().registerEvents(new TokenListener(tokenManager), this);

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
        // Plugin shutdown logic
    }
}
