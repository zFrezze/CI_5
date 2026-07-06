package at.zFrezze.cyberInfra;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class TokenListener implements Listener {

    private TokenManager tokenManager;

    TokenListener(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack result = e.getRecipe().getResult();

        if (result != null && result.hasItemMeta()) {
            if (result.getItemMeta().getPersistentDataContainer().has(TokenCraft.TOKEN_KEY, PersistentDataType.BYTE)) {

                e.setCancelled(true);

                if (e.isShiftClick()) {
                    e.getWhoClicked().sendMessage(ChatColor.RED + "Bitte einzeln craften (kein Shift-Klick).");
                } else {
                    CraftingInventory inv = e.getInventory();
                    for (int i = 1; i <= 9; i++) {
                        ItemStack item = inv.getItem(i);
                        if (item != null) item.setAmount(item.getAmount() - 1);
                    }
                    tokenManager.addToken(e.getWhoClicked().getUniqueId(), 100);
                    e.getWhoClicked().sendMessage("Du hast " + ChatColor.GREEN + "100 Tokens " + ChatColor.WHITE + "gecraftet");
                }
            }
        }
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