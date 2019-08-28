package chickendinner.portalmod.util;

import chickendinner.portalmod.reference.MessageKey;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public enum PlayerUtil {
    ;

    public static void tellPlayer(PlayerEntity player, MessageKey message, Object... args) {
        tellPlayer(player, message, true, args);
    }

    public static void tellPlayer(PlayerEntity player, MessageKey message, boolean actionBar, Object... args) {
        player.sendStatusMessage(message.getTranslated(args), actionBar);
    }

    @Deprecated
    public static void tellPlayer(PlayerEntity player, String message) {
        tellPlayer(player, message, true);
    }

    @Deprecated
    public static void tellPlayer(PlayerEntity player, String message, boolean actionBar) {
        player.sendStatusMessage(new TranslationTextComponent(message), actionBar);
    }
}
