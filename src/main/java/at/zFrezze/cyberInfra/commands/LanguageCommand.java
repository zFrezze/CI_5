package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LanguageCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager;
    private final PlayerManager playerManager;

    public LanguageCommand(ConfigManager configManager, PlayerManager playerManager) {
        this.configManager = configManager;
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return true;

        Inventory inv = Bukkit.createInventory(player, 9);

        if (args.length == 0) {

        } else {
            if (!configManager.isValidLanguage(args[0].toLowerCase())) {
                player.sendActionBar(configManager.getMessage(ConfigMessage.LANGUAGE_INVALID, cp.getLanguage()));
                return true;
            }
            cp.setLanguage(args[0].toLowerCase());
            player.sendActionBar(configManager.getMessage(ConfigMessage.LANGUAGE_CHANGED, Map.of("language", args[0]), cp.getLanguage()));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("en", "de", "at", "ch", "fr", "es"), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
