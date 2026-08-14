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

public class TpacceptCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager;
    private final PlayerManager playerManager;
    private final TpaManager tpaManager;
    private final CyberInfra main;

    public TpacceptCommand(ConfigManager configManager, PlayerManager playerManager, TpaManager tpaManager, CyberInfra main) {
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

        if (!player.hasPermission("ci.tpaccept.use")) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NO_PERMISSION, cp.getLanguage()));
            return true;
        }

        if (args.length < 1) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.TPACCEPT_USAGE, cp.getLanguage()));
            return true;
        }

        Player tpaSender = Bukkit.getPlayer(args[0]);
        if (tpaSender == null) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_PLAYER_NOT_EXISTING, cp.getLanguage()));
            return true;
        }

        TpaRequest request = tpaManager.removeRequest(player.getUniqueId(), tpaSender.getUniqueId());
        if (request == null) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.TPACCEPT_NO_PENDING_REQUEST, cp.getLanguage()));
            return true;
        }

        CustomPlayer cpSender = playerManager.get(tpaSender.getUniqueId());
        if (cpSender == null) return true;

        int price = main.getConfig().getInt("tpa.price");
        if (cpSender.getToken() < price) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.TPACCEPT_SENDER_NOT_ENOUGH_TOKENS,
                    Map.of("player", tpaSender.getName()), cp.getLanguage()));
            return true;
        }

        playerManager.removeToken(tpaSender.getUniqueId(), price);
        tpaSender.teleport(player);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        tpaSender.playSound(tpaSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        player.sendActionBar(configManager.getMessage(ConfigMessage.TPA_SUCCESS_TARGET,
                Map.of("player", tpaSender.getName()), cp.getLanguage()));
        tpaSender.sendActionBar(configManager.getMessage(ConfigMessage.TPA_SUCCESS_SENDER,
                Map.of("target", player.getName()), cpSender.getLanguage()));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return List.of();

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], tpaManager.getSenderNames(player.getUniqueId()), new ArrayList<>());
        }

        return List.of();
    }
}