package at.zFrezze.cyberInfra.data;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.TokenCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final CyberInfra main;
    private final TokenCraft tokenCraft;
    private final ConcurrentHashMap<UUID, CustomPlayer> players = new ConcurrentHashMap<>();

    public PlayerManager(CyberInfra main, TokenCraft tokenCraft) {
        this.main = main;
        this.tokenCraft = tokenCraft;
    }

    public CustomPlayer get(UUID uuid) {
        return players.get(uuid);
    }

    public void loadPlayer(UUID uuid) {
        int token = 0;
        boolean found = false;

        try (PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("SELECT token FROM tokens WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                token = rs.getInt("token");
                found = true;
            }
        } catch (SQLException e) {
            main.getLogger().severe("Token load failed for " + uuid + ": " + e.getMessage());
        }

        if (!found) {
            token = 100;
        }

        CustomPlayer cp = new CustomPlayer(uuid, token);
        players.put(uuid, cp);

        try (PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("SELECT name, world, x, y, z, yaw, pitch FROM homes WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    continue;
                }

                String name = rs.getString("name");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                float yaw = rs.getFloat("yaw");
                float pitch = rs.getFloat("pitch");

                Location location = new Location(world, x, y, z, yaw, pitch);
                cp.setHome(name, location);
            }
        } catch (SQLException e) {
            main.getLogger().severe("Home load failed for " + uuid + ": " + e.getMessage());
        }
    }

    public void savePlayer(UUID uuid) {
        CustomPlayer cp = players.get(uuid);
        if (cp == null) {
            return;
        }

        Connection conn = main.getDatabase().getConnection();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO tokens (uuid, token) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET token = excluded.token")) {
                statement.setString(1, uuid.toString());
                statement.setInt(2, cp.getToken());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = conn.prepareStatement("DELETE FROM homes WHERE uuid = ?")) {
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO homes (uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (Map.Entry<String, Location> entry : cp.getHomes().entrySet()) {
                    Location location = entry.getValue();
                    statement.setString(1, uuid.toString());
                    statement.setString(2, entry.getKey());
                    statement.setString(3, location.getWorld().getName());
                    statement.setDouble(4, location.getX());
                    statement.setDouble(5, location.getY());
                    statement.setDouble(6, location.getZ());
                    statement.setFloat(7, location.getYaw());
                    statement.setFloat(8, location.getPitch());
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                main.getLogger().severe("Rollback failed for " + uuid + ": " + ex.getMessage());
            }
            main.getLogger().severe("Save failed for " + uuid + ": " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
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
        try (PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("INSERT INTO tokens (uuid, token) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET token = excluded.token")) {
            statement.setString(1, uuid.toString());
            statement.setInt(2, amount);
            statement.executeUpdate();
        } catch (SQLException e) {
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
            ItemStack head = tokenCraft.getTokenHead();
            head.setAmount(stackSize);
            player.getInventory().addItem(head);
            remaining -= stackSize;
        }
        return true;
    }
}