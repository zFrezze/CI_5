package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import at.zFrezze.cyberInfra.data.TpaManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TpaCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager;
    private final PlayerManager playerManager;
    private final TpaManager tpaManager;

    public TpaCommand(ConfigManager configManager, PlayerManager playerManager, TpaManager tpaManager) {
        this.configManager = configManager;
        this.playerManager = playerManager;
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return true;

        if (!player.hasPermission("ci.tpa.use")) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NO_PERMISSION, cp.getLanguage()));
            return true;
        }

        if (!(args.length >= 1)) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.TPA_USAGE, cp.getLanguage()));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_PLAYER_NOT_EXISTING, Map.of("player", target.getName()), cp.getLanguage()));
            return true;
        }

        tpaManager.sendTpa(player, target);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> onlinePlayers = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player == sender) continue;
                onlinePlayers.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[0], onlinePlayers, new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
