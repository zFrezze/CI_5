package at.zFrezze.cyberInfra;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class TokenCraft {

    private final CyberInfra main;
    private ItemStack template;
    private NamespacedKey tokenKey;

    public NamespacedKey getTokenKey() {
        if (tokenKey == null) tokenKey = new NamespacedKey(main, "token");
        return tokenKey;
    }

    public TokenCraft(CyberInfra main) {
        this.main = main;
    }

    public ItemStack buildTokenHead() {

        if (tokenKey == null) tokenKey = new NamespacedKey(main, "token");

        String url = main.getConfig().getString("token.skin-url");

        ItemStack tokenHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) tokenHead.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL(url));
        } catch (MalformedURLException e) {
            main.getLogger().severe("Invalid token skin URL: " + e.getMessage());
        }
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);

        meta.getPersistentDataContainer().set(tokenKey, PersistentDataType.BYTE, (byte) 1);
        tokenHead.setItemMeta(meta);

        template = tokenHead;
        return tokenHead;
    }

    public ItemStack getTokenHead() {
        if (template == null) {
            buildTokenHead();
        }
        return template.clone();
    }

    public void registerRecipe() {
        ItemStack head = buildTokenHead();
        ShapedRecipe tokenRecipe = new ShapedRecipe(tokenKey, head);
        tokenRecipe.shape("ABA", "CDC", "ABA");
        tokenRecipe.setIngredient('A', Material.DIAMOND);
        tokenRecipe.setIngredient('B', Material.GOLD_BLOCK);
        tokenRecipe.setIngredient('C', Material.NETHERITE_INGOT);
        tokenRecipe.setIngredient('D', Material.CONDUIT);

        Bukkit.addRecipe(tokenRecipe);
    }
}