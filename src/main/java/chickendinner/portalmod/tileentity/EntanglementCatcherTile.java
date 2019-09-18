package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.tileentity.module.IModule;
import chickendinner.portalmod.tileentity.module.ItemModule;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Set;

public class EntanglementCatcherTile extends MachineTile {
    private final ItemStackHandler itemStorage;

    public EntanglementCatcherTile() {
        super(PortalMod.Tiles.ENTANGLEMENT_CATCHER.get());
        itemStorage = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0) {
                    return stack.getItem() == PortalMod.Items.ENTANGLED_PAIR.get(); // TODO: use a tag
                }
                return super.isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                EntanglementCatcherTile.this.markDirty();
            }
        };
    }

    @Override
    protected void addModules(Set<IModule> modules) {
        modules.add(new ItemModule(dir -> dir == getFacing(), () -> itemStorage));
    }
}
