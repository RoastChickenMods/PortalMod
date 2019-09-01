package chickendinner.portalmod.data.blockstate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.Rotations;

public class HorizontalBlockState extends FinishedBlockState {
    public HorizontalBlockState(Block block) {
        super(block);
    }

    public HorizontalBlockState(Block block, IBlockStateModelProvider modelProvider) {
        super(block, modelProvider);
    }

    @Override
    public Rotations getRotation(BlockState state) {
        return new Rotations(0, 180 + state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle(), 0);
    }
}
