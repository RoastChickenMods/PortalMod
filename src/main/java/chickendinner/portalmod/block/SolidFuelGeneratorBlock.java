package chickendinner.portalmod.block;

import chickendinner.portalmod.block.base.HorizontalFacingBlock;
import chickendinner.portalmod.tileentity.SolidFuelGeneratorTile;
import chickendinner.portalmod.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public class SolidFuelGeneratorBlock extends HorizontalFacingBlock {
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);

    public SolidFuelGeneratorBlock(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(STATE, State.IDLE));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote()) {
            displayStoredEnergy(worldIn, pos, player);
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    private void displayStoredEnergy(World worldIn, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityEnergy.ENERGY)
                    .ifPresent(storage -> PlayerUtil.tellPlayer(player, String.format("Stored Energy: %d/%dFE", storage.getEnergyStored(), storage.getMaxEnergyStored())));
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder.add(STATE));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SolidFuelGeneratorTile();
    }


    public enum State implements IStringSerializable {
        BURNING("burning"),
        IDLE("idle");

        private String name;

        State(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
