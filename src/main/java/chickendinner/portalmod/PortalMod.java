package chickendinner.portalmod;

import chickendinner.portalmod.block.*;
import chickendinner.portalmod.config.Config;
import chickendinner.portalmod.group.PortalModGroup;
import chickendinner.portalmod.item.EntangledPairItem;
import chickendinner.portalmod.item.PortalLinkBreakerItem;
import chickendinner.portalmod.item.PortalLinkerItem;
import chickendinner.portalmod.reference.Names;
import chickendinner.portalmod.setup.ClientSetup;
import chickendinner.portalmod.setup.CommonSetup;
import chickendinner.portalmod.tileentity.EntanglementCatcherTile;
import chickendinner.portalmod.tileentity.PortalTileEntity;
import chickendinner.portalmod.tileentity.SlitCannonTile;
import chickendinner.portalmod.tileentity.SolidFuelGeneratorTile;
import chickendinner.portalmod.tileentity.type.ModTileType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Main mod class.
 */
@Mod(PortalMod.ID)
public class PortalMod {
    public static final String ID = "portalmod";
    public static final String NAME = "Portal Mod";
    public static final Marker DEBUG_LOG = MarkerManager.getMarker("PORTALMOD");

    /**
     * Registers the registry common and client setup event handlers to the mod
     * event bus. <br>
     * Registers the config to the mod loading context.
     */
    public PortalMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(CommonSetup.INSTANCE);
        modEventBus.register(ClientSetup.INSTANCE);
        Blocks.REGISTRY.register(modEventBus);
        Items.REGISTRY.register(modEventBus);
        Tiles.REGISTRY.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }

    public static class Blocks {
        private static final DeferredRegister<Block> REGISTRY = new DeferredRegister<>(ForgeRegistries.BLOCKS, ID);
        private static final Block.Properties MACHINE_PROPS = Block.Properties.create(Material.IRON, MaterialColor.LIGHT_GRAY).hardnessAndResistance(5, 15).harvestLevel(1).harvestTool(ToolType.PICKAXE); // TODO: maybe make this a wrench

        public static final RegistryObject<PortalBlock> PORTAL = REGISTRY.register(Names.PORTAL, () -> new PortalBlock(MACHINE_PROPS));
        public static final RegistryObject<SolidFuelGeneratorBlock> SOLID_FUEL_GENERATOR = REGISTRY.register(Names.SOLID_FUEL_GENERATOR, () -> new SolidFuelGeneratorBlock(MACHINE_PROPS));
        public static final RegistryObject<Block> ENTANGLEMENT_CATCHER = REGISTRY.register(Names.ENTANGLEMENT_CATCHER, () -> new Block(MACHINE_PROPS));
        public static final RegistryObject<HorizontalFacingBlock> SLIT_CANNON = REGISTRY.register(Names.SLIT_CANNON, () -> new HorizontalFacingBlock(MACHINE_PROPS));
        public static final RegistryObject<SlitCannonBlock> SLIT_BLOCK = REGISTRY.register(Names.SLIT_BLOCK, () -> new SlitCannonBlock(MACHINE_PROPS));
        public static final RegistryObject<EntanglementCatcherBlock> MACHINE_BASE = REGISTRY.register(Names.MACHINE_BASE, () -> new EntanglementCatcherBlock(MACHINE_PROPS));
    }

    public static class Items {
        private static final DeferredRegister<Item> REGISTRY = new DeferredRegister<>(ForgeRegistries.ITEMS, ID);
        private static final Item.Properties DEFAULT_PROPS = new Item.Properties().group(PortalModGroup.INSTANCE);

        public static final RegistryObject<BlockItem> PORTAL = REGISTRY.register(Names.PORTAL, () -> new BlockItem(Blocks.PORTAL.get(), DEFAULT_PROPS));
        public static final RegistryObject<BlockItem> SOLID_FUEL_GENERATOR = REGISTRY.register(Names.SOLID_FUEL_GENERATOR, () -> new BlockItem(Blocks.SOLID_FUEL_GENERATOR.get(), DEFAULT_PROPS.maxStackSize(1)));
        public static final RegistryObject<BlockItem> SLIT_CANNON = REGISTRY.register(Names.SLIT_CANNON, () -> new BlockItem(Blocks.SLIT_CANNON.get(), DEFAULT_PROPS.maxStackSize(1)));
        public static final RegistryObject<BlockItem> SLIT_BLOCK = REGISTRY.register(Names.SLIT_BLOCK, () -> new BlockItem(Blocks.SLIT_BLOCK.get(), DEFAULT_PROPS));
        public static final RegistryObject<BlockItem> ENTANGLEMENT_CATCHER = REGISTRY.register(Names.ENTANGLEMENT_CATCHER, () -> new BlockItem(Blocks.ENTANGLEMENT_CATCHER.get(), DEFAULT_PROPS.maxStackSize(1)));
        public static final RegistryObject<BlockItem> MACHINE_BASE = REGISTRY.register(Names.MACHINE_BASE, () -> new BlockItem(Blocks.MACHINE_BASE.get(), DEFAULT_PROPS.maxStackSize(1)));

        public static final RegistryObject<PortalLinkerItem> PORTAL_LINKER = REGISTRY.register(Names.PORTAL_LINKER, () -> new PortalLinkerItem(DEFAULT_PROPS.maxStackSize(1)));
        public static final RegistryObject<PortalLinkBreakerItem> PORTAL_LINK_BREAKER = REGISTRY.register(Names.PORTAL_LINK_BREAKER, () -> new PortalLinkBreakerItem(DEFAULT_PROPS.maxStackSize(1)));
        public static final RegistryObject<EntangledPairItem> ENTANGLED_PAIR = REGISTRY.register(Names.ENTANGLED_PAIR, () -> new EntangledPairItem(DEFAULT_PROPS.maxStackSize(2)));
    }

    public static class Tiles {
        private static final DeferredRegister<TileEntityType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, ID);

        public static final RegistryObject<TileEntityType<PortalTileEntity>> PORTAL = REGISTRY.register(Names.PORTAL, () -> new ModTileType<>(PortalTileEntity::new, Blocks.PORTAL.get()));
        public static final RegistryObject<TileEntityType<SolidFuelGeneratorTile>> SOLID_FUEL_GENERATOR = REGISTRY.register(Names.SOLID_FUEL_GENERATOR, () -> new ModTileType<>(SolidFuelGeneratorTile::new, Blocks.SOLID_FUEL_GENERATOR.get()));
        public static final RegistryObject<TileEntityType<SlitCannonTile>> SLIT_CANNON = REGISTRY.register(Names.SLIT_CANNON, () -> new ModTileType<>(SlitCannonTile::new, Blocks.SLIT_CANNON.get()));
        public static final RegistryObject<TileEntityType<EntanglementCatcherTile>> ENTANGLEMENT_CATCHER = REGISTRY.register(Names.ENTANGLEMENT_CATCHER, () -> new ModTileType<>(EntanglementCatcherTile::new, Blocks.ENTANGLEMENT_CATCHER.get()));

    }

    public static class Tags {
        public static final Tag<Item> PORTAL_FRONT_WHITELIST = new ItemTags.Wrapper(new ResourceLocation(ID, "portal_front_whitelist"));
    }
}
