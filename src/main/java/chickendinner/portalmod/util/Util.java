package chickendinner.portalmod.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static chickendinner.portalmod.PortalMod.DEBUG_LOG;

public enum Util {
    ;
    private static final Logger LOGGER = LogManager.getLogger();

    public static void printStackTrace() {
        printStackTrace(0);
    }

    public static void printStackTrace(int toSkip) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length - toSkip; i++) {
            if (i < 2 + toSkip) continue;
            LOGGER.error(DEBUG_LOG, stackTrace[i]);
        }
    }

    public static void printErrorWithStackTrace(String error) {
        LOGGER.error(DEBUG_LOG, error);
        printStackTrace(1);
    }


    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int burnTime = ForgeEventFactory.getItemBurnTime(stack, stack.getBurnTime());
        if (burnTime == -1) {
            Integer vanillaBurnTime = AbstractFurnaceTileEntity.getBurnTimes().get(stack.getItem());
            burnTime = vanillaBurnTime == null ? 0 : vanillaBurnTime;
        }
        return burnTime;
    }

}
