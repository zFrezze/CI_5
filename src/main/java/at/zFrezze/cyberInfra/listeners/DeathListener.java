package at.zFrezze.cyberInfra.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();

        if (killer == null) return;
        if (!killer.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;

        Random random = new Random();
        int length = random.nextInt(5, 16);
        String hidden = ChatColor.MAGIC + "x".repeat(length) + ChatColor.RESET;
        String deathMessage = e.getDeathMessage().replace(killer.getName(), hidden);

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName()) {
            String weaponName = weapon.getItemMeta().getDisplayName();
            deathMessage = deathMessage.replace(weaponName, hidden);


        }
        e.setDeathMessage(deathMessage);
    }

}
