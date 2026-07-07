package at.zFrezze.cyberInfra;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenListener implements Listener {

    private static final int TOKENS_PER_CRAFT = 100;

    private final Plugin plugin;
    private final TokenManager tokenManager;

    private final Set<UUID> readyToCraft = ConcurrentHashMap.newKeySet();

    TokenListener(Plugin plugin, TokenManager tokenManager) {
        this.plugin = plugin;
        this.tokenManager = tokenManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        if (e.getView().getPlayer() instanceof Player player) {
            ItemStack result = e.getInventory().getResult();
            if (result != null && result.hasItemMeta()
                    && result.getItemMeta().getPersistentDataContainer()
                    .has(TokenCraft.TOKEN_KEY, PersistentDataType.BYTE)) {
                readyToCraft.add(player.getUniqueId());
            } else {
                readyToCraft.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof CraftingInventory inv) {
            if (e.getSlot() == 0) {
                if (e.getWhoClicked() instanceof Player player) {
                    if (readyToCraft.contains(player.getUniqueId())) {

                        e.setCancelled(true);

                        int count = e.isShiftClick() ? maxCrafts(inv) : 1;
                        if (count > 0) {
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
                            tokenManager.addToken(player.getUniqueId(), total);
                            player.sendMessage("Du hast " + ChatColor.GREEN + total + " Tokens " + ChatColor.WHITE + "gecraftet");

                            inv.setMatrix(next);
                            player.updateInventory();
                        }
                    }
                }
            }
        }
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
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                && e.getHand() == EquipmentSlot.HAND) {

            ItemStack item = e.getItem();

            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                if (meta.getPersistentDataContainer().has(TokenCraft.TOKEN_KEY, PersistentDataType.BYTE)) {
                    e.setCancelled(true);

                    int stackAmount = item.getAmount();

                    if (e.getPlayer().isSneaking()) {
                        item.setAmount(stackAmount - 1);
                        e.getPlayer().getInventory().setItemInMainHand(item.getAmount() > 0 ? item : null);
                        tokenManager.addToken(e.getPlayer().getUniqueId(), 1);
                        e.getPlayer().sendMessage("Du hast " + ChatColor.GREEN + "1 Token " + ChatColor.WHITE + "eingezahlt");
                    } else {
                        e.getPlayer().getInventory().setItemInMainHand(null);
                        tokenManager.addToken(e.getPlayer().getUniqueId(), stackAmount);
                        e.getPlayer().sendMessage("Du hast " + ChatColor.GREEN + stackAmount + " Tokens " + ChatColor.WHITE + "eingezahlt");
                    }
                }
            }
        }
    }
}