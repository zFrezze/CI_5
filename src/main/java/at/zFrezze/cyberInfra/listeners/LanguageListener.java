package at.zFrezze.cyberInfra.listeners;

import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PlayerManager;
import at.zFrezze.cyberInfra.gui.LanguageGUI;
import at.zFrezze.cyberInfra.gui.LanguageHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class LanguageListener implements Listener {

    private final LanguageGUI languageGUI;
    private final PlayerManager playerManager;
    private final ConfigManager configManager;

    public LanguageListener(LanguageGUI languageGUI, PlayerManager playerManager, ConfigManager configManager) {
        this.languageGUI = languageGUI;
        this.playerManager = playerManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof LanguageHolder || e.getCurrentItem() == null)) {return;}
        Player player = (Player) e.getWhoClicked();

        e.setCancelled(true);

        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return;

        ItemMeta meta = e.getCurrentItem().getItemMeta();
        String lang = meta.getPersistentDataContainer().get(languageGUI.getLanguageKey(), PersistentDataType.STRING);
        if (lang == null) return;

        if (!player.hasPermission("ci.language." + lang.toLowerCase())) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.GENERAL_NO_PERMISSION, cp.getLanguage()));
            return;
        }
        cp.setLanguage(lang);

        player.closeInventory();
        player.sendActionBar(configManager.getMessage(ConfigMessage.LANGUAGE_CHANGED, Map.of("language", lang), cp.getLanguage()));
    }

}
