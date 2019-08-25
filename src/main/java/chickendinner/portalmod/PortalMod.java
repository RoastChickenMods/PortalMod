package chickendinner.portalmod;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod(PortalMod.ID)
public class PortalMod {
    public static final String ID = "portalmod";

    // Used to be preinit
    public PortalMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    // Used to be init
    private void setup(final FMLCommonSetupEvent event) {

    }

}
