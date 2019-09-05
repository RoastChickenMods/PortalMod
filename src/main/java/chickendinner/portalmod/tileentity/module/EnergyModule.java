package chickendinner.portalmod.tileentity.module;

import chickendinner.portalmod.tileentity.energy.AdvancedEnergyStorage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class EnergyModule implements IModule<IEnergyStorage> {
    private static final String NBT_KET_ENERGY_STORED = "energyStored";
    private final Predicate<Direction> validFaceTest;
    private final Supplier<AdvancedEnergyStorage> energyStorage;
    private LazyOptional<IEnergyStorage> lazyOptional;

    public EnergyModule(Predicate<Direction> validFaceTest, Supplier<AdvancedEnergyStorage> energyStorage) {
        this.validFaceTest = validFaceTest;
        this.energyStorage = energyStorage;
        this.lazyOptional = LazyOptional.empty();
    }

    public EnergyModule(Supplier<AdvancedEnergyStorage> energyStorage) {
        this(ALL_DIRECTIONS, energyStorage);
    }

    @Override
    public Capability<IEnergyStorage> getCapaiblityType() {
        return CapabilityEnergy.ENERGY;
    }

    public IEnergyStorage getStored() {
        return energyStorage.get();
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
    public Predicate<Direction> isValidDirection() {
        return validFaceTest;
    }

    @Override
    public String getName() {
        return "energy";
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(NBT_KET_ENERGY_STORED, energyStorage.get().getEnergyStored());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        energyStorage.get().setEnergyStored(nbt.getInt(NBT_KET_ENERGY_STORED));
    }
}
