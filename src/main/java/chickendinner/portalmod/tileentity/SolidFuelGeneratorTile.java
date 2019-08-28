package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.Config;
import chickendinner.portalmod.energy.WritableEnergyStorage;
import chickendinner.portalmod.reference.ModTileTypes;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static chickendinner.portalmod.block.SolidFuelGeneratorBlock.STATE;
import static chickendinner.portalmod.block.SolidFuelGeneratorBlock.State.BURNING;
import static chickendinner.portalmod.block.SolidFuelGeneratorBlock.State.IDLE;

public class SolidFuelGeneratorTile extends TileEntity implements ITickableTileEntity {
    // Internal Battery
    private WritableEnergyStorage energyStorage = new WritableEnergyStorage(Config.SERVER.solidFuelGeneratorCapacity.get());
    private LazyOptional<IEnergyStorage> energyOptional = LazyOptional.empty();

    // Solid Fuel Slot
    private ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 0)
                return getBurnTime(stack) > 0;
            return super.isItemValid(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    private int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int burnTime = ForgeEventFactory.getItemBurnTime(stack, stack.getBurnTime());
        if (burnTime == -1) {
            Integer vanillaBurnTime = AbstractFurnaceTileEntity.getBurnTimes().get(stack.getItem());
            burnTime = vanillaBurnTime == null ? 0 : vanillaBurnTime;
        }
        return burnTime;
    }

    private LazyOptional<ItemStackHandler> itemOptional = LazyOptional.empty();

    private int workLeft;

    public SolidFuelGeneratorTile() {
        super(ModTileTypes.SOLID_FUEL_GENERATOR);
    }

    @Override
    public CompoundNBT getTileData() {
        return write(new CompoundNBT());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT write = super.write(compound);
        NBTKey.ENERGY.write(compound, energyStorage);
        NBTKey.ITEM.write(compound, itemStackHandler);
        return write;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        NBTKey.ENERGY.read(compound, energyStorage);
        NBTKey.ITEM.read(compound, itemStackHandler);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
        itemOptional.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            if (!energyOptional.isPresent()) {
                energyOptional = LazyOptional.of(() -> energyStorage);
            }
            return energyOptional.cast();
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (!itemOptional.isPresent()) {
                itemOptional = LazyOptional.of(() -> itemStackHandler);
            }
            return itemOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        switch (state.get(STATE)) {
            case BURNING:
                generatePower();
                consumeFuel();
                break;
            case IDLE:
                checkForFuel();
                break;
        }
    }

    private void checkForFuel() {
        if (hasFuel()) {
            ItemStack fuel = itemStackHandler.extractItem(0, 1, false);
            this.workLeft = getBurnTime(fuel);
            getWorld().setBlockState(pos, getBlockState().with(STATE, BURNING), 3);
        }
    }

    private boolean hasFuel() {
        return getBurnTime(itemStackHandler.getStackInSlot(0)) > 0;
    }

    private void consumeFuel() {
        this.workLeft--;
        if (this.workLeft == 0) {
            getWorld().setBlockState(pos, getBlockState().with(STATE, IDLE), 3);
        }
    }

    private void generatePower() {
        this.energyStorage.setEnergyStored(this.energyStorage.getEnergyStored() + 20);
    }
}
