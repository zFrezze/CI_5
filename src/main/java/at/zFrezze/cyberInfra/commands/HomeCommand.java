package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
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
import java.util.Map;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final PlayerManager playerManager;
    private final CyberInfra main;
    private final ConfirmGUI confirmGUI;
    private final ConfigManager configManager;

    public HomeCommand(ConfirmGUI confirmGUI, PlayerManager playerManager, CyberInfra main, ConfigManager configManager) {
        this.confirmGUI = confirmGUI;
        this.playerManager = playerManager;
        this.main = main;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player player)) return true;

        CustomPlayer customPlayer = playerManager.get(player.getUniqueId());
        if (customPlayer == null) return true;

        if (args.length != 1) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.HOME_USAGE, customPlayer.getLanguage()));
            return true;
        }

        Location home = customPlayer.getHome(args[0]);
        if (home == null) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.HOME_NOT_EXISTING, Map.of("home", args[0].toString()), customPlayer.getLanguage()));
            return true;
        }

        int price = main.getTeleportHomePrice();
        int balance = playerManager.getToken(player.getUniqueId());
        if (balance < price) {
            int missing = price - balance;
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NOT_ENOUGH_TOKENS,
                    Map.of(
                            "price", String.valueOf(price),
                            "missing", String.valueOf(missing)), customPlayer.getLanguage()));
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