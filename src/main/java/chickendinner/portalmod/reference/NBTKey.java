package chickendinner.portalmod.reference;

import chickendinner.portalmod.tileentity.energy.AdvancedEnergyStorage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class NBTKey<T, NBT extends INBT> {
    public static final NBTKey<AdvancedEnergyStorage, IntNBT> ENERGY = new NBTKey<>("energy", storage -> new IntNBT(storage.getEnergyStored()), (storage, nbt) -> storage.setEnergyStored(nbt.getInt()));
    public static final NBTKey<ItemStackHandler, CompoundNBT> ITEM = new NBTKey<>("storedItems", ItemStackHandler::serializeNBT, ItemStackHandler::deserializeNBT);
    private final String key;
    private final Function<T, NBT> mappingFunction;
    private final BiConsumer<T, NBT> unmappingFunction;

    private NBTKey(String key, Function<T, NBT> mappingFunction, BiConsumer<T, NBT> unmappingFunction) {
        this.key = key;
        this.mappingFunction = mappingFunction;
        this.unmappingFunction = unmappingFunction;
    }

    public void write(CompoundNBT nbt, T thing) {
        nbt.put(key, mappingFunction.apply(thing));
    }

    public void read(CompoundNBT nbt, T thing) {
        unmappingFunction.accept(thing, (NBT) nbt.get(key));
    }
}
