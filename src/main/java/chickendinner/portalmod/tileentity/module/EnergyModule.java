package chickendinner.portalmod.tileentity.module;

import chickendinner.portalmod.tileentity.energy.WritableEnergyStorage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Set;

public class EnergyModule implements IModule<IEnergyStorage> {
    private static final String NBT_KET_ENERGY_STORED = "energyStored";
    private final Set<Direction> validDirections;
    private final WritableEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyOptional;

    public EnergyModule(Set<Direction> validDirections, WritableEnergyStorage energyStorage) {
        this.validDirections = validDirections;
        this.energyStorage = energyStorage;
        this.lazyOptional = LazyOptional.of(this::getStored);
    }

    public EnergyModule(WritableEnergyStorage energyStorage) {
        this(ALL_DIRECTIONS, energyStorage);
    }

    @Override
    public Capability<IEnergyStorage> getCapaiblityType() {
        return CapabilityEnergy.ENERGY;
    }

    public IEnergyStorage getStored() {
        return energyStorage;
    }

    @Override
    public LazyOptional<IEnergyStorage> getLazyOptional() {
        return lazyOptional;
    }

    @Override
    public void setLazyOptional(LazyOptional<IEnergyStorage> lazyOptional) {
        this.lazyOptional = lazyOptional;
    }

    @Override
    public Set<Direction> getValidDirections() {
        return validDirections;
    }

    @Override
    public String getName() {
        return "energy";
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(NBT_KET_ENERGY_STORED, energyStorage.getEnergyStored());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        energyStorage.setEnergyStored(nbt.getInt(NBT_KET_ENERGY_STORED));
    }
}
