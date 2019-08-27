package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.registry.ModTileTypes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class DotTileEntity extends TileEntity {
    private static final String COUNTER_NBT_KEY = "counter";
    private int counter;

    public DotTileEntity() {
        super(ModTileTypes.DOT_BLOCK);
    }

    public void increment() {
        counter++;
        markDirty();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt(COUNTER_NBT_KEY, counter);
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        counter = compound.getInt(COUNTER_NBT_KEY);
        super.read(compound);
    }

    public int count() {
        return counter;
    }
}
