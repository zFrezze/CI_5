package at.zFrezze.cyberInfra.data;

import at.zFrezze.cyberInfra.CyberInfra;
import at.zFrezze.cyberInfra.config.ConfigManager;
import at.zFrezze.cyberInfra.config.ConfigMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TpaManager {

    private final CyberInfra main;
    private final ConfigManager configManager;
    private final PlayerManager playerManager;
    private final Map<UUID, TpaRequest> requests = new HashMap<>();

    public TpaManager(CyberInfra main, ConfigManager configManager, PlayerManager playerManager) {
        this.main = main;
        this.configManager = configManager;
        this.playerManager = playerManager;
    }

    public void sendTpa(Player player, Player target) {

        CustomPlayer cpPlayer = playerManager.get(player.getUniqueId());
        CustomPlayer cpTarget = playerManager.get(target.getUniqueId());
        if (cpPlayer == null || cpTarget == null) return;

        if (target == player) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.TPA_SENDER_EQUAL_TARGET, cpPlayer.getLanguage()));
            return;
        }

        Component targetMessage = Component.text()
                .append(
                        configManager.getMessage(
                                ConfigMessage.TPA_REQUEST_MESSAGE,
                                Map.of("player", player.getName(), "prefix", main.getPrefix()),
                                cpTarget.getLanguage()
                        )
                ).build();

        Component targetMessageAccept = Component.text()
                .append(
                        configManager.getMessage(
                                ConfigMessage.TPA_REQUEST_ACCEPT, Map.of("player", player.getName()),
                                cpTarget.getLanguage()
                        ).clickEvent(
                                ClickEvent.runCommand("/tpaccept " + player.getName())
                        ).hoverEvent(
                                HoverEvent.showText(configManager.getMessage(ConfigMessage.TPA_REQUEST_ACCEPT_HOVER, cpTarget.getLanguage()))
                        )
                ).build();
        Component targetMessageDeny = Component.text()
                .append(
                        configManager.getMessage(
                                ConfigMessage.TPA_REQUEST_DENY, Map.of("player", player.getName()),
                                cpTarget.getLanguage()
                        ).clickEvent(
                                ClickEvent.runCommand("/tpdeny " + player.getName())
                        ).hoverEvent(
                                HoverEvent.showText(configManager.getMessage(ConfigMessage.TPA_REQUEST_DENY_HOVER, cpTarget.getLanguage()))
                        )
                ).build();

        Component senderMessage = Component.text().append(
                configManager.getMessage(ConfigMessage.TPA_REQUEST_SENT_MESSAGE, Map.of("target", target.getName(), "prefix", main.getPrefix()), cpPlayer.getLanguage())).build();
        Component senderMessageDeny = Component.text()
                .append(
                        configManager.getMessage(ConfigMessage.TPA_REQUEST_SENT_CANCEL, Map.of("player", target.getName()), cpPlayer.getLanguage())
                ).clickEvent(
                        ClickEvent.runCommand("/tpcancel " + target.getName())
                ).hoverEvent(
                        HoverEvent.showText(configManager.getMessage(ConfigMessage.TPA_REQUEST_SENT_CANCEL_HOVER, cpPlayer.getLanguage()))
                ).build();

        player.sendMessage(senderMessage);
        player.sendMessage(senderMessageDeny);

        target.sendMessage(targetMessage);
        target.sendMessage(targetMessageAccept);
        target.sendMessage(targetMessageDeny);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(main, () -> {
            if (requests.remove(target.getUniqueId()) != null) {
                player.sendActionBar(configManager.getMessage(ConfigMessage.TPA_EXPIRE_SENDER, Map.of("target", target.getName()), cpPlayer.getLanguage()));
                target.sendActionBar(configManager.getMessage(ConfigMessage.TPA_EXPIRE_TARGET, Map.of("player", player.getName()), cpTarget.getLanguage()));
            }
        }, 20L * 60);

        requests.put(target.getUniqueId(), new TpaRequest(player.getUniqueId(), task));
    }

    public boolean hasOpenTpa(Player target) {
        return requests.containsKey(target.getUniqueId());
    }

    public TpaRequest removeRequest(UUID target) {
        TpaRequest request = requests.remove(target);
        if (request != null) {
            request.getTimeoutTask().cancel();
        }
        return request;
    }

}
