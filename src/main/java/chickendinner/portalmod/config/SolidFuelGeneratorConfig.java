package chickendinner.portalmod.config;

import chickendinner.portalmod.reference.Names;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public enum SolidFuelGeneratorConfig implements IConfigHolder {
    INSTANCE;

    private IntValue feCapacity;
    private IntValue feOutputPerTick;
    private IntValue fePerBurnTime;
    private DoubleValue burnTimeMultiplier;

    public int getFeCapacity() {
        return feCapacity.get();
    }

    public int getFeOutputPerTick() {
        return feOutputPerTick.get();
    }

    public int getFePerBurnTime() {
        return fePerBurnTime.get();
    }

    public double getBurnTimeMultiplier() {
        return burnTimeMultiplier.get();
    }

    @Override
    public boolean hasServer() {
        return true;
    }

    @Override
    public void writeServer(ForgeConfigSpec.Builder builder) {
        feCapacity = builder.comment("The FE capacity of the solid fuel generator.")
                .translation(translationKey("fe_capacity"))
                .defineInRange("fe_capacity", 32000, 0, Integer.MAX_VALUE);
        feOutputPerTick = builder.comment("The FE this block outputs to adjacent energy storages that accept power.")
                .translation(translationKey("fe_output_per_tick"))
                .defineInRange("fe_output_per_tick", 100, 0, Integer.MAX_VALUE);
        fePerBurnTime = builder.comment("The FE per burn time of the solid fuel.",
                "Burn time is in ticks, and for example a stack be default has a burn time of 200.")
                .translation(translationKey("fe_per_burn_time"))
                .defineInRange("fe_per_burn_time", 20, 1, Integer.MAX_VALUE);
        burnTimeMultiplier = builder.comment("The amount of burn time in the generator per burn time of the item.",
                "For example if the item's burn time is 200, and this is set to 2, it will last for 400 ticks instead.")
                .translation(translationKey("burn_time_multiplier"))
                .defineInRange("burn_time_multiplier", 1D, 1e-8D, Double.MAX_VALUE);
    }

    @Override
    public String getName() {
        return Names.SOLID_FUEL_GENERATOR;
    }
}
