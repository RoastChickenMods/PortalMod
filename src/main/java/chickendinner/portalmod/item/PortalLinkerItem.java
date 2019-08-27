package chickendinner.portalmod.item;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.block.PortalBlock;
import chickendinner.portalmod.registry.Names;
import chickendinner.portalmod.tileentity.PortalTileEntity;
import chickendinner.portalmod.util.PlayerUtil;
import chickendinner.portalmod.util.PortalLinkResult;
import chickendinner.portalmod.util.VectorUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import static chickendinner.portalmod.registry.Names.PORTAL_LINKER;

public class PortalLinkerItem extends Item {

    public PortalLinkerItem(Properties properties) {
        super(properties);
        this.addPropertyOverride(new ResourceLocation(PortalMod.ID, Names.PORTAL_LINKER_ACTIVE), (stack, world, entity) -> hasLink(stack) ? 1F : 0F);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();

        BlockPos pos = context.getPos();
        Block block = world.getBlockState(pos).getBlock();
        TileEntity tile = world.getTileEntity(pos);
        PlayerEntity player = context.getPlayer();

        if (player == null || !(block instanceof PortalBlock) || !(tile instanceof PortalTileEntity)) {
            return ActionResultType.FAIL; // Because we do nothing
        }

        if (((PortalTileEntity) tile).isLinked()) {
            PlayerUtil.tellPlayer(player, PortalLinkResult.ALREADY_LINKED_ERROR.getUnlocalizedMessage());
            return ActionResultType.FAIL;
        }

        ItemStack heldItem = player.getHeldItem(context.getHand());

        BlockPos linkPosition = getLink(heldItem);
        if (linkPosition == null) {
            if (hasLink(heldItem)) {
                removeLink(heldItem);
                PlayerUtil.tellPlayer(player, "Something broke, clearing stored position.");
                return ActionResultType.SUCCESS; // Because we did something (remove the link)
            }
            setLink(heldItem, pos);
            PlayerUtil.tellPlayer(player, String.format("Set the stored position to %s", VectorUtils.convertToCoordinate(pos)));
            return ActionResultType.SUCCESS; // Because we did something (set the link)
        }

        TileEntity portalTile = world.getTileEntity(linkPosition);
        if (!(portalTile instanceof PortalTileEntity)) {
            PlayerUtil.tellPlayer(player, PortalLinkResult.MISSING_DESTINATION_ERROR.getUnlocalizedMessage());
            removeLink(heldItem);
            return ActionResultType.SUCCESS; // Because we did something (remove the link)
        }

        if (((PortalTileEntity) portalTile).isLinked()) {
            PlayerUtil.tellPlayer(player, PortalLinkResult.ALREADY_LINKED_ERROR.getUnlocalizedMessage());
            removeLink(heldItem);
            return ActionResultType.SUCCESS; // Because we did something (remove the link)
        }

        PortalLinkResult portalLinkResult = ((PortalTileEntity) portalTile).linkPortal(((PortalTileEntity) tile));
        PlayerUtil.tellPlayer(player, portalLinkResult.getUnlocalizedMessage());
        removeLink(heldItem);
        return ActionResultType.SUCCESS; // Because we did something (remove the link)
    }


    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ITextComponent normal = super.getDisplayName(stack);
        if (hasLink(stack)) {
            return new StringTextComponent(String.format("%s %s", normal.getString(), VectorUtils.convertToCoordinate(getLink(stack))));
        }
        return normal;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            removeLink(player.getHeldItem(hand));
            PlayerUtil.tellPlayer(player, "Stored position has been cleared.");
        }
        return super.onItemRightClick(world, player, hand);
    }

    private static boolean hasLink(ItemStack stack) {
        return stack.getOrCreateChildTag(PORTAL_LINKER).contains("pos");
    }

    private static BlockPos getLink(ItemStack stack) {
        if (!hasLink(stack)) {
            return null;
        }
        return NBTUtil.readBlockPos(stack.getOrCreateChildTag(PORTAL_LINKER).getCompound("pos"));
    }

    private static void setLink(ItemStack stack, BlockPos pos) {
        stack.getOrCreateChildTag(PORTAL_LINKER).put("pos", NBTUtil.writeBlockPos(pos));
    }

    private static void removeLink(ItemStack stack) {
        stack.getOrCreateChildTag(PORTAL_LINKER).remove("pos");
    }
}
