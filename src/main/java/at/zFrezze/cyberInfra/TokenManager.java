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
        if (withdrawAmount <= amount && withdrawAmount > 0 && withdrawAmount <= 64) {
            if (player.getInventory().firstEmpty() != -1) {
                token.put(uuid, amount - withdrawAmount);
                ItemStack token = TokenCraft.getTokenHead();
                token.setAmount(withdrawAmount);
                player.getInventory().addItem(token);
                return true;
            }
            return false;
        }
        return false;
    }

    public int getToken(UUID uuid) {return token.getOrDefault(uuid, 0);}


}
