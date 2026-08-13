package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WithdrawCommand implements CommandExecutor, TabCompleter {

    private final PlayerManager playerManager;
    private final ConfigManager configManager;

    public WithdrawCommand(PlayerManager playerManager, ConfigManager configManager) {
        this.playerManager = playerManager;
        this.configManager = configManager;
    }

    private Integer parseAmount(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().warning("Only players can withdraw tokens.");
            return true;
        }

        CustomPlayer cp = playerManager.get(player.getUniqueId());

        if (!player.hasPermission("ci.withdraw.use")) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NO_PERMISSION, cp.getLanguage()));
            return true;
        }

        if (args.length != 1) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.WITHDRAW_USAGE, cp.getLanguage()));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        Integer amount = parseAmount(args[0]);
        if (amount == null) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_INVALID_NUMBER, Map.of("input", args[0]), cp.getLanguage()));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        if (amount <= 0) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.WITHDRAW_NOT_POSITIVE, cp.getLanguage()));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        boolean success = playerManager.withdrawToken(player.getUniqueId(), player, amount);

        if (!success) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.WITHDRAW_NO_SPACE, cp.getLanguage()));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        int remaining = playerManager.getToken(player.getUniqueId());
        player.sendActionBar(configManager.getMessage(ConfigMessage.WITHDRAW_SUCCESS,
                Map.of("amount", String.valueOf(amount),
                        "remaining", String.valueOf(remaining)
                        ), cp.getLanguage()));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("50", "100", "250", "500", "1000", "2500", "5000", "10000"), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}