package chickendinner.portalmod.setup;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.event.CommonEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * This event handler manages the common setup for
 */
public enum CommonSetup {
    INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Most non-specific mod setup will be performed here. Note that this is a parallel dispatched event - you cannot
     * interact with game state in this event.
     *
     * @param event The FMLCommonSetupEvent passed to us by fml.
     */
    @SubscribeEvent
    public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info(PortalMod.DEBUG_LOG, "Loading {}", PortalMod.NAME);
        MinecraftForge.EVENT_BUS.register(CommonEventHandler.INSTANCE);
    }
}
