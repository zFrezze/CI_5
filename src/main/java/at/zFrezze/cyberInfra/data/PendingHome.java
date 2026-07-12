package at.zFrezze.cyberInfra.data;

import at.zFrezze.cyberInfra.gui.HomeActions;
import org.bukkit.Location;

public class PendingHome {

    private final String name;
    private final Location location;
    private final int price;
    private final HomeActions actions;


    public PendingHome(String name, Location location, int price, HomeActions actions) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.actions = actions;
    }

    public String getName() {return name;}
    public Location getLocation() {return location;}
    public int getPrice() {return price;}
    public HomeActions getAction() {return actions;}
}
