package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.tileentity.base.MachineTile;
import chickendinner.portalmod.tileentity.module.IModule;

import java.util.Set;

public class SlitCannonTile extends MachineTile {
    public SlitCannonTile() {
        super(PortalMod.Tiles.SLIT_CANNON);
    }

    @Override
    protected void addModules(Set<IModule> modules) {

    }
}
