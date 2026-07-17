package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import at.zFrezze.cyberInfra.gui.ConfirmGUI;
import at.zFrezze.cyberInfra.gui.HomeActions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RemovehomeCommand implements CommandExecutor, TabCompleter {
    private PlayerManager playerManager;
    private CyberInfra main;
    private ConfirmGUI confirmGUI;

    public RemovehomeCommand(PlayerManager playerManager, CyberInfra main, ConfirmGUI confirmGUI) {
        this.playerManager = playerManager;
        this.main = main;
        this.confirmGUI = confirmGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) return true;

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return true;

        if (args.length != 1) {
            player.sendActionBar(Component.text("Invalid usage! /removehome <name>", NamedTextColor.RED));
            return true;
        }

        if (cp.getHome(args[0]) == null) {
            player.sendActionBar(Component.text(args[0] + " doesn't exist!", NamedTextColor.RED));
            return true;
        }

        int price = main.getRemoveHomePrice();
        confirmGUI.openGUI(player, HomeActions.REMOVE_HOME, args[0], cp.getHome(args[0]), price, CyberInfra.CONFIRM_GUI_TITLE);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp != null && args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], cp.getHomes().keySet(), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}