package chickendinner.portalmod.event;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.block.PortalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public enum CommonEventHandler {
    INSTANCE;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockLeftClick(final PlayerInteractEvent.LeftClickBlock event) {
        BlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.getBlock() == PortalMod.Blocks.PORTAL
                && !PortalMod.Tags.PORTAL_FRONT_WHITELIST.contains(event.getItemStack().getItem())
                && event.getFace() == state.get(PortalBlock.FACING)) {
            // TODO: forward action
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY); //TODO check if fixed by https://github.com/MinecraftForge/MinecraftForge/pull/6047
            event.setResult(Event.Result.DENY);
            event.setCancellationResult(ActionResultType.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockRightClick(final PlayerInteractEvent.RightClickBlock event) {
        BlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.getBlock() == PortalMod.Blocks.PORTAL
                && !PortalMod.Tags.PORTAL_FRONT_WHITELIST.contains(event.getItemStack().getItem())
                && event.getFace() == state.get(PortalBlock.FACING)) {
            // TODO: forward action
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY); //TODO check if fixed by https://github.com/MinecraftForge/MinecraftForge/pull/6047
            event.setResult(Event.Result.DENY);
            event.setCancellationResult(ActionResultType.FAIL);
            event.setCanceled(true);
        }
    }

}
