package chickendinner.portalmod.util;

import net.minecraft.util.math.Vec3i;

public class VectorUtils {
    public static String convertToCoordinate(Vec3i v) {
        return String.format("(%d,%d,%d)", v.getX(), v.getY(), v.getZ());
    }
}
