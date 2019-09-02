package chickendinner.portalmod.config;


import com.google.common.collect.ImmutableSet;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class Config {
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;
    private static final Set<IConfigHolder> CONFIG_HOLDERS;

    static {
        CONFIG_HOLDERS = ImmutableSet.of(
                SolidFuelGeneratorConfig.INSTANCE,
                SlitCannonConfig.INSTANCE
        );
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Server {
        public Server(final ForgeConfigSpec.Builder builder) {
            CONFIG_HOLDERS.stream()
                    .filter(IConfigHolder::hasServer)
                    .forEach(holder -> {
                        builder.push(holder.getName());
                        holder.writeServer(builder);
                        builder.pop();
                    });
        }
    }
}
