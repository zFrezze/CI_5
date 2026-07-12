package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.gui.ConfirmGUI;
import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.gui.HomeActions;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class SethomeCommand implements CommandExecutor {

    private PlayerManager playerManager;
    private CyberInfra main;
    private ConfirmGUI confirmGUI;

    public SethomeCommand(PlayerManager playerManager, CyberInfra main, ConfirmGUI confirmGUI) {
        this.playerManager = playerManager;
        this.main = main;
        this.confirmGUI = confirmGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) return true;

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return true;

        if (args.length >= 2) {
            sender.sendMessage(ChatColor.RED + "Invalid usage! /sethome <name>");
            return true;
        }

        String name;
        if (args.length == 0) {
            int i = 1;
            while (cp.getHome("Home" + i) != null) {
                i++;
            }
            name = "Home" + i;
        } else {
            name = args[0];
        }

        boolean isOverride = cp.getHome(name) != null;

        if (!isOverride && cp.getHomeAmount() >= 5) {
            sender.sendMessage(ChatColor.RED + "You have reached the maximum amount of homes!");
            return true;
        }

        int price;
        if (isOverride) {
            price = main.getOverrideHomePrice();
        } else {
            price = main.getSetHomePrice();
        }

        HomeActions action;
        if (isOverride) {
            action = HomeActions.OVERRIDE_HOME;
        } else {
            action = HomeActions.SET_HOME;
        }

        int balance = playerManager.getToken(player.getUniqueId());
        if (balance < price) {
            int missing = price - balance;
            sender.sendMessage(ChatColor.RED + "Not enough tokens! You need " + price + " (you're missing " + missing + ").");
            return true;
        }

        confirmGUI.openGUI(player, action , name,player.getLocation(), price,CyberInfra.CONFIRM_GUI_TITLE);

        return true;
    }
}