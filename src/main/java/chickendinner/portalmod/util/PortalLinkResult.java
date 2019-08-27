package chickendinner.portalmod.util;

import chickendinner.portalmod.PortalMod;

public enum PortalLinkResult {
    INVALID_STATE_ERROR,
    MISSING_DESTINATION_ERROR,
    ALREADY_LINKED_ERROR,
    SHAPE_MISMATCH_ERROR,
    LINK_TO_SELF_ERROR,
    SUCCESS;

    private String unlocalizedMessage;

    PortalLinkResult() {
        this.unlocalizedMessage = "message." + PortalMod.ID + ".linkresult." + this.name().toLowerCase();
    }

    public String getUnlocalizedMessage() {
        return unlocalizedMessage;
    }
}
