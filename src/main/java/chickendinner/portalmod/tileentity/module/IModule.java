package chickendinner.portalmod.tileentity.module;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface IModule<T> extends ICapabilityProvider, IStringSerializable, INBTSerializable<CompoundNBT> {
    Predicate<Direction> ALL_DIRECTIONS = d -> true;

    Capability<T> getCapaiblityType();

    T getStored();

    LazyOptional<T> getLazyOptional();

    default void checkLazyOptional() {
        if (!getLazyOptional().isPresent()) {
            setLazyOptional(LazyOptional.of(this::getStored));
        }
    }

    void setLazyOptional(LazyOptional<T> lazyOptional);

    Predicate<Direction> isValidDirection();

    @Nonnull
    @Override
    default <Z> LazyOptional<Z> getCapability(@Nonnull Capability<Z> cap, @Nullable Direction side) {
        if (side == null || isValidDirection().test(side)) {
            checkLazyOptional();
            return getCapaiblityType().orEmpty(cap, getLazyOptional());
        }
        return LazyOptional.empty();
    }

    default void invalidateCap() {
        getLazyOptional().invalidate();
    }
}
