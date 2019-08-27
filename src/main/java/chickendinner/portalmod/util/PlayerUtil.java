package chickendinner.portalmod.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public enum PlayerUtil {
    ;

    public static void tellPlayer(PlayerEntity player, String message) {
        tellPlayer(player, message, true);
    }

    public static void tellPlayer(PlayerEntity player, String message, boolean actionBar) {
        player.sendStatusMessage(new TranslationTextComponent(message), actionBar);
    }
}
