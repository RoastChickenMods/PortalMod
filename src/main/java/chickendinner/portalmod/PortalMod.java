package chickendinner.portalmod;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }



    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loading");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // register renderers/models/particles/keybindings etc
    }
}
