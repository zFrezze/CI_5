package at.zFrezze.cyberInfra.listeners;

import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.gui.ConfirmGUI;
import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PendingHome;
import at.zFrezze.cyberInfra.data.PlayerManager;
import at.zFrezze.cyberInfra.gui.ConfirmHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public class InventoryListener implements Listener {

    private final ConfirmGUI confirmGUI;
    private final PlayerManager playerManager;
    private final ConfigManager configManager;

    public InventoryListener(ConfirmGUI confirmGUI, PlayerManager playerManager, ConfigManager configManager) {
        this.confirmGUI = confirmGUI;
        this.playerManager = playerManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getInventory().getHolder() instanceof ConfirmHolder) || e.getCurrentItem() == null) {
            return;
        }

        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();
        CustomPlayer customPlayer = playerManager.get(player.getUniqueId());
        if (customPlayer == null) return;

        switch (e.getRawSlot()) {
            case 11:
                PendingHome pendingHome = confirmGUI.removePending(player.getUniqueId());
                if (pendingHome == null) return;

                int price = pendingHome.getPrice();
                int balance = playerManager.getToken(player.getUniqueId());

                switch (pendingHome.getAction()) {
                    case SET_HOME, OVERRIDE_HOME -> {
                        if (balance < price) {
                            int missing = price - balance;
                            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NOT_ENOUGH_TOKENS, Map.of(
                                    "price", String.valueOf(price),
                                    "missing", String.valueOf(missing)
                            )));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                            player.closeInventory();
                            return;
                        }
                        playerManager.removeToken(player.getUniqueId(), price);
                        customPlayer.setHome(pendingHome.getName(), pendingHome.getLocation());
                        player.sendActionBar(configManager.getMessage(ConfigMessage.SETHOME_CREATED, Map.of(
                                "home", pendingHome.getName(),
                                "price", String.valueOf(price)
                        )));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    case REMOVE_HOME -> {
                        if (customPlayer.getHome(pendingHome.getName()) == null) {
                            player.closeInventory();
                            return;
                        }
                        playerManager.addToken(player.getUniqueId(), price);
                        customPlayer.removeHome(pendingHome.getName());
                        player.sendActionBar(configManager.getMessage(ConfigMessage.REMOVEHOME_REMOVED, Map.of(
                                "home", pendingHome.getName(),
                                "price", String.valueOf(price)
                        )));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    case TELEPORT_HOME -> {
                        if (balance < price) {
                            int missing = price - balance;
                            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NOT_ENOUGH_TOKENS, Map.of(
                                    "price", String.valueOf(price),
                                    "missing", String.valueOf(missing)
                            )));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                            player.closeInventory();
                            return;
                        }
                        playerManager.removeToken(player.getUniqueId(), price);
                        player.teleport(pendingHome.getLocation());
                        player.sendActionBar(configManager.getMessage(ConfigMessage.HOME_TELEPORTED, Map.of("home", pendingHome.getName())));
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    }
                }
                break;
            case 13:
                return;
            case 15:
                confirmGUI.removePending(player.getUniqueId());
                break;
            default:
                return;
        }
        player.closeInventory();
    }
}