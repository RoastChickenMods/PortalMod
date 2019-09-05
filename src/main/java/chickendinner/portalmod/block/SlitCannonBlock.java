package chickendinner.portalmod.block;

import chickendinner.portalmod.reference.TranslatedMessage;
import chickendinner.portalmod.tileentity.SlitCannonTile;
import chickendinner.portalmod.util.PlayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * This is a block which will be part of the 'double slit' multiblock.
 * <p>
 * It's function will be to store a bunch of power, then fire an ender eye (until a better idea is thought of) into a
 * slit block.
 * <p>
 * To put an ender eye into this block, you will need to click the front face. It will only store 1 ender eye. (It may
 * require more for balance... since quantum entanglement isn't an exact science.)
 * <p>
 * To fire the slit cannon, you will need to apply a redstone signal.
 */
public class SlitCannonBlock extends HorizontalFacingBlock {
    public SlitCannonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        boolean canInteract = state.get(FACING) == hit.getFace();

        // Only do the hand swinging animation on the client if the player is clicking the front face
        if (worldIn.isRemote()) {
            return canInteract;
        }

        // Don't do anything on the server if you didn't click on the front face
        if (!canInteract) {
            return false;
        }

        // This should not happen
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof SlitCannonTile)) {
            return false;
        }
        SlitCannonTile cannonTile = (SlitCannonTile) tileEntity;
        LazyOptional<IItemHandler> inventory = cannonTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, hit.getFace());

        // Can't insert anything if it has the eye already
        if (cannonTile.containsItem()) {
            ItemStack enderEye = inventory.map(handler -> handler.extractItem(0, 1, false)).orElse(ItemStack.EMPTY);
            if (!enderEye.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, enderEye);
            }
            return false;
        }

        // The player can't do anything if they aren't holding an ender eye
        ItemStack heldItem = player.getHeldItem(handIn);
        if (heldItem.getItem() != Items.ENDER_EYE) {
            return false;
        }

        // Create a stack containing a single ender eye
        ItemStack enderEye = new ItemStack(Items.ENDER_EYE, 1);

        // Try and insert it
        ItemStack remaining = inventory.map(handler -> handler.insertItem(0, enderEye, false)).orElse(enderEye);
        if (!remaining.isEmpty()) {
            return false;
        }

        // Actually take an ender eye from the player because it has been put into the cannon
        heldItem.shrink(1);

        // Tell the player about the success
        PlayerUtil.tellPlayer(player, TranslatedMessage.CANNON_SUCCESS);
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SlitCannonTile();
    }
}
