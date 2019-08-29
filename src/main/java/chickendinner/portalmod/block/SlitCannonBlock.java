package chickendinner.portalmod.block;

import chickendinner.portalmod.block.base.HorizontalFacingBlock;
import chickendinner.portalmod.tileentity.SlitCannonTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SlitCannonBlock extends HorizontalFacingBlock {
    public SlitCannonBlock(Properties properties) {
        super(properties);
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
