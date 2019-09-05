package chickendinner.portalmod.setup;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.event.ClientEventHandler;
import chickendinner.portalmod.ter.PortalTileEntityRenderer;
import chickendinner.portalmod.tileentity.PortalTileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static chickendinner.portalmod.PortalMod.DEBUG_LOG;

/**
 * Client side setup stuff
 */
public enum ClientSetup {
    INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Do client only setup with this event, such as KeyBindings.
     *
     * @param event The FMLClientSetupEvent passed to the mod by fml.
     */
    @SubscribeEvent
    public void onFMLClientSetup(final FMLClientSetupEvent event) {
        LOGGER.info(DEBUG_LOG, "Loading {} Client Stuff", PortalMod.NAME);
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(PortalTileEntity.class, new PortalTileEntityRenderer());
    }
}
