package chickendinner.portalmod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryHandlers {

    private RegistryHandlers(){}

    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new Block(Block.Properties.from(Blocks.COBBLESTONE).lightValue(15)).setRegistryName(Names.PORTAL));
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(ModBlocks.PORTAL, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(Names.PORTAL));
    }
}
