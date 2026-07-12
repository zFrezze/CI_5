package at.zFrezze.cyberInfra.gui;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.data.PendingHome;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private final Map<UUID, PendingHome> pending = new HashMap<>();

    public ConfirmGUI(CyberInfra main) {
        this.main = main;
    }

    public void openGUI(Player player, HomeActions homeActions, String homeName, Location location, int price, String title) {

        int x = Math.toIntExact(Math.round(location.getX()));
        int y = Math.toIntExact(Math.round(location.getY()));
        int z = Math.toIntExact(Math.round(location.getZ()));
        String world = location.getWorld().getName();


        String actionDisplay = homeActions.getDisplay();

        Inventory inv = Bukkit.createInventory(player, 27, title);

        for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26}) {
            ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            inv.setItem(i, filler);
        }

        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm");
        String message = homeActions.getMessage().replace("%price%", String.valueOf(price));
        confirmMeta.setLore(List.of(ChatColor.WHITE + message));
        confirm.setItemMeta(confirmMeta);

        inv.setItem(11, confirm);

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
        cancel.setItemMeta(cancelMeta);

        inv.setItem(15, cancel);

        String url = main.getConfig().getString("homes.skin-url");

        ItemStack home = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) home.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Confirm to " + actionDisplay + " home " + ChatColor.GREEN + homeName + ChatColor.WHITE + "?");
        meta.setLore(List.of(
                ChatColor.WHITE + homeActions.getPriceLabel() + ": " + ChatColor.GREEN + price,
                " ",
                ChatColor.GRAY + "World: " + ChatColor.WHITE + world,
                ChatColor.GRAY + "x: " + ChatColor.WHITE + x,
                ChatColor.GRAY + "y: " + ChatColor.WHITE + y,
                ChatColor.GRAY + "z: " + ChatColor.WHITE + z
        ));

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        player.sendMessage(url);
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

    public void removePending(UUID uuid) {
        pending.remove(uuid);
    }
}
