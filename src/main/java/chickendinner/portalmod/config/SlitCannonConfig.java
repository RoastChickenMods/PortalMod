package chickendinner.portalmod.config;

import chickendinner.portalmod.reference.Names;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public enum SlitCannonConfig implements IConfigHolder {
    INSTANCE;

    IntValue fePerLaunch;

    public int getFePerLaunch() {
        return fePerLaunch.get();
    }

    @Override
    public boolean hasServer() {
        return true;
    }

    @Override
    public void writeServer(ForgeConfigSpec.Builder builder) {
        fePerLaunch = builder.comment("The amount of FE required to launch an ender eye into the double slit experiment")
                .translation(translationKey("fe_per_launch"))
                .defineInRange("fe_per_launch", 10_000_000, 0, 10_000_000); // TODO: change the max to be the capacity of the energy storage
    }

    @Override
    public String getName() {
        return Names.SLIT_CANNON;
    }
}
