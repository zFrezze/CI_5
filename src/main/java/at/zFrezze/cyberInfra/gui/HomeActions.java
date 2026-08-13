package at.zFrezze.cyberInfra.gui;

import at.zFrezze.cyberInfra.config.ConfigMessage;

public enum HomeActions {

    SET_HOME(ConfigMessage.CONFIRMGUI_TITLE_SET, ConfigMessage.CONFIRMGUI_MESSAGE_SET, ConfigMessage.CONFIRMGUI_LABEL_COSTS),
    OVERRIDE_HOME(ConfigMessage.CONFIRMGUI_TITLE_OVERRIDE, ConfigMessage.CONFIRMGUI_MESSAGE_OVERRIDE, ConfigMessage.CONFIRMGUI_LABEL_COSTS),
    REMOVE_HOME(ConfigMessage.CONFIRMGUI_TITLE_REMOVE, ConfigMessage.CONFIRMGUI_MESSAGE_REMOVE, ConfigMessage.CONFIRMGUI_LABEL_REFUND),
    TELEPORT_HOME(ConfigMessage.CONFIRMGUI_TITLE_TELEPORT, ConfigMessage.CONFIRMGUI_MESSAGE_TELEPORT, ConfigMessage.CONFIRMGUI_LABEL_COSTS);

    private final ConfigMessage title;
    private final ConfigMessage message;
    private final ConfigMessage priceLabel;

    HomeActions(ConfigMessage title, ConfigMessage message, ConfigMessage priceLabel) {
        this.title = title;
        this.message = message;
        this.priceLabel = priceLabel;
    }

    public ConfigMessage getTitle() { return title; }
    public ConfigMessage getMessage() { return message; }
    public ConfigMessage getPriceLabel() { return priceLabel; }
}