package chickendinner.portalmod.reference;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public enum TranslatedMessage {
    /* PORTAL LINK MESSAGES */
    PORTAL_LINK_SUCCESS("portal.link.success"),
    PORTAL_LINK_FAIL_INVALID_STATE("portal.link.invalid_state"),
    PORTAL_LINK_FAIL_MISSING_DESTINATION("portal.link.missing_destination"),
    PORTAL_LINK_FAIL_ALREADY_LINKED("portal.link.already_linked"),
    PORTAL_LINK_FAIL_WRONG_SHAPE("portal.link.wrong_shape"),
    PORTAL_LINK_FAIL_SELF_LINK("portal.link.self_link"),
    PORTAL_LINK_POSITION_CLEARED("portal.link.cleared"),
    PORTAL_LINK_STORED_POSITION("portal.link.stored_position", 1),

    /* PORTAL UNLINK MESSAGES */
    PORTAL_UNLINK_SUCCESS("portal.unlink.success"),
    PORTAL_UNLINK_FAIL_NOT_LINKED("portal.unlink.not_linked"),

    /* */;
    private String translationKey;
    private int argCount;

    TranslatedMessage(String key) {
        this(key, 0);
    }

    TranslatedMessage(String key, int argCount) {
        this.translationKey = message(key);
        this.argCount = argCount;
    }

    private static String message(String key) {
        return String.format("message.%s.%s", PortalMod.ID, key);
    }

    public TranslationTextComponent getTranslated(Object... args) {
        if (args.length != argCount) {
            Util.printErrorWithStackTrace("Incorrect number of arguments given to message key for translation.");
            return new TranslationTextComponent(translationKey);
        }
        return new TranslationTextComponent(translationKey, args);
    }

    @Override
    public String toString() {
        return translationKey;
    }
}
