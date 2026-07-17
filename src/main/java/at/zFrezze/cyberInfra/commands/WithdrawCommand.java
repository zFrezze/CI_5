package at.zFrezze.cyberInfra.commands;

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

public class WithdrawCommand implements CommandExecutor, TabCompleter {

    private final PlayerManager playerManager;

    public WithdrawCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
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

        if (args.length != 1) {
            player.sendActionBar(Component.text("Usage: /withdraw <amount>", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        Integer amount = parseAmount(args[0]);
        if (amount == null) {
            player.sendActionBar(Component.text("Invalid number: " + args[0], NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        if (amount <= 0) {
            player.sendActionBar(Component.text("Amount must be positive.", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        boolean success = playerManager.withdrawToken(player.getUniqueId(), player, amount);

        if (!success) {
            player.sendActionBar(Component.text("You don't have enough tokens or inventory space!", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        int remaining = playerManager.getToken(player.getUniqueId());
        player.sendActionBar(Component.text("Withdrew " + amount + " tokens. Balance: " + remaining, NamedTextColor.GREEN));
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