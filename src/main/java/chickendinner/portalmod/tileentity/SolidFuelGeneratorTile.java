package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.config.SolidFuelGeneratorConfig;
import chickendinner.portalmod.tileentity.base.MachineTile;
import chickendinner.portalmod.tileentity.energy.AdvancedEnergyStorage;
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
    private static final SolidFuelGeneratorConfig CONFIG = SolidFuelGeneratorConfig.INSTANCE;
    private AdvancedEnergyStorage energyStorage;
    private ItemStackHandler itemStackHandler;

    public SolidFuelGeneratorTile() {
        super(PortalMod.Tiles.SOLID_FUEL_GENERATOR);
        energyStorage = new AdvancedEnergyStorage(CONFIG.getFeCapacity(), 0, CONFIG.getFeOutputPerTick());
        itemStackHandler = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0)
                    return Util.getBurnTime(stack) > 0;
                return super.isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                SolidFuelGeneratorTile.this.markDirty();
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
        return this.write(super.getTileData());
    }

    @Override
    public void tick() {
        BlockState state = this.getBlockState();
        switch (state.get(STATE)) {
            case BURNING:
                this.generatePower();
                this.consumeFuel();
                break;
            case IDLE:
                this.checkForFuel();
                break;
        }
    }

    private void checkForFuel() {
        if (this.hasFuel()) {
            ItemStack fuel = this.itemStackHandler.extractItem(0, 1, false);
            this.setWorkLeft((int) Math.floor(Util.getBurnTime(fuel) * CONFIG.getBurnTimeMultiplier()));
            this.getWorld().setBlockState(this.getPos(), this.getBlockState().with(STATE, BURNING), 3);
        }
    }

    private boolean hasFuel() {
        return Util.getBurnTime(itemStackHandler.getStackInSlot(0)) > 0;
    }

    private void consumeFuel() {
        decrementWorkLeft();
        if (this.getWorkLeft() == 0) {
            this.getWorld().setBlockState(this.getPos(), this.getBlockState().with(STATE, IDLE), 3);
        }
    }

    private void generatePower() {
        this.energyStorage.supply(CONFIG.getFePerBurnTime());
    }
}
