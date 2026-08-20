package at.zFrezze.cyberInfra.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class DeathListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();

        if (killer == null) return;
        if (!killer.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;

        int length = random.nextInt(5, 16);
        Component hidden = Component.text("x".repeat(length)).decoration(TextDecoration.OBFUSCATED, true);
        Component deathMessage = e.deathMessage().replaceText(
                TextReplacementConfig.builder().matchLiteral(killer.getName()).replacement(hidden).build()
        );

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName()) {
            Component weaponName = weapon.getItemMeta().displayName();
            String weaponPlain = PlainTextComponentSerializer.plainText().serialize(weaponName);
            deathMessage = deathMessage.replaceText(
                    TextReplacementConfig.builder().matchLiteral(weaponPlain).replacement(hidden).build()
            );
        }
        e.deathMessage(deathMessage);
    }

}
