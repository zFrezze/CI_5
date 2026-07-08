package at.zFrezze.cyberInfra;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    private final CyberInfra main;
    private final PlayerManager playerManager;

    public JoinListener(CyberInfra main, PlayerManager playerManager) {
        this.main = main;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> playerManager.loadPlayer(uuid));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> playerManager.unloadPlayer(uuid));
    }
}