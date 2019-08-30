package chickendinner.portalmod.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public class VectorUtils {
    public static String convertToCoordinate(Vec3i v) {
        return String.format("(%d,%d,%d)", v.getX(), v.getY(), v.getZ());
    }

    public static String convertToCoordinate(Vec3d v) {
        return String.format("(%f,%f,%f)", v.getX(), v.getY(), v.getZ());
    }

    public static String convertToCoordinate(Vector3d v) {
        return String.format("(%f,%f,%f)", v.getX(), v.getY(), v.getZ());
    }

    // The quaternion that represents the rotation from a to b
    public static Quat4d quaternionDifference(Quat4d a, Quat4d b) {
        if (a.equals(b) || Math.abs(new Vector4d(a).dot(new Vector4d(b))) > 1.0F - 1e-6) {
            return new Quat4d();
        }

//        return Quat4d.mul(Quat4d.negate(a, null), b, null);

        // QTransition = QFinal * QInitial^{-1}
        // diff = b * inverse(a)
        Quat4d q = new Quat4d();
        q.inverse(a);
        q.mul(b, q);
        return q;
    }

    // The quaternion representing the shortest arc between a and b
    public static Quat4d rotationBetween(Vector3d a, Vector3d b) {
//        Quaternion q;
//        vector a = crossproduct(v1, v2);
//        q.xyz = a;
//        q.w = sqrt((v1.Length ^ 2) * (v2.Length ^ 2)) + dotproduct(v1, v2);

        Vector3d v = new Vector3d();
        v.cross(a, b);

        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();
        double w = Math.sqrt(a.lengthSquared() * b.lengthSquared()) + a.dot(b);

        return new Quat4d(x, y, z, w); // constructor normalizes the quaternion
    }

    public static Vector3d rotateVector3d(Quat4d quat, Vector3d vec) {
        Matrix3d rotationMatrix = new Matrix3d();
        rotationMatrix.set(quat);

        Vector3d v = new Vector3d(vec);
        rotationMatrix.transform(v);
        return v;
    }
}
