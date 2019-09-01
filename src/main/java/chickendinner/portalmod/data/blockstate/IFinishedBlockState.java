package chickendinner.portalmod.data.blockstate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Rotations;

public interface IFinishedBlockState {
    Block getBlock();

    IBlockStateModelProvider getModelProvider();

    Rotations getRotation(BlockState state) ;
}
