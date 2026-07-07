package at.zFrezze.cyberInfra;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class TokenManager {

    private HashMap<UUID, Integer> token = new HashMap<>();

    public TokenManager(CyberInfra cyberInfra) {
    }

    public void setToken(UUID uuid, int amount) {
        token.put(uuid, amount);
    }

    public void addToken(UUID uuid, int addAmount) {
        int finalAmount = token.getOrDefault(uuid, 0) + addAmount;
        token.put(uuid, finalAmount);
    }

    public void removeToken(UUID uuid, int removeAmount) {
        int finalAmount = token.getOrDefault(uuid, 0) - removeAmount;
        token.put(uuid, finalAmount);
    }

    public boolean withdrawToken(UUID uuid, Player player, int withdrawAmount) {
        int amount = token.getOrDefault(uuid, 0);
        if (withdrawAmount <= 0 || withdrawAmount > amount) {
            return false;
        }

        int neededSlots = (withdrawAmount + 63) / 64;
        int freeSlots = 0;
        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content == null || content.getType().isAir()) {
                freeSlots++;
            }
        }
        if (freeSlots < neededSlots) {
            return false;
        }

        token.put(uuid, amount - withdrawAmount);

        int remaining = withdrawAmount;
        while (remaining > 0) {
            int stackSize = Math.min(64, remaining);
            ItemStack head = TokenCraft.getTokenHead();
            head.setAmount(stackSize);
            player.getInventory().addItem(head);
            remaining -= stackSize;
        }
        return true;
    }

    public int getToken(UUID uuid) {return token.getOrDefault(uuid, 0);}


}
