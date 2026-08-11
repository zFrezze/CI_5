package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import at.zFrezze.cyberInfra.gui.ConfirmGUI;
import at.zFrezze.cyberInfra.gui.HomeActions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final PlayerManager playerManager;
    private final CyberInfra main;
    private final ConfirmGUI confirmGUI;

    public HomeCommand(ConfirmGUI confirmGUI, PlayerManager playerManager, CyberInfra main) {
        this.confirmGUI = confirmGUI;
        this.playerManager = playerManager;
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player player)) return true;

        CustomPlayer customPlayer = playerManager.get(player.getUniqueId());
        if (customPlayer == null) return true;

        if (args.length != 1) {
            player.sendActionBar(Component.text("Invalid usage! You need to use /home <Home>", NamedTextColor.RED));
            return true;
        }

        Location home = customPlayer.getHome(args[0]);
        if (home == null) {
            player.sendActionBar(Component.text(args[0] + " doesn't exist!", NamedTextColor.RED));
            return true;
        }

        int price = main.getTeleportHomePrice();
        int balance = playerManager.getToken(player.getUniqueId());
        if (balance < price) {
            int missing = price - balance;
            player.sendActionBar(Component.text("Not enough tokens! You need " + price + " (you're missing " + missing + ").", NamedTextColor.RED));
            return true;
        }
        confirmGUI.openGUI(player, HomeActions.TELEPORT_HOME, args[0], home, price, CyberInfra.CONFIRM_GUI_TITLE);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player player) {
            CustomPlayer customPlayer = playerManager.get(player.getUniqueId());
            if (customPlayer != null && args.length == 1) {
                return StringUtil.copyPartialMatches(args[0], customPlayer.getHomes().keySet(), new ArrayList<>());
            }
        }
        return new ArrayList<>();
    }
}