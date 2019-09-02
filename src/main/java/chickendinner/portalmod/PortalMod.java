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
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Set;

import static net.minecraft.block.Blocks.IRON_BLOCK;

/**
 * Main mod class.
 */
@Mod(PortalMod.ID)
public class PortalMod {
    public static final String ID = "portalmod";
    public static final String NAME = "Portal Mod";
    public static final Marker DEBUG_LOG = MarkerManager.getMarker(NAME);

    /**
     * Registers the registry common and client setup event handlers to the mod event bus.
     * <br>
     * Registers the config to the mod loading context.
     */
    public PortalMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(CommonSetup.INSTANCE);
        FMLJavaModLoadingContext.get().getModEventBus().register(ClientSetup.INSTANCE);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }

    private static ResourceLocation rl(String name) {
        return new ResourceLocation(ID, name);
    }

    @ObjectHolder(ID)
    public static class Blocks {
        public static final Block PORTAL = null;
        public static final Block SOLID_FUEL_GENERATOR = null;
        public static final Block ENTANGLEMENT_CATCHER = null;
        public static final Block SLIT_CANNON = null;
        public static final Block SLIT_BLOCK = null;
        public static final Block MACHINE_BASE = null;

        public static Set<Block> get() {
            final Block.Properties machineProperties = Block.Properties.from(IRON_BLOCK);
            return ImmutableSet.of(
                    new PortalBlock(machineProperties).setRegistryName(rl(Names.PORTAL)),
                    new SolidFuelGeneratorBlock(machineProperties).setRegistryName(rl(Names.SOLID_FUEL_GENERATOR)),
                    new Block(machineProperties).setRegistryName(rl(Names.MACHINE_BASE)),
                    new HorizontalFacingBlock(machineProperties).setRegistryName(rl(Names.SLIT_BLOCK)),
                    new SlitCannonBlock(machineProperties).setRegistryName(rl(Names.SLIT_CANNON)),
                    new EntanglementCatcherBlock(machineProperties).setRegistryName(rl(Names.ENTANGLEMENT_CATCHER))
            );
        }
    }

    @ObjectHolder(ID)
    public static class Items {
        public static final BlockItem PORTAL = null;
        public static final BlockItem SOLID_FUEL_GENERATOR = null;
        public static final BlockItem SLIT_CANNON = null;
        public static final BlockItem SLIT_BLOCK = null;
        public static final BlockItem ENTANGLEMENT_CATCHER = null;
        public static final BlockItem MACHINE_BASE = null;

        public static final Item PORTAL_LINKER = null;
        public static final Item PORTAL_LINK_BREAKER = null;
        public static final Item ENTANGLED_PAIR = null;

        public static Set<Item> get() {
            final Item.Properties defaultProps = new Item.Properties().group(PortalModGroup.INSTANCE);
            return ImmutableSet.of(
                    new BlockItem(Blocks.PORTAL, defaultProps).setRegistryName(rl(Names.PORTAL)),
                    new BlockItem(Blocks.SOLID_FUEL_GENERATOR, defaultProps.maxStackSize(1)).setRegistryName(rl(Names.SOLID_FUEL_GENERATOR)),
                    new BlockItem(Blocks.SLIT_CANNON, defaultProps.maxStackSize(1)).setRegistryName(rl(Names.SLIT_CANNON)),
                    new BlockItem(Blocks.SLIT_BLOCK, defaultProps).setRegistryName(rl(Names.SLIT_BLOCK)),
                    new BlockItem(Blocks.ENTANGLEMENT_CATCHER, defaultProps.maxStackSize(1)).setRegistryName(rl(Names.ENTANGLEMENT_CATCHER)),
                    new BlockItem(Blocks.MACHINE_BASE, defaultProps.maxStackSize(1)).setRegistryName(rl(Names.MACHINE_BASE)),

                    new PortalLinkerItem(defaultProps.maxStackSize(1)).setRegistryName(rl(Names.PORTAL_LINKER)),
                    new PortalLinkBreakerItem(defaultProps.maxStackSize(1)).setRegistryName(rl(Names.PORTAL_LINK_BREAKER)),
                    new EntangledPairItem(defaultProps.maxStackSize(2)).setRegistryName(rl(Names.ENTANGLED_PAIR))
            );
        }
    }

    @ObjectHolder(ID)
    public static class Tiles {
        public static final TileEntityType<PortalTileEntity> PORTAL = null;
        public static final TileEntityType<SolidFuelGeneratorTile> SOLID_FUEL_GENERATOR = null;
        public static final TileEntityType<SlitCannonTile> SLIT_CANNON = null;
        public static final TileEntityType<EntanglementCatcherTile> ENTANGLEMENT_CATCHER = null;

        public static Set<TileEntityType<? extends TileEntity>> get() {
            return ImmutableSet.of(
                    new ModTileType<>(PortalTileEntity::new, Blocks.PORTAL).setRegistryName(rl(Names.PORTAL)),
                    new ModTileType<>(SolidFuelGeneratorTile::new, Blocks.SOLID_FUEL_GENERATOR).setRegistryName(rl(Names.SOLID_FUEL_GENERATOR)),
                    new ModTileType<>(SlitCannonTile::new, Blocks.SLIT_CANNON).setRegistryName(rl(Names.SLIT_CANNON)),
                    new ModTileType<>(EntanglementCatcherTile::new, Blocks.ENTANGLEMENT_CATCHER).setRegistryName(rl(Names.ENTANGLEMENT_CATCHER))
            );
        }
    }

    public static class Tags {
        public static final Tag<Item> PORTAL_FRONT_WHITELIST = new ItemTags.Wrapper(rl("portal_front_whitelist"));
    }
}
