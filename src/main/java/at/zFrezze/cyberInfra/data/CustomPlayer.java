package at.zFrezze.cyberInfra.data;

import org.bukkit.Location;

import java.util.*;

public class CustomPlayer {

    private final UUID uuid;
    private int token;
    private String language;

    private final Map<String, Location> homes = new HashMap<>();

    public CustomPlayer(UUID uuid, int token, String language) {
        this.uuid = uuid;
        this.token = token;
        this.language = language;
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
        this.token = Math.max(0, this.token - amount);
    }

    public Location getHome(String name) {return homes.get(name);}
    public Map<String, Location> getHomes() {return homes;}
    public int getHomeAmount() {return homes.size();}
    public void setHome(String name, Location location) {
        homes.put(name, location);
    }
    public void removeHome(String name) {
        homes.remove(name);
    }

    public String getLanguage() {return language;}
    public void setLanguage(String language) {this.language = language;}
}