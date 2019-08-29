package chickendinner.portalmod.util;

import chickendinner.portalmod.reference.TranslatedMessage;

public enum PortalLinkResult {
    INVALID_STATE_ERROR(TranslatedMessage.PORTAL_LINK_FAIL_INVALID_STATE),
    MISSING_DESTINATION_ERROR(TranslatedMessage.PORTAL_LINK_FAIL_MISSING_DESTINATION),
    ALREADY_LINKED_ERROR(TranslatedMessage.PORTAL_LINK_FAIL_ALREADY_LINKED),
    SHAPE_MISMATCH_ERROR(TranslatedMessage.PORTAL_LINK_FAIL_WRONG_SHAPE),
    LINK_TO_SELF_ERROR(TranslatedMessage.PORTAL_LINK_FAIL_SELF_LINK),
    SUCCESS(TranslatedMessage.PORTAL_LINK_SUCCESS);

    private TranslatedMessage message;

    PortalLinkResult(TranslatedMessage message) {
        this.message = message;
    }

    public TranslatedMessage getMessage() {
        return message;
    }
}
