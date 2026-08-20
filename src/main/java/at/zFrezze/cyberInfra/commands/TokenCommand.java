package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

public class TokenCommand implements CommandExecutor, TabCompleter {

    private final PlayerManager playerManager;
    private final ConfigManager configManager;

    public TokenCommand(PlayerManager playerManager, ConfigManager configManager) {
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
            if (args.length < 2) {
                Bukkit.getLogger().warning("Usage: /token info|set|add|remove <player> [amount]");
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            String targetName = target.getName() != null ? target.getName() : args[1];

            switch (args[0].toLowerCase()) {

                case "info" -> {
                    int amount = playerManager.getToken(target.getUniqueId());
                    Bukkit.getLogger().info(targetName + " has " + amount + " tokens.");
                }

                case "set", "add", "remove" -> {
                    if (args.length != 3) {
                        Bukkit.getLogger().warning("You need to specify a number!");
                        return true;
                    }
                    Integer amount = parseAmount(args[2]);
                    if (amount == null) {
                        Bukkit.getLogger().warning("Invalid number: " + args[2]);
                        return true;
                    }
                    switch (args[0].toLowerCase()) {
                        case "set" -> playerManager.setToken(target.getUniqueId(), amount);
                        case "add" -> playerManager.addToken(target.getUniqueId(), amount);
                        case "remove" -> playerManager.removeToken(target.getUniqueId(), amount);
                    }
                    Bukkit.getLogger().info(args[0].toLowerCase() + " done for " + targetName + " (" + amount + ").");
                }

                default ->
                        Bukkit.getLogger().warning("Invalid usage! Use /token info|set|add|remove <player> [amount]");
            }
            return true;
        }

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return true;

        if (!player.hasPermission("ci.token.use")) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NO_PERMISSION, cp.getLanguage()));
            return true;
        }

        if (args.length == 0) {
            int amount = playerManager.getToken(player.getUniqueId());
            player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_BALANCE,
                    Map.of("amount", String.valueOf(amount)), cp.getLanguage()));
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "info" -> {
                if (args.length >= 2 && player.hasPermission("ci.token.others")) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    String targetName = target.getName() != null ? target.getName() : args[1];
                    int amount = playerManager.getToken(target.getUniqueId());
                    player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_BALANCE_OTHER,
                            Map.of(
                                    "player", targetName,
                                    "amount", String.valueOf(amount)
                            ), cp.getLanguage()));
                } else {
                    int amount = playerManager.getToken(player.getUniqueId());
                    player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_BALANCE,
                            Map.of("amount", String.valueOf(amount)), cp.getLanguage()));
                }
            }

            case "set", "add", "remove" -> {
                if (!player.hasPermission("ci.admin") && !player.hasPermission("ci.token.admin")) {
                    player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NO_PERMISSION, cp.getLanguage()));
                    return true;
                }
                if (args.length != 3) {
                    player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_USAGE_SUB, Map.of("sub", args[0].toLowerCase()), cp.getLanguage()));
                    return true;
                }
                Integer amount = parseAmount(args[2]);
                if (amount == null) {
                    player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_INVALID_NUMBER, Map.of("input", args[2]), cp.getLanguage()));
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                String targetName = target.getName() != null ? target.getName() : args[1];
                switch (args[0].toLowerCase()) {
                    case "set" -> playerManager.setToken(target.getUniqueId(), amount);
                    case "add" -> playerManager.addToken(target.getUniqueId(), amount);
                    case "remove" -> playerManager.removeToken(target.getUniqueId(), amount);
                }
                player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_ACTION_DONE, Map.of(
                        "action", args[0].toLowerCase(),
                        "player", targetName,
                        "amount", String.valueOf(amount)), cp.getLanguage()));
            }

            default -> {
                boolean isAdmin = player.hasPermission("ci.admin") || player.hasPermission("ci.token.admin");
                if (!isAdmin) {
                    int amount = playerManager.getToken(player.getUniqueId());
                    player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_BALANCE,
                            Map.of("amount", String.valueOf(amount)), cp.getLanguage()));
                } else {
                    player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_USAGE_INVALID, cp.getLanguage()));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        boolean admin = sender.hasPermission("ci.admin") || sender.hasPermission("ci.token.admin");

        if (args.length == 1) {
            List<String> subs = admin ? Arrays.asList("info", "set", "add", "remove") : Arrays.asList("info");
            return StringUtil.copyPartialMatches(args[0], subs, new ArrayList<>());

        } else if (args.length == 2) {
            boolean isAdminSub = args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove");
            boolean infoOthers = args[0].equalsIgnoreCase("info") && sender.hasPermission("ci.token.others");

            if ((isAdminSub && admin) || infoOthers) {
                List<String> names = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    names.add(player.getName());
                }
                return StringUtil.copyPartialMatches(args[1], names, new ArrayList<>());
            }

        } else if (args.length == 3) {
            boolean isAdminSub = args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove");
            if (isAdminSub && admin) {
                return StringUtil.copyPartialMatches(args[2], Arrays.asList("50", "100", "250", "500", "1000", "2500", "5000", "10000"), new ArrayList<>());
            }
        }

        return new ArrayList<>();
    }
}