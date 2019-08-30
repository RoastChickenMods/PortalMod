package chickendinner.portalmod.config;

import chickendinner.portalmod.reference.Names;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public enum SolidFuelGeneratorConfig implements IConfigHolder {
    INSTANCE;

    public int getFeCapacity() {
        return feCapacity.get();
    }

    private IntValue feCapacity;

    @Override
    public boolean hasServer() {
        return true;
    }

    @Override
    public void writeServer(ForgeConfigSpec.Builder builder) {
        feCapacity = builder.comment("The FE capacity of the solid fuel generator")
                .translation(translationKey("fe_capacity"))
                .defineInRange("fe_capacity", 32000, 0, Integer.MAX_VALUE);
    }

    @Override
    public String getName() {
        return Names.SOLID_FUEL_GENERATOR;
    }
}
