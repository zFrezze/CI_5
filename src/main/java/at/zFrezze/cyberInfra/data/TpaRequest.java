package at.zFrezze.cyberInfra.data;

import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;

public class TpaRequest {

    private final UUID sender;
    private final BukkitTask timeoutTask;
    private final TpaType tpaType;

    public TpaRequest(UUID sender, BukkitTask timeoutTask, TpaType tpaType) {
        this.sender = sender;
        this.timeoutTask = timeoutTask;
        this.tpaType = tpaType;
    }

    public UUID getSender() {
        return sender;
    }

    public BukkitTask getTimeoutTask() {
        return timeoutTask;
    }

    public TpaType getTpaType() {return tpaType;}
}