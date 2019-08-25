package chickendinner.portalmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PortalMod.ID)
public class PortalMod {
    public static final String ID = "portalmod";
    public static final String NAME = "Portal Mod";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public PortalMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loading");
    }
}
