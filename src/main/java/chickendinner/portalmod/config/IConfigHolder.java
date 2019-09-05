package chickendinner.portalmod.config;

import chickendinner.portalmod.PortalMod;
import net.minecraftforge.common.ForgeConfigSpec;

public interface IConfigHolder {
    default boolean hasServer() {
        return false;
    }

    default void writeServer(ForgeConfigSpec.Builder builder) {
    }

    default boolean hasCommon() {
        return false;
    }

    default void writeCommon(ForgeConfigSpec.Builder builder) {
    }

    default boolean hasClient() {
        return false;
    }

    default void writeClient(ForgeConfigSpec.Builder builder) {
    }

    String getName();

    default String translationKey(String key) {
        return String.format("config.%s.%s.%s", PortalMod.ID, getName(), key);
    }
}
