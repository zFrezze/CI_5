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

import java.util.*;

public class TpaManager {

    private final CyberInfra main;
    private final ConfigManager configManager;
    private final PlayerManager playerManager;
    private final Map<UUID, List<TpaRequest>> requests = new HashMap<>();

    public TpaManager(CyberInfra main, ConfigManager configManager, PlayerManager playerManager) {
        this.main = main;
        this.configManager = configManager;
        this.playerManager = playerManager;
    }

    public void sendTpa(Player player, Player target, TpaType type) {

        CustomPlayer cpPlayer = playerManager.get(player.getUniqueId());
        CustomPlayer cpTarget = playerManager.get(target.getUniqueId());
        if (cpPlayer == null || cpTarget == null) return;

        if (target == player) {
            player.sendActionBar(configManager.getMessage(ConfigMessage.TPA_SENDER_EQUAL_TARGET, cpPlayer.getLanguage()));
            return;
        }

        List<TpaRequest> existing = requests.get(target.getUniqueId());
        if (existing != null) {
            for (TpaRequest r : existing) {
                if (r.getSender().equals(player.getUniqueId())) {
                    player.sendActionBar(configManager.getMessage(ConfigMessage.TPA_ALREADY_PENDING,
                            Map.of("target", target.getName()), cpPlayer.getLanguage()));
                    return;
                }
            }
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(main, () -> {
            TpaRequest removed = removeRequest(target.getUniqueId(), player.getUniqueId());
            if (removed != null) {
                if (player.isOnline()) {
                    player.sendActionBar(configManager.getMessage(ConfigMessage.TPA_EXPIRE_SENDER,
                            Map.of("target", target.getName()), cpPlayer.getLanguage()));
                }
                if (target.isOnline()) {
                    target.sendActionBar(configManager.getMessage(ConfigMessage.TPA_EXPIRE_TARGET,
                            Map.of("player", player.getName()), cpTarget.getLanguage()));
                }
            }
        }, 20L * 60);

        requests.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>())
                .add(new TpaRequest(player.getUniqueId(), task, type));

        Component senderMessage = configManager.getMessage(ConfigMessage.TPA_REQUEST_SENT_MESSAGE,
                Map.of("target", target.getName(), "prefix", main.getPrefix()), cpPlayer.getLanguage());
        Component senderMessageCancel = configManager.getMessage(ConfigMessage.TPA_REQUEST_SENT_CANCEL,
                        Map.of("target", target.getName()), cpPlayer.getLanguage())
                .clickEvent(ClickEvent.runCommand("/tpcancel " + target.getName()))
                .hoverEvent(HoverEvent.showText(configManager.getMessage(ConfigMessage.TPA_REQUEST_SENT_CANCEL_HOVER, cpPlayer.getLanguage())));

        player.sendMessage(senderMessage);
        player.sendMessage(senderMessageCancel);

        Component targetMessage = configManager.getMessage(ConfigMessage.TPA_REQUEST_MESSAGE,
                Map.of("player", player.getName(), "prefix", main.getPrefix()), cpTarget.getLanguage());
        Component targetMessageAccept = configManager.getMessage(ConfigMessage.TPA_REQUEST_ACCEPT,
                        Map.of("player", player.getName()), cpTarget.getLanguage())
                .clickEvent(ClickEvent.runCommand("/tpaccept " + player.getName()))
                .hoverEvent(HoverEvent.showText(configManager.getMessage(ConfigMessage.TPA_REQUEST_ACCEPT_HOVER, cpTarget.getLanguage())));
        Component targetMessageDeny = configManager.getMessage(ConfigMessage.TPA_REQUEST_DENY,
                        Map.of("player", player.getName()), cpTarget.getLanguage())
                .clickEvent(ClickEvent.runCommand("/tpdeny " + player.getName()))
                .hoverEvent(HoverEvent.showText(configManager.getMessage(ConfigMessage.TPA_REQUEST_DENY_HOVER, cpTarget.getLanguage())));

        target.sendMessage(targetMessage);
        target.sendMessage(targetMessageAccept);
        target.sendMessage(targetMessageDeny);
    }

    public TpaRequest removeRequest(UUID target, UUID senderUuid) {
        List<TpaRequest> list = requests.get(target);
        if (list == null) return null;

        for (TpaRequest request : list) {
            if (request.getSender().equals(senderUuid)) {
                list.remove(request);
                request.getTimeoutTask().cancel();
                if (list.isEmpty()) {
                    requests.remove(target);
                }
                return request;
            }
        }
        return null;
    }

    public TpaRequest getRequest(UUID target, UUID senderUuid) {
        List<TpaRequest> list = requests.get(target);
        if (list == null) return null;

        for (TpaRequest request : list) {
            if (request.getSender().equals(senderUuid)) {
                return request;
            }
        }
        return null;
    }

    public List<String> getSenderNames(UUID target) {
        List<TpaRequest> list = requests.get(target);
        if (list == null) return new ArrayList<>();

        List<String> names = new ArrayList<>();
        for (TpaRequest request : list) {
            Player sender = Bukkit.getPlayer(request.getSender());
            if (sender != null) {
                names.add(sender.getName());
            }
        }
        return names;
    }

    public List<String> getSentToNames(UUID senderUuid) {
        List<String> names = new ArrayList<>();

        for (Map.Entry<UUID, List<TpaRequest>> entry : requests.entrySet()) {
            for (TpaRequest request : entry.getValue()) {
                if (request.getSender().equals(senderUuid)) {
                    Player target = Bukkit.getPlayer(entry.getKey());
                    if (target != null) {
                        names.add(target.getName());
                    }
                }
            }
        }
        return names;
    }
}