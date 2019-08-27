package chickendinner.portalmod;

import chickendinner.portalmod.block.PortalBlock;
import chickendinner.portalmod.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PortalMod.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public enum EventHandler {
    ;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockLeftClick(final PlayerInteractEvent.LeftClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != ModBlocks.PORTAL) {
            return;
        }
        Direction portalDir = state.get(PortalBlock.FACING);
        if (portalDir == event.getFace()) {
            // TODO: forward action
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY); //TODO check if fixed by https://github.com/MinecraftForge/MinecraftForge/pull/6047
            event.setResult(Event.Result.DENY);
            event.setCancellationResult(ActionResultType.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockRightClick(final PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != ModBlocks.PORTAL) {
            return;
        }
        Direction portalDir = state.get(PortalBlock.FACING);
        if (portalDir == event.getFace()) {
            // TODO: forward action
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY); //TODO check if fixed by https://github.com/MinecraftForge/MinecraftForge/pull/6047
            event.setResult(Event.Result.DENY);
            event.setCancellationResult(ActionResultType.FAIL);
            event.setCanceled(true);
        }
    }
}
