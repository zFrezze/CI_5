package at.zFrezze.cyberInfra.gui;

public enum HomeActions {

    SET_HOME("set", "Confirm Sethome", "You need to pay %price% tokens to set this home.", "Costs"),
    OVERRIDE_HOME("override", "Confirm Override-home", "You need to pay %price% tokens to override this home.", "Costs"),
    REMOVE_HOME("remove", "Confirm Removehome", "You get %price% tokens back for removing this home.", "Refund");

    private final String display;
    private final String guiName;
    private final String message;
    private final String priceLabel;

    HomeActions(String display, String guiName, String message, String priceLabel) {
        this.display = display;
        this.guiName = guiName;
        this.message = message;
        this.priceLabel = priceLabel;
    }

    public String getDisplay() {return display;}
    public String getGuiName() {return guiName;}
    public String getMessage() {return message;}
    public String getPriceLabel() {return priceLabel;}
}