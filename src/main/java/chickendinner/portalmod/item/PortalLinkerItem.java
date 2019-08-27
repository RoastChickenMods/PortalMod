package chickendinner.portalmod.item;

import chickendinner.portalmod.block.PortalBlock;
import chickendinner.portalmod.registry.Names;
import chickendinner.portalmod.tileentity.PortalTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class PortalLinkerItem extends Item {

    public static final String NBT_TAG = Names.PORTAL_LINKER;

    public PortalLinkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();

        BlockPos dstPos = context.getPos();
        Block dstBlock = world.getBlockState(dstPos).getBlock();
        TileEntity dstTile = world.getTileEntity(dstPos);
        PlayerEntity player = context.getPlayer();

        if (player == null || !(dstBlock instanceof PortalBlock) || !(dstTile instanceof PortalTileEntity)) {
            return ActionResultType.FAIL;
        }

        ItemStack itemStack = player.getHeldItem(context.getHand());
        CompoundNBT nbt = itemStack.getOrCreateChildTag(NBT_TAG);

        if (nbt.contains("pos")) {
            BlockPos srcPos = NBTUtil.readBlockPos(nbt.getCompound("pos"));
            TileEntity srcTile = world.getTileEntity(srcPos);

            if (!(srcTile instanceof PortalTileEntity)) {
                player.sendStatusMessage(new StringTextComponent("Source portal was not found"), true);
                clearNBT(itemStack);
                return ActionResultType.FAIL;
            }

            if (!((PortalTileEntity) dstTile).linkPortal((PortalTileEntity) srcTile)) {
                player.sendStatusMessage(new StringTextComponent("Could not link portal"), true);
            } else {
                player.sendStatusMessage(new StringTextComponent("Portal linked"), true);
            }
            clearNBT(player.getHeldItem(context.getHand()));
        } else {
            player.sendStatusMessage(new StringTextComponent(String.format("Stored portal at %s", dstPos.toString())), true);
            nbt.put("pos", NBTUtil.writeBlockPos(dstPos));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateChildTag(NBT_TAG);
        if (tag.contains("pos")) {
            BlockPos blockPos = NBTUtil.readBlockPos(tag);
            return new StringTextComponent(String.format("%s (%d,%d,%d)", super.getDisplayName(stack).getString(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        }
        return super.getDisplayName(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            clearNBT(player.getHeldItem(hand));

            player.sendStatusMessage(new StringTextComponent(String.format("Portal link status reset")), true);
        }

        return super.onItemRightClick(world, player, hand);
    }

    private void clearNBT(ItemStack itemStack) {
        itemStack.removeChildTag(NBT_TAG);
    }
}
