package at.zFrezze.cyberInfra;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WithdrawCommand implements CommandExecutor, TabCompleter {

    private final TokenManager tokenManager;

    public WithdrawCommand(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
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

        if (sender instanceof Player player) {

            if (args.length == 1) {

                Integer amount = parseAmount(args[0]);
                if (amount == null) {
                    player.sendMessage(ChatColor.RED + "Invalid number: " + args[0]);
                    return true;
                }

                if (amount <= 0) {
                    player.sendMessage(ChatColor.RED + "Amount must be positive.");
                } else {
                    boolean success = tokenManager.withdrawToken(player.getUniqueId(),player, amount);





                    if (success) {
                        int remaining = tokenManager.getToken(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "Withdrew " + amount + " tokens. " + ChatColor.WHITE + "Balance: " + ChatColor.GREEN + remaining);
                    } else {
                        player.sendMessage(ChatColor.RED + "You don't have enough tokens!");
                    }
                }

            } else {
                player.sendMessage(ChatColor.RED + "Usage: /withdraw <amount>");
            }

        } else {
            Bukkit.getLogger().warning("Only players can withdraw tokens.");
        }
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