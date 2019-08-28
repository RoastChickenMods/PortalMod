package chickendinner.portalmod.util;

import chickendinner.portalmod.reference.MessageKey;

public enum PortalLinkResult {
    INVALID_STATE_ERROR(MessageKey.PORTAL_LINK_FAIL_INVALID_STATE),
    MISSING_DESTINATION_ERROR(MessageKey.PORTAL_LINK_FAIL_MISSING_DESTINATION),
    ALREADY_LINKED_ERROR(MessageKey.PORTAL_LINK_FAIL_ALREADY_LINKED),
    SHAPE_MISMATCH_ERROR(MessageKey.PORTAL_LINK_FAIL_WRONG_SHAPE),
    LINK_TO_SELF_ERROR(MessageKey.PORTAL_LINK_FAIL_SELF_LINK),
    SUCCESS(MessageKey.PORTAL_LINK_SUCCESS);

    private MessageKey message;

    PortalLinkResult(MessageKey message) {
        this.message = message;
    }

    public MessageKey getMessage() {
        return message;
    }
}
