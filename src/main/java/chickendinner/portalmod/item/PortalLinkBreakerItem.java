package chickendinner.portalmod.item;

import chickendinner.portalmod.reference.MessageKey;
import chickendinner.portalmod.tileentity.PortalTileEntity;
import chickendinner.portalmod.util.PlayerUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalLinkBreakerItem extends Item {
    public PortalLinkBreakerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof PortalTileEntity)) {
            return ActionResultType.FAIL; // Did nothing
        }
        PortalTileEntity portal = (PortalTileEntity) tile;
        if (!portal.isLinked()) {
            PlayerUtil.tellPlayer(context.getPlayer(), MessageKey.PORTAL_UNLINK_FAIL_NOT_LINKED);
            return ActionResultType.SUCCESS; // Did something (told the player they are dumb)
        }
        portal.unlinkPortal();
        PlayerUtil.tellPlayer(context.getPlayer(), MessageKey.PORTAL_UNLINK_SUCCESS);
        return ActionResultType.SUCCESS; // Broke link
    }
}
