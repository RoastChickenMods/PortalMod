package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.block.PortalBlock;
import chickendinner.portalmod.registry.ModTileTypes;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.vecmath.Matrix4f;
import java.util.*;

public class PortalTileEntity extends TileEntity implements ITickableTileEntity {

    private BlockPos destPos;
    private PortalSurface surface;
    private int surfaceCoordU;
    private int surfaceCoordV;

    public PortalTileEntity() {
        super(ModTileTypes.PORTAL);
    }

    @Override
    public void tick() {

    }

    public Matrix4f getRelativeAxis(Direction src, Direction dst) {
        /*
            XX YX ZX TX
            XY YY ZY TY
            XZ YZ ZZ TZ
            00 00 00 01
         */
//        if (src.equals(Direction.NORTH) && dst.equals(Direction.NORTH)) {
//            return new Matrix4f()
//        }

        return null;
    }

    public boolean linkPortal(PortalTileEntity portal) {
        World world = this.getWorld();

        if (world == null || portal == null) {
            return false;
        }

        int[] surfaceCoord0 = new int[2];
        int[] surfaceCoord1 = new int[2];

        BlockPos pos0 = this.getPos();
        BlockPos pos1 = portal.getPos();

        PortalSurface surface0 = this.buildPortalSurface(surfaceCoord0);
        PortalSurface surface1 = portal.buildPortalSurface(surfaceCoord1);


        if (!world.isRemote) {


            StringBuilder sb0 = new StringBuilder("Portal 0 shape:\n");

            for (int v = surface0.shape[0].length - 1; v >= 0; v--) {
                for (int u = 0; u < surface0.shape.length; u++) {
                    boolean a = u == surfaceCoord0[0] && v == surfaceCoord0[1];
                    boolean o = u == 0 && v == 0;
                    sb0.append(o ? 'O' : (a ? 'A' : (surface0.shape[u][v] ? '1' : '0')));
                }

                sb0.append('\n');
            }

            StringBuilder sb1 = new StringBuilder("Portal 1 shape:\n");
            for (int v = surface1.shape[0].length - 1; v >= 0; v--) {
                for (int u = 0; u < surface1.shape.length; u++) {
                    boolean b = u == surfaceCoord1[0] && v == surfaceCoord1[1];
                    boolean o = u == 0 && v == 0;
                    sb1.append(o ? 'O' : (b ? 'B' : (surface1.shape[u][v] ? '1' : '0')));
                }
                sb1.append('\n');
            }

            System.out.println(sb0 + "\n" + sb1);
        }





        if (surface0 == null || surface1 == null || !Arrays.deepEquals(surface0.shape, surface1.shape)) {
            return false;
        }

        for (BlockPos p : surface0.positions) {
            if (surface1.positions.contains(p)) {
                return false;
            }
        }

        this.surfaceCoordU = surfaceCoord0[0];
        this.surfaceCoordV = surfaceCoord0[1];
        portal.surfaceCoordU = surfaceCoord1[0];
        portal.surfaceCoordV = surfaceCoord1[1];

        // origin = blockpos - (uAxis * uOffset + vAxis * vOffset)

        int xOrigin0 = pos0.getX() - (surface0.uAxis.getDirectionVec().getX() * surfaceCoord0[0] + surface0.vAxis.getDirectionVec().getX() * surfaceCoord0[1]);
        int yOrigin0 = pos0.getY() - (surface0.uAxis.getDirectionVec().getY() * surfaceCoord0[0] + surface0.vAxis.getDirectionVec().getY() * surfaceCoord0[1]);
        int zOrigin0 = pos0.getZ() - (surface0.uAxis.getDirectionVec().getZ() * surfaceCoord0[0] + surface0.vAxis.getDirectionVec().getZ() * surfaceCoord0[1]);
        int xOrigin1 = pos1.getX() - (surface1.uAxis.getDirectionVec().getX() * surfaceCoord1[0] + surface1.vAxis.getDirectionVec().getX() * surfaceCoord1[1]);
        int yOrigin1 = pos1.getY() - (surface1.uAxis.getDirectionVec().getY() * surfaceCoord1[0] + surface1.vAxis.getDirectionVec().getY() * surfaceCoord1[1]);
        int zOrigin1 = pos1.getZ() - (surface1.uAxis.getDirectionVec().getZ() * surfaceCoord1[0] + surface1.vAxis.getDirectionVec().getZ() * surfaceCoord1[1]);

        world.setBlockState(new BlockPos(xOrigin0, yOrigin0, zOrigin0), Blocks.STONE.getDefaultState());
        world.setBlockState(new BlockPos(xOrigin1, yOrigin1, zOrigin1), Blocks.OAK_PLANKS.getDefaultState());

        return true;
    }

