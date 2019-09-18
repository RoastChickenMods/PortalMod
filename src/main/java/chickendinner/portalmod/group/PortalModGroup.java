package chickendinner.portalmod.group;

import chickendinner.portalmod.PortalMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class PortalModGroup extends ItemGroup {
    public static final PortalModGroup INSTANCE = new PortalModGroup();

    private PortalModGroup() {
        super(PortalMod.ID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(PortalMod.Blocks.PORTAL.get());
    }
}
