package at.zFrezze.cyberInfra.listeners;

import at.zFrezze.cyberInfra.TokenCraft;
import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import at.zFrezze.cyberInfra.data.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenListener implements Listener {

    private static final int TOKENS_PER_CRAFT = 100;

    private final PlayerManager playerManager;
    private final TokenCraft tokenCraft;
    private final ConfigManager configManager;

    private final Set<UUID> readyToCraft = ConcurrentHashMap.newKeySet();

    public TokenListener(PlayerManager playerManager, TokenCraft tokenCraft, ConfigManager configManager) {
        this.playerManager = playerManager;
        this.tokenCraft = tokenCraft;
        this.configManager = configManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        if (!(e.getView().getPlayer() instanceof Player player)) {
            return;
        }

        ItemStack result = e.getInventory().getResult();
        if (result != null && result.hasItemMeta()
                && result.getItemMeta().getPersistentDataContainer()
                .has(tokenCraft.getTokenKey(), PersistentDataType.BYTE)) {
            readyToCraft.add(player.getUniqueId());
        } else {
            readyToCraft.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent e) {
        if (!(e.getClickedInventory() instanceof CraftingInventory inv)) {
            return;
        }
        if (e.getSlot() != 0) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!readyToCraft.contains(player.getUniqueId())) {
            return;
        }

        e.setCancelled(true);

        int count = e.isShiftClick() ? maxCrafts(inv) : 1;
        if (count <= 0) {
            return;
        }

        readyToCraft.remove(player.getUniqueId());

        ItemStack[] old = inv.getMatrix();
        ItemStack[] next = new ItemStack[old.length];
        for (int i = 0; i < old.length; i++) {
            ItemStack item = old[i];
            if (item == null || item.getType() == Material.AIR) {
                next[i] = null;
            } else {
                ItemStack copy = item.clone();
                int left = copy.getAmount() - count;
                if (left <= 0) {
                    next[i] = null;
                } else {
                    copy.setAmount(left);
                    next[i] = copy;
                }
            }
        }

        int total = count * TOKENS_PER_CRAFT;
        playerManager.addToken(player.getUniqueId(), total);
        player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_CRAFTED, Map.of(
                "amount", String.valueOf(total)
        )));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        inv.setMatrix(next);
        player.updateInventory();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        readyToCraft.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        readyToCraft.remove(e.getPlayer().getUniqueId());
    }

    private int maxCrafts(CraftingInventory inv) {
        int min = Integer.MAX_VALUE;
        for (ItemStack item : inv.getMatrix()) {
            if (item != null && item.getType() != Material.AIR) {
                min = Math.min(min, item.getAmount());
            }
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                || e.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = e.getItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(tokenCraft.getTokenKey(), PersistentDataType.BYTE)) {
            return;
        }

        e.setCancelled(true);

        Player player = e.getPlayer();
        int stackAmount = item.getAmount();

        if (player.isSneaking()) {
            item.setAmount(stackAmount - 1);
            player.getInventory().setItemInMainHand(item.getAmount() > 0 ? item : null);
            playerManager.addToken(player.getUniqueId(), 1);
            player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_DEPOSITED_ONE));
        } else {
            player.getInventory().setItemInMainHand(null);
            playerManager.addToken(player.getUniqueId(), stackAmount);
            player.sendActionBar(configManager.getMessage(ConfigMessage.TOKEN_DEPOSITED_MANY, Map.of(
                    "amount", String.valueOf(stackAmount)
            )));
        }
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}