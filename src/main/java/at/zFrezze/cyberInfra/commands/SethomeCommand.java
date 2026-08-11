package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.gui.ConfirmGUI;
import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.gui.HomeActions;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SethomeCommand implements CommandExecutor {

    private final PlayerManager playerManager;
    private final CyberInfra main;
    private final ConfirmGUI confirmGUI;

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
            player.sendActionBar(Component.text("Invalid usage! /sethome <name>", NamedTextColor.RED));
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
            player.sendActionBar(Component.text("You have reached the maximum amount of homes!", NamedTextColor.RED));
            return true;
        }

        int price = isOverride ? main.getOverrideHomePrice() : main.getSetHomePrice();
        HomeActions action = isOverride ? HomeActions.OVERRIDE_HOME : HomeActions.SET_HOME;

        int balance = playerManager.getToken(player.getUniqueId());
        if (balance < price) {
            int missing = price - balance;
            player.sendActionBar(Component.text("Not enough tokens! You need " + price + " (you're missing " + missing + ").", NamedTextColor.RED));
            return true;
        }

        confirmGUI.openGUI(player, action, name, player.getLocation(), price, CyberInfra.CONFIRM_GUI_TITLE);

        return true;
    }
}