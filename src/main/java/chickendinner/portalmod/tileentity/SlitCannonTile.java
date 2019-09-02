package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.config.SlitCannonConfig;
import chickendinner.portalmod.tileentity.energy.AdvancedEnergyStorage;
import chickendinner.portalmod.tileentity.module.EnergyModule;
import chickendinner.portalmod.tileentity.module.IModule;
import chickendinner.portalmod.tileentity.module.ItemModule;
import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Set;

public class SlitCannonTile extends MachineTile {
    private static final SlitCannonConfig CONFIG = SlitCannonConfig.INSTANCE;
    private final AdvancedEnergyStorage energyStorage;
    private final ItemStackHandler itemStorage;

    public SlitCannonTile() {
        super(PortalMod.Tiles.SLIT_CANNON);
        energyStorage = new AdvancedEnergyStorage(CONFIG.getFeCapacity(), CONFIG.getFeInputPerTick(), 0);
        itemStorage = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0) {
                    return stack.getItem() == Items.ENDER_EYE;
                }
                return super.isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }

    @Override
    protected void addModules(Set<IModule> modules) {
        // Power can only be taken from the bottom
        modules.add(new EnergyModule(ImmutableSet.of(Direction.DOWN), energyStorage));
        // Items can only be inserted by hand
        modules.add(new ItemModule(ImmutableSet.of(), itemStorage));
    }

    public void firePearl() {
        if (hasRequiredPower() && containsItem()) {
            // DO THE THING
        }
    }

    private boolean containsItem() { //TODO: replace with a tag.
        return itemStorage.getStackInSlot(0).getItem() == Items.ENDER_EYE;
    }

    private boolean hasRequiredPower() {
        return energyStorage.getEnergyStored() >= CONFIG.getFePerLaunch();
    }
}
