package at.zFrezze.cyberInfra.gui;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.data.CustomPlayer;
import at.zFrezze.cyberInfra.data.PendingHome;
import at.zFrezze.cyberInfra.data.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfirmGUI {

    private final CyberInfra main;
    private final ConfigManager configManager;
    private final PlayerManager playerManager;
    private final Map<UUID, PendingHome> pending = new HashMap<>();

    public ConfirmGUI(CyberInfra main, ConfigManager configManager, PlayerManager playerManager) {
        this.main = main;
        this.configManager = configManager;
        this.playerManager = playerManager;
    }

    public void openGUI(Player player, HomeActions homeActions, String homeName, Location location, int price) {

        int x = Math.toIntExact(Math.round(location.getX()));
        int y = Math.toIntExact(Math.round(location.getY()));
        int z = Math.toIntExact(Math.round(location.getZ()));
        String world = location.getWorld().getName();


        CustomPlayer cp = playerManager.get(player.getUniqueId());
        if (cp == null) return;

        Component windowTitle = configManager.getMessage(ConfigMessage.CONFIRMGUI_WINDOW_TITLE, cp.getLanguage()).decoration(TextDecoration.ITALIC, false);

        ConfirmHolder holder = new ConfirmHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, windowTitle);
        holder.setInventory(inv);

        for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26}) {
            ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            inv.setItem(i, filler);
        }

        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.displayName(configManager.getMessage(ConfigMessage.CONFIRMGUI_CONFIRM, cp.getLanguage()).decoration(TextDecoration.ITALIC, false));
        confirmMeta.lore(List.of(configManager.getMessage(homeActions.getMessage(), Map.of("price", String.valueOf(price)), cp.getLanguage()).decoration(TextDecoration.ITALIC, false)));
        confirm.setItemMeta(confirmMeta);

        inv.setItem(11, confirm);

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(configManager.getMessage(ConfigMessage.CONFIRMGUI_CANCEL, cp.getLanguage()).decoration(TextDecoration.ITALIC, false));
        cancel.setItemMeta(cancelMeta);

        inv.setItem(15, cancel);

        String url = main.getConfig().getString("homes.skin-url");

        ItemStack home = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) home.getItemMeta();
        meta.displayName(configManager.getMessage(homeActions.getTitle(), Map.of("home", homeName), cp.getLanguage()).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                configManager.getMessage(homeActions.getPriceLabel(), Map.of("price", String.valueOf(price)), cp.getLanguage()).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                configManager.getMessage(ConfigMessage.CONFIRMGUI_WORLD, Map.of("world", world), cp.getLanguage()).decoration(TextDecoration.ITALIC, false),
                configManager.getMessage(ConfigMessage.CONFIRMGUI_COORD_X, Map.of("x", String.valueOf(x)), cp.getLanguage()).decoration(TextDecoration.ITALIC, false),
                configManager.getMessage(ConfigMessage.CONFIRMGUI_COORD_Y, Map.of("y", String.valueOf(y)), cp.getLanguage()).decoration(TextDecoration.ITALIC, false),
                configManager.getMessage(ConfigMessage.CONFIRMGUI_COORD_Z, Map.of("z", String.valueOf(z)), cp.getLanguage()).decoration(TextDecoration.ITALIC, false)
        ));

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL(url));
        } catch (MalformedURLException e) {
            main.getLogger().severe("Failed to load skin-url: " + e.getMessage());
        }
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);

        home.setItemMeta(meta);

        inv.setItem(13, home);

        pending.put(player.getUniqueId(), new PendingHome(homeName, location, price, homeActions));
        player.openInventory(inv);


    }

    public PendingHome getPending(UUID uuid) {
        return pending.get(uuid);
    }

    public PendingHome removePending(UUID uuid) {
        return pending.remove(uuid);
    }
}
