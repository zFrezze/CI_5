package at.zFrezze.cyberInfra.config;

public enum ConfigMessage {

    //Shared across multiple commands
    GENERAL_NO_PERMISSION("general.no-permission"),
    GENERAL_NOT_ENOUGH_TOKENS("general.not-enough-tokens"),
    GENERAL_INVALID_NUMBER("general.invalid-number"),

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
    REMOVEHOME_REMOVED("removehome.removed");


    private final String key;

    ConfigMessage(String key) {
        this.key = key;
    }

    public String getKey() {return key;}
}

