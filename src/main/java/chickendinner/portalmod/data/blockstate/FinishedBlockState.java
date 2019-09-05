package chickendinner.portalmod.data.blockstate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Rotations;

public class FinishedBlockState implements IFinishedBlockState {
    public static final Rotations DEFAULT_ROTATIONS = new Rotations(0, 0, 0);
    private static final DefaultBlockStateModelProvider DEFAULT_MODEL_PROVIDER = new DefaultBlockStateModelProvider();
    private final Block block;
    private final IBlockStateModelProvider modelProvider;

    public FinishedBlockState(Block block) {
        this(block, DEFAULT_MODEL_PROVIDER);
    }

    public FinishedBlockState(Block block, IBlockStateModelProvider modelProvider) {
        this.block = block;
        this.modelProvider = modelProvider;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public IBlockStateModelProvider getModelProvider() {
        return modelProvider;
    }

    @Override
    public Rotations getRotation(BlockState state) {
        return DEFAULT_ROTATIONS;
    }
}
