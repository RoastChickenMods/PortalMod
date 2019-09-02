package chickendinner.portalmod.tileentity.module;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static net.minecraft.util.Direction.*;

public interface IModule<T> extends ICapabilityProvider, IStringSerializable, INBTSerializable<CompoundNBT> {
    ImmutableSet<Direction> ALL_DIRECTIONS = ImmutableSet.of(UP, DOWN, NORTH, EAST, SOUTH, WEST);

    Capability<T> getCapaiblityType();

    T getStored();

    LazyOptional<T> getLazyOptional();

    void setLazyOptional(LazyOptional<T> lazyOptional);

    default void checkLazyOptional() {
        if (!getLazyOptional().isPresent()) {
            setLazyOptional(LazyOptional.of(this::getStored));
        }
    }

    Set<Direction> getValidDirections();

    @Nonnull
    @Override
    default <Z> LazyOptional<Z> getCapability(@Nonnull Capability<Z> cap, @Nullable Direction side) {
        if (side == null || getValidDirections().contains(side)) {
            checkLazyOptional();
            return getCapaiblityType().orEmpty(cap, getLazyOptional());
        }
        return LazyOptional.empty();
    }

    default void invalidateCap() {
        getLazyOptional().invalidate();
    }
}
