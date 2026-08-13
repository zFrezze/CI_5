package at.zFrezze.cyberInfra.data;

import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;

public class TpaRequest {

    private final UUID sender;
    private final BukkitTask timeoutTask;

    public TpaRequest(UUID sender, BukkitTask timeoutTask) {
        this.sender = sender;
        this.timeoutTask = timeoutTask;
    }

    public UUID getSender() {
        return sender;
    }

    public BukkitTask getTimeoutTask() {
        return timeoutTask;
    }
}