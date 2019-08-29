package chickendinner.portalmod.setup;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.event.CommonEventHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
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

    /**
     * Registers all blocks in the mod to {@link net.minecraftforge.registries.ForgeRegistries#BLOCKS}
     * <br>
     * NOTE: All creation of blocks is done in {@link PortalMod.Blocks#get()}
     *
     * @param event The registry event for blocks
     */
    @SubscribeEvent
    public void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        PortalMod.Blocks.get().forEach(event.getRegistry()::register);
    }

    /**
     * Registers all blocks in the mod to {@link net.minecraftforge.registries.ForgeRegistries#ITEMS}
     * <br>
     * NOTE: All creation of blocks is done in {@link PortalMod.Items#get()}
     *
     * @param event The registry event for items
     */
    @SubscribeEvent
    public void onRegisterItems(final RegistryEvent.Register<Item> event) {
        PortalMod.Items.get().forEach(event.getRegistry()::register);
    }

    /**
     * Registers all blocks in the mod to {@link net.minecraftforge.registries.ForgeRegistries#TILE_ENTITIES}
     * <br>
     * NOTE: All creation of blocks is done in {@link PortalMod.Tiles#get()}
     *
     * @param event The registry event for tile entities
     */
    @SubscribeEvent
    public void onRegisterTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        PortalMod.Tiles.get().forEach(event.getRegistry()::register);
    }
}
