package at.zFrezze.cyberInfra.config;

public enum ConfigMessage {

    //Shared across multiple commands
    GENERAL_NO_PERMISSION("general.no-permission"),
    GENERAL_NOT_ENOUGH_TOKENS("general.not-enough-tokens"),
    GENERAL_INVALID_NUMBER("general.invalid-number"),
    GENERAL_PLAYER_NOT_EXISTING("general.player-not-online"),

    LANGUAGE_INVALID("language.invalid"),
    LANGUAGE_CHANGED("language.changed"),

    //Token system
    TOKEN_CRAFTED("token.crafted"),
    TOKEN_DEPOSITED_ONE("token.deposited-one"),
    TOKEN_DEPOSITED_MANY("token.deposited-many"),
    TOKEN_BALANCE("token.balance"),
    TOKEN_BALANCE_OTHER("token.balance-other"),
    TOKEN_USAGE_INVALID("token.usage-invalid"),
    TOKEN_USAGE_SUB("token.usage-sub"),
    TOKEN_ACTION_DONE("token.action-done"),

    //Withdraw
    WITHDRAW_USAGE("withdraw.usage"),
    WITHDRAW_NOT_POSITIVE("withdraw.not-positive"),
    WITHDRAW_NO_SPACE("withdraw.no-space"),
    WITHDRAW_SUCCESS("withdraw.success"),

    //Home - teleport
    HOME_USAGE("home.usage"),
    HOME_NOT_EXISTING("home.not-existing"),
    HOME_TELEPORTED("home.teleported"),

    //Sethome
    SETHOME_USAGE("sethome.usage"),
    SETHOME_MAX_REACHED("sethome.max-reached"),
    SETHOME_CREATED("sethome.created"),

    //Removehome
    REMOVEHOME_USAGE("removehome.usage"),
    REMOVEHOME_REMOVED("removehome.removed"),

    //ConfirmGUI
    CONFIRMGUI_CONFIRM("gui.confirm"),
    CONFIRMGUI_CANCEL("gui.cancel"),
    CONFIRMGUI_WORLD("gui.world"),
    CONFIRMGUI_COORD_X("gui.coord-x"),
    CONFIRMGUI_COORD_Y("gui.coord-y"),
    CONFIRMGUI_COORD_Z("gui.coord-z"),

    //Tpa
    TPA_USAGE("tpa.usage"),
    TPA_SENDER_EQUAL_TARGET("tpa.sender-equal-target"),
    TPA_SENDER_OFFLINE("tpa.sender-offline"),
    TPA_ALREADY_PENDING("tpa.already-pending"),
    TPA_EXPIRE_SENDER("tpa.expire-sender"),
    TPA_EXPIRE_TARGET("tpa.expire-target"),
    TPA_SUCCESS_SENDER("tpa.success-sender"),
    TPA_SUCCESS_TARGET("tpa.success-target"),
    TPA_REQUEST_MESSAGE("tpa.request.message"),
    TPA_REQUEST_ACCEPT("tpa.request.accept"),
    TPA_REQUEST_DENY("tpa.request.deny"),
    TPA_REQUEST_ACCEPT_HOVER("tpa.request.accept-hover"),
    TPA_REQUEST_DENY_HOVER("tpa.request.deny-hover"),
    TPA_REQUEST_SENT_MESSAGE("tpa.request-sent.message"),
    TPA_REQUEST_SENT_CANCEL("tpa.request-sent.cancel"),
    TPA_REQUEST_SENT_CANCEL_HOVER("tpa.request-sent.cancel-hover"),

    //Tpaccept
    TPACCEPT_USAGE("tpaccept.usage"),
    TPACCEPT_NO_PENDING_REQUEST("tpaccept.no-request"),
    TPACCEPT_SENDER_NOT_ENOUGH_TOKENS("tpaccept.sender-not-enough-tokens"),

    //Tpdeny
    TPDENY_USAGE("tpdeny.usage"),
    TPDENY_NO_PENDING_REQUEST("tpdeny.no-request"),
    TPDENY_DENIED_TARGET("tpdeny.denied-target"),
    TPDENY_DENIED_SENDER("tpdeny.denied-sender"),

    //Tpcancel
    TPCANCEL_USAGE("tpcancel.usage"),
    TPCANCEL_NO_PENDING_REQUEST("tpcancel.no-request"),
    TPCANCEL_CANCELLED_SENDER("tpcancel.cancelled-sender"),
    TPCANCEL_CANCELLED_TARGET("tpcancel.cancelled-target"),

    //HomeActions
    CONFIRMGUI_TITLE_SET("gui.title-set"),
    CONFIRMGUI_TITLE_TELEPORT("gui.title-teleport"),
    CONFIRMGUI_TITLE_OVERRIDE("gui.title-override"),
    CONFIRMGUI_TITLE_REMOVE("gui.title-remove"),
    CONFIRMGUI_MESSAGE_SET("gui.message-set"),
    CONFIRMGUI_MESSAGE_OVERRIDE("gui.message-override"),
    CONFIRMGUI_MESSAGE_REMOVE("gui.message-remove"),
    CONFIRMGUI_MESSAGE_TELEPORT("gui.message-teleport"),
    CONFIRMGUI_LABEL_COSTS("gui.label-costs"),
    CONFIRMGUI_LABEL_REFUND("gui.label-refund"),
    CONFIRMGUI_WINDOW_TITLE("gui.window-title");

    private final String key;

    ConfigMessage(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}