package at.zFrezze.cyberInfra.gui;

public enum HomeActions {

    SET_HOME("set", "You need to pay %price% tokens to set this home.", "Costs"),
    OVERRIDE_HOME("override", "You need to pay %price% tokens to override this home.", "Costs"),
    REMOVE_HOME("remove", "You get %price% tokens back for removing this home.", "Refund"),
    TELEPORT_HOME("teleport to", "Confirm home teleportation. ", "Costs");

    private final String display;
    private final String message;
    private final String priceLabel;

    HomeActions(String display, String message, String priceLabel) {
        this.display = display;
        this.message = message;
        this.priceLabel = priceLabel;
    }

    public String getDisplay() {return display;}
    public String getMessage() {return message;}
    public String getPriceLabel() {return priceLabel;}
}