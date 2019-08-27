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
        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("rf costs");
                rfPerTick = builder.comment("rf per tick for the thing")
                        .defineInRange("idklol", 5, 0, 10);
            builder.pop();
        }
    }
}
