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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemovehomeCommand implements CommandExecutor, TabCompleter {
    private final PlayerManager playerManager;
    private final CyberInfra main;
    private final ConfirmGUI confirmGUI;
    private final ConfigManager configManager;

    public RemovehomeCommand(PlayerManager playerManager, CyberInfra main, ConfirmGUI confirmGUI, ConfigManager configManager) {
        this.playerManager = playerManager;
        this.main = main;
        this.confirmGUI = confirmGUI;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) return true;

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return true;

        if (args.length != 1) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.REMOVEHOME_USAGE));
            return true;
        }

        if (cp.getHome(args[0]) == null) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.HOME_NOT_EXISTING,
                    Map.of("home", args[0].toString())));
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