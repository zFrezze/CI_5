package at.zFrezze.cyberInfra;

import java.util.UUID;

public class CustomPlayer {

    private final UUID uuid;
    private int token;

    public CustomPlayer(UUID uuid, int token) {
        this.uuid = uuid;
        this.token = token;
    }

    public UUID getUuid() {return uuid;}

    public int getToken() {return token;}

    public void setToken(int token) {
        this.token = token;
    }

    public void addToken(int amount) {
        this.token = this.token + amount;
    }

    public void removeToken(int amount) {
        this.token = this.token - amount;
    }
}