package at.zFrezze.cyberInfra.listeners;

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

public class InventoryListener implements Listener {

    private final ConfirmGUI confirmGUI;
    private final PlayerManager playerManager;

    public InventoryListener(ConfirmGUI confirmGUI, PlayerManager playerManager) {
        this.confirmGUI = confirmGUI;
        this.playerManager = playerManager;
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
                            player.sendActionBar(Component.text("Not enough tokens! You need " + price + " (you're missing " + missing + ").", NamedTextColor.RED));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                            player.closeInventory();
                            return;
                        }
                        playerManager.removeToken(player.getUniqueId(), price);
                        customPlayer.setHome(pendingHome.getName(), pendingHome.getLocation());
                        player.sendActionBar(Component.text(pendingHome.getName() + " successfully created! (-" + price + " tokens)", NamedTextColor.GREEN));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    case REMOVE_HOME -> {
                        if (customPlayer.getHome(pendingHome.getName()) == null) {
                            player.closeInventory();
                            return;
                        }
                        playerManager.addToken(player.getUniqueId(), price);
                        customPlayer.removeHome(pendingHome.getName());
                        player.sendActionBar(Component.text(pendingHome.getName() + " successfully removed! (+" + price + " tokens)", NamedTextColor.GREEN));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    case TELEPORT_HOME -> {
                        if (balance < price) {
                            int missing = price - balance;
                            player.sendActionBar(Component.text("Not enough tokens! You need " + price + " (you're missing " + missing + ").", NamedTextColor.RED));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                            player.closeInventory();
                            return;
                        }
                        playerManager.removeToken(player.getUniqueId(), price);
                        player.teleport(pendingHome.getLocation());
                        player.sendActionBar(Component.text("Successfully teleported to " + pendingHome.getName() + ".", NamedTextColor.GREEN));
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