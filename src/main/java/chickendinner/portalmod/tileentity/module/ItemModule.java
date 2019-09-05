package chickendinner.portalmod.tileentity.module;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ItemModule implements IModule<IItemHandler> {
    private final Predicate<Direction> validDirections;
    private final Supplier<ItemStackHandler> itemHandler;
    private LazyOptional<IItemHandler> lazyOptional;

    public ItemModule(Supplier<ItemStackHandler> itemHandler) {
        this(ALL_DIRECTIONS, itemHandler);
    }

    public ItemModule(Predicate<Direction> validDirections, Supplier<ItemStackHandler> itemHandler) {
        this.validDirections = validDirections;
        this.itemHandler = itemHandler;
        this.lazyOptional = LazyOptional.of(this::getStored);
    }

    @Override
    public Capability<IItemHandler> getCapaiblityType() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public IItemHandler getStored() {
        return this.itemHandler.get();
    }

    @Override
    public LazyOptional<IItemHandler> getLazyOptional() {
        return lazyOptional;
    }

    @Override
    public void setLazyOptional(LazyOptional<IItemHandler> lazyOptional) {
        this.lazyOptional = lazyOptional;
    }

    @Override
    public Predicate<Direction> isValidDirection() {
        return validDirections;
    }

    @Override
    public String getName() {
        return "item";
    }

    @Override
    public CompoundNBT serializeNBT() {
        return itemHandler.get().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        itemHandler.get().deserializeNBT(nbt);
    }
}
