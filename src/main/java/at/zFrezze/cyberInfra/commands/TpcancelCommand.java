package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import at.zFrezze.cyberInfra.data.TpaManager;
import at.zFrezze.cyberInfra.data.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TpcancelCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager;
    private final PlayerManager playerManager;
    private final TpaManager tpaManager;
    private final CyberInfra main;

    public TpcancelCommand(ConfigManager configManager, PlayerManager playerManager, TpaManager tpaManager, CyberInfra main) {
        this.configManager = configManager;
        this.playerManager = playerManager;
        this.tpaManager = tpaManager;
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) return true;

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return true;

        if (!player.hasPermission("ci.tpcancel.use")) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NO_PERMISSION, cp.getLanguage()));
            return true;
        }

        if (args.length < 1) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.TPCANCEL_USAGE, cp.getLanguage()));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_PLAYER_NOT_EXISTING, Map.of("player", args[0]), cp.getLanguage()));
            return true;
        }

        TpaRequest request = tpaManager.removeRequest(target.getUniqueId(), player.getUniqueId());
        if (request == null) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.TPCANCEL_NO_PENDING_REQUEST, cp.getLanguage()));
            return true;
        }

        CustomPlayer cpSender = playerManager.get(target.getUniqueId());
        if (cpSender == null) return true;

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        target.playSound(target.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);

        player.sendActionBar(configManager.getMessage(ConfigMessage.TPCANCEL_CANCELLED_TARGET,
                Map.of("player", target.getName()), cp.getLanguage()));
        target.sendActionBar(configManager.getMessage(ConfigMessage.TPCANCEL_CANCELLED_SENDER,
                Map.of("target", player.getName()), cpSender.getLanguage()));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return List.of();

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], tpaManager.getSentToNames(player.getUniqueId()), new ArrayList<>());
        }

        return List.of();
    }
}