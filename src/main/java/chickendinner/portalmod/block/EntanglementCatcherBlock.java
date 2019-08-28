package chickendinner.portalmod.block;

import chickendinner.portalmod.tileentity.EntanglementCatcherTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class EntanglementCatcherBlock extends Block {
    public EntanglementCatcherBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EntanglementCatcherTile();
    }
}
