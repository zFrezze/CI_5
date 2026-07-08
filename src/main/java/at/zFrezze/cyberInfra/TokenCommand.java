package at.zFrezze.cyberInfra;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class TokenCommand implements CommandExecutor, TabCompleter {

    private final PlayerManager playerManager;

    public TokenCommand(PlayerManager playerManager) {this.playerManager = playerManager;}

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

            switch (args[0].toLowerCase()) {

                case "info" -> {
                    int amount = playerManager.getToken(target.getUniqueId());
                    Bukkit.getLogger().info(target.getName() + " has " + amount + " tokens.");
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
                    Bukkit.getLogger().info(args[0].toLowerCase() + " done for " + target.getName() + " (" + amount + ").");
                }

                default ->
                        Bukkit.getLogger().warning("Invalid usage! Use /token info|set|add|remove <player> [amount]");
            }
            return true;
        }

        if (args.length == 0) {
            int amount = playerManager.getToken(player.getUniqueId());
            player.sendMessage("You have " + ChatColor.GREEN + amount + ChatColor.WHITE + " tokens.");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "info" -> {
                if (args.length >= 2 && player.hasPermission("ci.tokens.others")) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    int amount = playerManager.getToken(target.getUniqueId());
                    player.sendMessage(target.getName() + " has " + ChatColor.GREEN + amount + ChatColor.WHITE + " tokens.");
                } else {
                    int amount = playerManager.getToken(player.getUniqueId());
                    player.sendMessage("You have " + ChatColor.GREEN + amount + ChatColor.WHITE + " tokens.");
                }
            }

            case "set", "add", "remove" -> {
                if (!player.hasPermission("ci.admin") && !player.hasPermission("ci.tokens.admin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                    return true;
                }
                if (args.length != 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /token " + args[0].toLowerCase() + " <player> <amount>");
                    return true;
                }
                Integer amount = parseAmount(args[2]);
                if (amount == null) {
                    player.sendMessage(ChatColor.RED + "Invalid number: " + args[2]);
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                switch (args[0].toLowerCase()) {
                    case "set" -> playerManager.setToken(target.getUniqueId(), amount);
                    case "add" -> playerManager.addToken(target.getUniqueId(), amount);
                    case "remove" -> playerManager.removeToken(target.getUniqueId(), amount);
                }
                player.sendMessage(ChatColor.GREEN + args[0].toLowerCase() + " done for " + target.getName() + " (" + amount + ").");
            }

            default -> {
                if (!player.hasPermission("ci.admin") || !player.hasPermission("ci.tokens.admin")) {
                    int amount = playerManager.getToken(player.getUniqueId());
                    player.sendMessage("You have " + ChatColor.GREEN + amount + ChatColor.WHITE + " tokens.");
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid usage! Use /token info|set|add|remove <player> [amount]");
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        boolean admin = sender.hasPermission("ci.admin") || sender.hasPermission("ci.tokens.admin");

        if (args.length == 1) {
            List<String> subs = admin ? Arrays.asList("info", "set", "add", "remove") : Arrays.asList("info");
            return StringUtil.copyPartialMatches(args[0], subs, new ArrayList<>());

        } else if (args.length == 2) {
            boolean isAdminSub = args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove");
            boolean infoOthers = args[0].equalsIgnoreCase("info") && sender.hasPermission("ci.tokens.others");

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