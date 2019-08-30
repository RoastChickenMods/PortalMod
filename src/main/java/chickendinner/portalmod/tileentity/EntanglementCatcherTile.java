package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.tileentity.base.MachineTile;
import chickendinner.portalmod.tileentity.module.EnergyModule;
import chickendinner.portalmod.tileentity.module.IModule;
import com.google.common.collect.ImmutableSet;
import net.minecraft.tileentity.TileEntity;

import java.util.Set;

public class EntanglementCatcherTile extends MachineTile {
    public EntanglementCatcherTile() {
        super(PortalMod.Tiles.ENTANGLEMENT_CATCHER);
    }

    @Override
    protected void addModules(Set<IModule> modules) {

    }
}
