package chickendinner.portalmod.data.blockstate;

import chickendinner.portalmod.data.MutableResourceLocation;
import net.minecraft.block.BlockState;

public class DefaultBlockStateModelProvider implements IBlockStateModelProvider {
    @Override
    public MutableResourceLocation getModel(BlockState state) {
        return new MutableResourceLocation(state.getBlock().getRegistryName());
    }
}
