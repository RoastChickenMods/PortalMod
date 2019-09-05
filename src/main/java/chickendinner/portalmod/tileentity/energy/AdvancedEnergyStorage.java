package chickendinner.portalmod.tileentity.energy;

import net.minecraftforge.energy.EnergyStorage;

public class AdvancedEnergyStorage extends EnergyStorage {
    public AdvancedEnergyStorage(int capacity) {
        super(capacity);
    }

    public AdvancedEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public AdvancedEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public AdvancedEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void setEnergyStored(int amount) {
        this.energy = amount;
    }

    public void consume(int amount) {
        this.energy -= amount;
    }

    public void supply(int amount) {
        this.energy += amount;
    }
}
