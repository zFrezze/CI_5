package at.zFrezze.cyberInfra;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private TokenManager tokenManager;

    public JoinListener(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) {
            tokenManager.setToken(e.getPlayer().getUniqueId(), 100);
        }
    }

}
