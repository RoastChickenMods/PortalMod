package chickendinner.portalmod.config;

import chickendinner.portalmod.reference.Names;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public enum SlitCannonConfig implements IConfigHolder {
    INSTANCE;

    IntValue fePerLaunch;
    IntValue feCapacity;
    IntValue feInputPerTick;

    public int getFePerLaunch() {
        return fePerLaunch.get();
    }

    public int getFeCapacity() {
        return feCapacity.get();
    }

    public int getFeInputPerTick() {
        return feInputPerTick.get();
    }

    @Override
    public boolean hasServer() {
        return true;
    }

    @Override
    public void writeServer(ForgeConfigSpec.Builder builder) {
        builder.comment("Please note that the value of these config values may make the machine unusable if set to incompatible values.",
                "So make sure that the capacity is at least equal to the fe_per_launch");
        fePerLaunch = builder.comment("The amount of FE required to launch an ender eye into the double slit experiment")
                .translation(translationKey("fe_per_launch"))
                .defineInRange("fe_per_launch", 10_000_000, 0, Integer.MAX_VALUE);
        feCapacity = builder.comment("The FE Capacity of the Slit Cannon")
                .translation(translationKey("fe_capacity"))
                .defineInRange("fe_capacity", 10_000_000, 0, Integer.MAX_VALUE);
        feInputPerTick = builder.comment("The FE this block can accept from other block/items per tick.")
                .translation(translationKey("fe_input_per_tick"))
                .defineInRange("fe_input_per_tick", 10_000, 0, Integer.MAX_VALUE);
    }

    @Override
    public String getName() {
        return Names.SLIT_CANNON;
    }
}
