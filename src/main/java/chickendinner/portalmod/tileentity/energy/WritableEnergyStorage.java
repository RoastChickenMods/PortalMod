package chickendinner.portalmod.tileentity.energy;

import net.minecraftforge.energy.EnergyStorage;

public class WritableEnergyStorage extends EnergyStorage {
    public WritableEnergyStorage(int capacity) {
        super(capacity);
    }

    public WritableEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public WritableEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public WritableEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void setEnergyStored(int amount) {
        this.energy = amount;
    }
}
