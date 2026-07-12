package at.zFrezze.cyberInfra.listeners;

import at.zFrezze.cyberInfra.gui.ConfirmGUI;
import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PendingHome;
import at.zFrezze.cyberInfra.data.PlayerManager;
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

        if (ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).equals(CyberInfra.CONFIRM_GUI_TITLE) && e.getCurrentItem() != null) {

            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();

            CustomPlayer customPlayer = playerManager.get(player.getUniqueId());

            switch (e.getRawSlot()) {
                case 11:
                    PendingHome pendingHome = confirmGUI.getPending(player.getUniqueId());
                    if (pendingHome == null) return;

                    switch (pendingHome.getAction()) {
                        case SET_HOME, OVERRIDE_HOME -> {
                            playerManager.removeToken(player.getUniqueId(), pendingHome.getPrice());
                            customPlayer.setHome(pendingHome.getName(), pendingHome.getLocation());
                            player.sendMessage(ChatColor.GREEN + pendingHome.getName() + ChatColor.WHITE + " successfully created! " + ChatColor.GRAY + "(-" + pendingHome.getPrice() + " tokens)");
                        }
                        case REMOVE_HOME -> {
                            playerManager.addToken(player.getUniqueId(), pendingHome.getPrice());
                            customPlayer.removeHome(pendingHome.getName());
                            player.sendMessage(ChatColor.GREEN + pendingHome.getName() + ChatColor.WHITE + " successfully removed! " + ChatColor.GRAY + "(+" + pendingHome.getPrice() + " tokens)");
                        }
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    confirmGUI.removePending(player.getUniqueId());
                    break;
                case 13:
                    return;
                case 15:
                    break;
                default:
                    return;
            }
            player.closeInventory();
        }

    }

}
