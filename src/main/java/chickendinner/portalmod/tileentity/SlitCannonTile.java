package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.config.SlitCannonConfig;
import chickendinner.portalmod.tileentity.energy.AdvancedEnergyStorage;
import chickendinner.portalmod.tileentity.module.EnergyModule;
import chickendinner.portalmod.tileentity.module.IModule;
import chickendinner.portalmod.tileentity.module.ItemModule;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Set;

import static net.minecraft.util.Direction.DOWN;

public class SlitCannonTile extends MachineTile {
    private static final SlitCannonConfig CONFIG = SlitCannonConfig.INSTANCE;
    // These can't be initialized in the constructor otherwise they will not be initialized before the modules are created
    private final AdvancedEnergyStorage energyStorage = new AdvancedEnergyStorage(CONFIG.getFeCapacity(), CONFIG.getFeInputPerTick(), 0);
    private final ItemStackHandler itemStorage = new ItemStackHandler(1) {
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

    public SlitCannonTile() {
        super(PortalMod.Tiles.SLIT_CANNON.get());
    }

    @Override
    protected void addModules(Set<IModule> modules) {
        // Power can only be taken from the bottom
        modules.add(new EnergyModule(DOWN::equals, () -> energyStorage));
        // Items can only be inserted by hand
        modules.add(new ItemModule(d -> getFacing() == d, () -> itemStorage));
    }

    public void firePearl() {
        if (hasRequiredPower() && containsItem()) {
            // DO THE THING
        }
    }

    public boolean containsItem() { //TODO: replace with a tag.
        return itemStorage.getStackInSlot(0).getItem() == Items.ENDER_EYE;
    }

    private boolean hasRequiredPower() {
        return energyStorage.getEnergyStored() >= CONFIG.getFePerLaunch();
    }
}