    private PortalSurface buildPortalSurface(int[] surfaceCoord) {
        PortalSurface surface = new PortalSurface();

        surface.direction = this.getBlockState().get(PortalBlock.FACING);

        /*
                 -z(N)
                   ^
                   |
         -x(W) <---+---> +x(E)
                   |
                   v
                 +z(S)
         */





        if (surface.direction.equals(Direction.NORTH)) {
            surface.uAxis = Direction.EAST;
            surface.vAxis = Direction.UP;
        } else if (surface.direction.equals(Direction.EAST)) {
            surface.uAxis = Direction.NORTH;
            surface.vAxis = Direction.UP;
        } else if (surface.direction.equals(Direction.SOUTH)) {
            surface.uAxis = Direction.EAST;
            surface.vAxis = Direction.UP;
        } else if (surface.direction.equals(Direction.WEST)) {
            surface.uAxis = Direction.SOUTH;
            surface.vAxis = Direction.UP;
        } else {
            return null;
        }

        Set<BlockPos> surfaceSet = new HashSet<>();
        Direction[] searchPlane = new Direction[]{surface.uAxis, surface.uAxis.getOpposite(), surface.vAxis, surface.vAxis.getOpposite()};
        walkSearch(searchPlane, surface.direction, new HashSet<>(), surfaceSet, surface.bounds);
        surface.positions.addAll(surfaceSet);

        int xSize = surface.bounds[PortalSurface.X_MAX] - surface.bounds[PortalSurface.X_MIN];
        int ySize = surface.bounds[PortalSurface.Y_MAX] - surface.bounds[PortalSurface.Y_MIN];
        int zSize = surface.bounds[PortalSurface.Z_MAX] - surface.bounds[PortalSurface.Z_MIN];

        if (surface.direction.equals(Direction.NORTH)) {
            surface.shape = new boolean[xSize][ySize];

            for (int u = 0; u < xSize; u++) {
                for (int v = 0; v < ySize; v++) {
                    BlockPos pos = new BlockPos(surface.bounds[PortalSurface.X_MIN] + u, surface.bounds[PortalSurface.Y_MIN] + v, surface.bounds[PortalSurface.Z_MIN]);
                    surface.shape[u][v] = surface.positions.contains(pos);
                    if (pos.equals(this.getPos())) {
                        surfaceCoord[0] = u;
                        surfaceCoord[1] = v;
                    }
                }
            }
        } else if (surface.direction.equals(Direction.SOUTH)) {
            surface.shape = new boolean[xSize][ySize];

            for (int u = 0; u < xSize; u++) {
                for (int v = 0; v < ySize; v++) {
                    BlockPos pos = new BlockPos(surface.bounds[PortalSurface.X_MIN] + u, surface.bounds[PortalSurface.Y_MIN] + v, surface.bounds[PortalSurface.Z_MIN]);
                    surface.shape[u][v] = surface.positions.contains(pos);
                    if (pos.equals(this.getPos())) {
                        surfaceCoord[0] = u;
                        surfaceCoord[1] = v;
                    }
                }
            }
        } else if (surface.direction.equals(Direction.WEST)) {
            surface.shape = new boolean[zSize][ySize];

            for (int u = 0; u < zSize; u++) {
                for (int v = 0; v < ySize; v++) {
                    BlockPos pos = new BlockPos(surface.bounds[PortalSurface.X_MIN], surface.bounds[PortalSurface.Y_MIN] + v, surface.bounds[PortalSurface.Z_MIN] + u);
                    surface.shape[u][v] = surface.positions.contains(pos);
                    if (pos.equals(this.getPos())) {
                        surfaceCoord[0] = u;
                        surfaceCoord[1] = v;
                    }
                }
            }
        } else if (surface.direction.equals(Direction.EAST)) {
            surface.shape = new boolean[zSize][ySize];

            for (int u = 0; u < zSize; u++) {
                for (int v = 0; v < ySize; v++) {
                    BlockPos pos = new BlockPos(surface.bounds[PortalSurface.X_MIN], surface.bounds[PortalSurface.Y_MIN] + v, surface.bounds[PortalSurface.Z_MIN] + v);
                    surface.shape[u][v] = surface.positions.contains(pos);
                    if (pos.equals(this.getPos())) {
                        surfaceCoord[0] = u;
                        surfaceCoord[1] = v;
                    }
                }
            }
        }

        return surface;
    }

    private void walkSearch(Direction[] searchPlane, Direction direction, Set<BlockPos> visited, Set<BlockPos> surface, int[] bounds) {
        BlockPos pos = this.getPos();
        World world = this.getWorld();

        if (world != null) {
            surface.add(pos);
            bounds[PortalSurface.X_MIN] = Math.min(bounds[PortalSurface.X_MIN], pos.getX());
            bounds[PortalSurface.Y_MIN] = Math.min(bounds[PortalSurface.Y_MIN], pos.getY());
            bounds[PortalSurface.Z_MIN] = Math.min(bounds[PortalSurface.Z_MIN], pos.getZ());
            bounds[PortalSurface.X_MAX] = Math.max(bounds[PortalSurface.X_MAX], pos.getX() + 1);
            bounds[PortalSurface.Y_MAX] = Math.max(bounds[PortalSurface.Y_MAX], pos.getY() + 1);
            bounds[PortalSurface.Z_MAX] = Math.max(bounds[PortalSurface.Z_MAX], pos.getZ() + 1);

            for (Direction d : searchPlane) {
                BlockPos adjacentPos = pos.offset(d);
                if (!visited.contains(adjacentPos)) {
                    visited.add(adjacentPos);

                    TileEntity adjacentTile = world.getTileEntity(adjacentPos);

                    if (adjacentTile instanceof PortalTileEntity && world.getBlockState(adjacentPos).get(PortalBlock.FACING).equals(direction)) {
                        ((PortalTileEntity) adjacentTile).walkSearch(searchPlane, direction, visited, surface, bounds);
                    }
                }
            }
        }
    }

    private static class PortalSurface {
        public static final int X_MIN = 0;
        public static final int Y_MIN = 1;
        public static final int Z_MIN = 2;
        public static final int X_MAX = 3;
        public static final int Y_MAX = 4;
        public static final int Z_MAX = 5;

        public List<BlockPos> positions = new ArrayList<>();
        public Direction direction = null;
        public boolean[][] shape = null;
        public int[] bounds = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        public Direction uAxis;
        public Direction vAxis;
    }
}
