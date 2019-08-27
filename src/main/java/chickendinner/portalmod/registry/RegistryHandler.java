package chickendinner.portalmod.registry;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.block.PortalBlock;
import chickendinner.portalmod.group.PortalModGroup;
import chickendinner.portalmod.item.PortalLinkBreakerItem;
import chickendinner.portalmod.item.PortalLinkerItem;
import chickendinner.portalmod.tileentity.PortalTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryHandler {

    public static final Item.Properties DEFAULT_ITEM_PROPERTIES = new Item.Properties().group(PortalModGroup.INSTANCE);
    private static final Map<String, Block> intermediateBlockMap = new HashMap<>();
    private static final Map<String, Item> intermediateItemMap = new HashMap<>();
    private static final Map<Block, Supplier<TileEntity>> intermediateTileMap = new HashMap<>();

    private RegistryHandler() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerAll(final RegistryEvent.Register<Block> event) {
        RegistryHandler.addBlock(Names.PORTAL, new PortalBlock(Block.Properties.from(Blocks.OBSIDIAN)), PortalTileEntity::new);
        addItem(Names.PORTAL_LINKER, new PortalLinkerItem(DEFAULT_ITEM_PROPERTIES.maxStackSize(1)));
        addItem(Names.PORTAL_LINK_BREAKER, new PortalLinkBreakerItem(DEFAULT_ITEM_PROPERTIES.maxStackSize(1)));
    }

    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        intermediateBlockMap.forEach((name, block) -> event.getRegistry().register(block.setRegistryName(rl(name))));
        intermediateBlockMap.clear();
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        intermediateItemMap.forEach((name, item) -> event.getRegistry().register(item.setRegistryName(rl(name))));
        intermediateItemMap.clear();
    }

    @SubscribeEvent
    public static void onRegisterTileEntityTypes(final RegistryEvent.Register<TileEntityType<?>> event) {
        intermediateTileMap.forEach((block, supplier) -> event.getRegistry().register(TileEntityType.Builder.create(supplier, block).build(null).setRegistryName(block.getRegistryName())));
        intermediateTileMap.clear();
    }

    private static void addBlock(String name, Block block, Supplier<TileEntity> tileSupplier) {
        addBlock(name, block, new BlockNamedItem(block, DEFAULT_ITEM_PROPERTIES), tileSupplier);
    }

    private static void addBlock(String name, Block block, BlockItem item, Supplier<TileEntity> tileSupplier) {
        addBlock(name, block, item);
        intermediateTileMap.put(block, tileSupplier);
    }

    private static void addBlock(String name, Block block) {
        addBlock(name, block, new BlockNamedItem(block, DEFAULT_ITEM_PROPERTIES));
    }

    private static void addBlock(String name, Block block, BlockItem item) {
        addItem(name, item);
        intermediateBlockMap.put(name, block);
    }

    private static void addItem(String name, Item item) {
        intermediateItemMap.put(name, item);
    }

    private static ResourceLocation rl(String name) {
        return new ResourceLocation(PortalMod.ID, name);
    }
}