package chickendinner.portalmod.data.blockstate;

import chickendinner.portalmod.data.MutableResourceLocation;
import net.minecraft.block.BlockState;

public interface IBlockStateModelProvider {
    MutableResourceLocation getModel(BlockState state);
}
