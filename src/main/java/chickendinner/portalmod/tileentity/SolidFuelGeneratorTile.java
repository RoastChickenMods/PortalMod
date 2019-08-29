package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.config.Config;
import chickendinner.portalmod.tileentity.base.MachineTile;
import chickendinner.portalmod.tileentity.energy.WritableEnergyStorage;
import chickendinner.portalmod.tileentity.module.EnergyModule;
import chickendinner.portalmod.tileentity.module.IModule;
import chickendinner.portalmod.tileentity.module.ItemModule;
import chickendinner.portalmod.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Set;

import static chickendinner.portalmod.block.SolidFuelGeneratorBlock.STATE;
import static chickendinner.portalmod.block.SolidFuelGeneratorBlock.State.BURNING;
import static chickendinner.portalmod.block.SolidFuelGeneratorBlock.State.IDLE;

public class SolidFuelGeneratorTile extends MachineTile implements ITickableTileEntity {
    private WritableEnergyStorage energyStorage;
    private ItemStackHandler itemStackHandler;

    public SolidFuelGeneratorTile() {
        super(PortalMod.Tiles.SOLID_FUEL_GENERATOR);
        energyStorage = new WritableEnergyStorage(Config.SERVER.solidFuelGeneratorCapacity.get());
        itemStackHandler = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0)
                    return Util.getBurnTime(stack) > 0;
                return super.isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }

    @Override
    protected void addModules(Set<IModule> moduleList) {
        moduleList.add(new EnergyModule(energyStorage));
        moduleList.add(new ItemModule(itemStackHandler));
    }

    @Override
    public CompoundNBT getTileData() {
        return write(super.getTileData());
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
            setWorkLeft(Util.getBurnTime(fuel));
            getWorld().setBlockState(pos, getBlockState().with(STATE, BURNING), 3);
        }
    }

    private boolean hasFuel() {
        return Util.getBurnTime(itemStackHandler.getStackInSlot(0)) > 0;
    }

    private void consumeFuel() {
        decrementWorkLeft();
        if (getWorkLeft() == 0) {
            getWorld().setBlockState(pos, getBlockState().with(STATE, IDLE), 3);
        }
    }

    private void generatePower() {
        this.energyStorage.setEnergyStored(this.energyStorage.getEnergyStored() + 20);
    }
}
