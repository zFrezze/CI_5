package at.zFrezze.cyberInfra.gui;

import at.zFrezze.cyberInfra.CyberInfra;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class LanguageGUI {

    private final CyberInfra main;
    private final NamespacedKey languageKey;

    public LanguageGUI(CyberInfra main) {
        this.main = main;
        this.languageKey = new NamespacedKey(main, "language");
    }

    public void open(Player player) {
        LanguageHolder holder = new LanguageHolder();
        Inventory inv = Bukkit.createInventory(holder, 9, "Language");
        holder.setInventory(inv);

        List<String> langs = List.of("en", "de", "at", "ch", "fr", "es", "pirate", "cat");
        int slot = 0;
        for (String lang : langs) {
            inv.setItem(slot, createHead(lang));
            slot++;
        }

        player.openInventory(inv);

    }

    public ItemStack createHead(String lang) {
        String skin = main.getConfig().getString("language-skins." + lang);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(skin));
        } catch (MalformedURLException e) {
            main.getLogger().severe("Invalid language skin URL for " + lang + ": " + e.getMessage());
        }
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
        meta.getPersistentDataContainer().set(languageKey, PersistentDataType.STRING, lang);

        meta.displayName(Component.text(lang).decoration(TextDecoration.ITALIC, false));

        head.setItemMeta(meta);
        return head;
    }

    public NamespacedKey getLanguageKey() {
        return languageKey;
    }

}
