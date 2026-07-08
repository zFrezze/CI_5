package at.zFrezze.cyberInfra;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final CyberInfra main;
    private final ConcurrentHashMap<UUID, CustomPlayer> players = new ConcurrentHashMap<>();

    public PlayerManager(CyberInfra main) {
        this.main = main;
    }

    public CustomPlayer get(UUID uuid) {
        return players.get(uuid);
    }

    public void loadPlayer(UUID uuid) {
        int token = 0;
        boolean found = false;

        try (PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("SELECT token FROM tokens WHERE uuid = ?");) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                token = rs.getInt("token");
                found = true;
            }
        } catch (SQLException e) {
            main.getLogger().severe("Load failed for " + uuid + ": " + e.getMessage());
        }

        if (!found) {
            token = 100;
        }

        players.put(uuid, new CustomPlayer(uuid, token));
    }

    public void savePlayer(UUID uuid) {
        CustomPlayer cp = players.get(uuid);
        if (cp == null) {
            return;
        }

        try (PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("INSERT INTO tokens (uuid, token) VALUES (?, ?) " + "ON DUPLICATE KEY UPDATE token = ?");) {
            statement.setString(1, uuid.toString());
            statement.setInt(2, cp.getToken());
            statement.setInt(3, cp.getToken());
            statement.executeUpdate();
        } catch (SQLException e) {
            main.getLogger().severe("Save failed for " + uuid + ": " + e.getMessage());
        }
    }

    public void unloadPlayer(UUID uuid) {
        savePlayer(uuid);
        players.remove(uuid);
    }

    public void saveAll() {
        for (UUID uuid : players.keySet()) {
            savePlayer(uuid);
        }
    }

    private int getTokenDB(UUID uuid) {

        int token = 0;

        try (PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("SELECT token FROM tokens WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                token = rs.getInt("token");
            }
        } catch (SQLException e) {
            main.getLogger().severe("Load failed for " + uuid + ": " + e.getMessage());
        }
        return token;
    }




    public int getToken(UUID uuid) {
        CustomPlayer cp = players.get(uuid);
        if (cp != null) {
            return cp.getToken();
        }
        return getTokenDB(uuid);
    }

    public void setToken(UUID uuid, int amount) {
        CustomPlayer cp = players.get(uuid);
        if (cp != null) {
            cp.setToken(amount);
            return;
        }
        try(PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("INSERT INTO tokens (uuid, token) VALUES (?, ?) ON DUPLICATE KEY UPDATE token = ?")) {
            statement.setString(1, uuid.toString());
            statement.setInt(2, amount);
            statement.setInt(3, amount);
            statement.executeUpdate();
        }catch (SQLException e) {
            main.getLogger().severe("Save failed for " + uuid + ": " + e.getMessage());
        }
    }

    public void addToken(UUID uuid, int amount) {
        int finalAmount = getToken(uuid) + amount;
        setToken(uuid, finalAmount);
    }

    public void removeToken(UUID uuid, int amount) {
        int finalAmount = getToken(uuid) - amount;
        setToken(uuid, finalAmount);
    }

    public boolean withdrawToken(UUID uuid, Player player, int withdrawAmount) {
        CustomPlayer cp = players.get(uuid);
        if (cp == null) {
            return false;
        }

        int amount = cp.getToken();
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

        cp.setToken(amount - withdrawAmount);

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
}