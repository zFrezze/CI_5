package at.zFrezze.cyberInfra.commands;

import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
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

import java.util.Map;

public class SethomeCommand implements CommandExecutor {

    private final PlayerManager playerManager;
    private final CyberInfra main;
    private final ConfirmGUI confirmGUI;
    private final ConfigManager configManager;

    public SethomeCommand(PlayerManager playerManager, CyberInfra main, ConfirmGUI confirmGUI, ConfigManager configManager) {
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

        if (!player.hasPermission("ci.sethome.use")) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NO_PERMISSION, cp.getLanguage()));
            return true;
        }

        if (args.length >= 2) {
            player.sendActionBar();
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
        boolean isVip = player.hasPermission("home.vip");

        int maxHomes = isVip ? main.getConfig().getInt("homes.max-homes.vip") : main.getConfig().getInt("homes.max-homes.default");

        if (!isOverride && cp.getHomeAmount() >= maxHomes) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.SETHOME_MAX_REACHED, cp.getLanguage()));
            return true;
        }

        int price = isOverride ? main.getOverrideHomePrice() : main.getSetHomePrice();
        HomeActions action = isOverride ? HomeActions.OVERRIDE_HOME : HomeActions.SET_HOME;

        int balance = playerManager.getToken(player.getUniqueId());
        if (balance < price) {
            int missing = price - balance;
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NOT_ENOUGH_TOKENS,
                    Map.of(
                            "price", String.valueOf(price),
                            "missing", String.valueOf(missing)
                    ), cp.getLanguage()));
            return true;
        }

        confirmGUI.openGUI(player, action, name, player.getLocation(), price);

        return true;
    }
}