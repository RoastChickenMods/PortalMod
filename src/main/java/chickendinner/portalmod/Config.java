package chickendinner.portalmod;


import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Server {
        public ForgeConfigSpec.IntValue rfPerTick;
        public ForgeConfigSpec.IntValue solidFuelGeneratorCapacity;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("rf costs");
            rfPerTick = builder.comment("rf per tick for the thing")
                    .defineInRange("idklol", 5, 0, 10);

            solidFuelGeneratorCapacity = builder.comment("The amount of FE that can be store in the solid fuel generator")
                    .defineInRange("solid_fuel_generator_capacity", 32000, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }
}
