package chickendinner.portalmod.tileentity.base;

import chickendinner.portalmod.tileentity.module.IModule;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public abstract class MachineTile extends TileEntity {
    public static final String NBT_KEY_WORK_LEFT = "workLeft";
    private Direction facing;
    private Set<IModule> modules = new HashSet<>();
    private int workLeft = -1;

    public MachineTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        addModules(modules);
    }

    protected abstract void addModules(Set<IModule> modules);

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        modules.forEach(IModule::invalidateCap);
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        facing = null;
    }

    public Direction getFacing() {
        if (facing == null) {
            facing = getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
        }
        return facing;
    }

    public int getWorkLeft() {
        return workLeft;
    }

    public void setWorkLeft(int workLeft) {
        this.workLeft = workLeft;
    }

    public void decrementWorkLeft() {
        workLeft--;
    }

    @Override
    public CompoundNBT write(final CompoundNBT nbt) {
        modules.forEach(module -> nbt.put(module.getName(), module.serializeNBT()));
        nbt.putInt(NBT_KEY_WORK_LEFT, workLeft);
        return super.write(nbt);
    }

    @Override
    public void read(final CompoundNBT nbt) {
        modules.forEach(module -> module.deserializeNBT(nbt.getCompound(module.getName())));
        workLeft = nbt.getInt(NBT_KEY_WORK_LEFT);
        super.read(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        for (IModule module : modules) {
            LazyOptional<?> lazyOptional = module.getCapability(cap, side);
            if (lazyOptional.isPresent()) {
                return lazyOptional.cast();
            }
        }
        return super.getCapability(cap, side);
    }
}
