package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.block.PortalBlock;
import chickendinner.portalmod.registry.ModTileTypes;
import chickendinner.portalmod.util.PortalLinkResult;
import chickendinner.portalmod.util.VectorUtils;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import java.util.*;

public class PortalTileEntity extends TileEntity implements ITickableTileEntity {

    private BlockPos destPos = null;
    private PortalSurface surface = null;
    private int surfaceCoordU = 0;
    private int surfaceCoordV = 0;
    private boolean relinkFlag = false;

    private Matrix4d transformation = null;

    public PortalTileEntity() {
        super(ModTileTypes.PORTAL);
    }

    @Override
    public void tick() {

    }

    @Override
    public void remove() {
        this.unlinkPortal();
        super.remove();
    }

    @Override
    public void read(CompoundNBT compound) {
        if (compound.contains("surface")) {
            surface = new PortalSurface();
            surface.deserializeNBT(compound.getCompound("surface"));
            if (compound.contains("destPos")) {
                destPos = NBTUtil.readBlockPos(compound.getCompound("destPos"));
            }
            if (compound.contains("surfaceCoordU")) {
                surfaceCoordU = compound.getInt("surfaceCoordU");
            }
            if (compound.contains("surfaceCoordV")) {
                surfaceCoordV = compound.getInt("surfaceCoordV");
            }
        }
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (surface != null) {
            compound.put("surface", surface.serializeNBT());
            if (getDestPos() != null) {
                compound.put("destPos", NBTUtil.writeBlockPos(getDestPos()));
            }
            if (getSurfaceCoordU() != 0) {
                compound.putInt("surfaceCoordU", getSurfaceCoordU());
            }
            if (getSurfaceCoordV() != 0) {
                compound.putInt("surfaceCoordV", getSurfaceCoordV());
            }
        }

        return super.write(compound);
    }

    public Vec3d getTransformedPoint(Vec3d point) {
        Vector3d p = this.getTransformedPoint(new Vector3d(point.getX(), point.getY(), point.getZ()));
        return new Vec3d(p.getX(), p.getY(), p.getZ());
    }

    public Vector3d getTransformedPoint(Vector3d point) {

        if (this.isLinked()) {
            PortalSurface srcSurface = this.getSurface();
            BlockPos srcBlockPosition = this.getPos();
            Vector3d srcPortalPosition = new Vector3d(srcBlockPosition.getX(), srcBlockPosition.getY(), srcBlockPosition.getZ());
            Quat4d srcPortalRotation = new Quat4d();
            srcPortalRotation.set(new AxisAngle4d(0.0, 1.0, 0.0, Math.toRadians(srcSurface.direction.getHorizontalAngle())));

            PortalSurface dstSurface = this.getSurface().destSurface;
            BlockPos dstBlockPosition = this.getDestPos();
            Vector3d dstPortalPosition = new Vector3d(dstBlockPosition.getX(), dstBlockPosition.getY(), dstBlockPosition.getZ());
            Quat4d dstPortalRotation = new Quat4d();
            dstPortalRotation.set(new AxisAngle4d(0.0, 1.0, 0.0, Math.toRadians(dstSurface.direction.getHorizontalAngle())));

            Vector3d portalToPoint = new Vector3d(point);
            portalToPoint.sub(srcPortalPosition);

            Quat4d srcToDstRotation = VectorUtils.quaternionDifference(srcPortalRotation, dstPortalRotation);

            Quat4d rotate180 = new Quat4d();
            rotate180.set(new AxisAngle4d(0.0, 1.0, 0.0, Math.PI));

            Vector3d transformedPoint = new Vector3d(portalToPoint);
            transformedPoint = VectorUtils.rotateVector3d(rotate180, transformedPoint);
            transformedPoint = VectorUtils.rotateVector3d(srcToDstRotation, transformedPoint);

            transformedPoint.add(dstPortalPosition);

            return transformedPoint;
        }

        return null;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    public void unlinkPortal() {
        World world = this.getWorld();

        if (world != null) {

            PortalSurface surface0 = this.surface;
            if (surface0 != null) {
                for (BlockPos position : surface0.positions) {
                    TileEntity tile = world.getTileEntity(position);
                    if (tile instanceof PortalTileEntity) {
                        PortalTileEntity portalTile = (PortalTileEntity) tile;

                        portalTile.surface = null;
                        portalTile.destPos = null;
                        portalTile.transformation = null;
                        portalTile.surfaceCoordU = 0;
                        portalTile.surfaceCoordV = 0;
                        portalTile.markDirty();
                    }
                }

                PortalSurface surface1 = surface0.destSurface;
                if (surface1 != null) {
                    for (BlockPos position : surface1.positions) {
                        TileEntity tile = world.getTileEntity(position);
                        if (tile instanceof PortalTileEntity) {
                            PortalTileEntity portalTile = (PortalTileEntity) tile;

                            portalTile.surface = null;
                            portalTile.destPos = null;
                            portalTile.transformation = null;
                            portalTile.surfaceCoordU = 0;
                            portalTile.surfaceCoordV = 0;
                            portalTile.markDirty();
                        }
                    }
                }
            }
        }
    }

    public PortalLinkResult linkPortal(PortalTileEntity portal) {
        World world = this.getWorld();

        if (world == null || portal == null) {
            return PortalLinkResult.MISSING_DESTINATION_ERROR;
        }

        int[] surfaceCoord0 = new int[2];
        int[] surfaceCoord1 = new int[2];

        BlockPos pos0 = this.getPos();
        BlockPos pos1 = portal.getPos();

        PortalSurface surface0 = this.buildPortalSurface(surfaceCoord0);
        PortalSurface surface1 = portal.buildPortalSurface(surfaceCoord1);

        if (surface0 == null || surface1 == null) {
            return PortalLinkResult.INVALID_STATE_ERROR;
        }

        if (surface0.direction.equals(surface1.direction.getOpposite()) || surface0.direction.equals(surface1.direction)) {
            surface1.uAxis = surface1.uAxis.getOpposite();
            surfaceCoord1[0] = (surface1.shape.length - 1) - surfaceCoord1[0];
            boolean[][] flippedShape = new boolean[surface1.shape.length][surface1.shape[0].length];

            for (int v = 0; v < surface1.shape[0].length; v++) {
                for (int u = 0; u < surface1.shape.length; u++) {
                    flippedShape[(surface1.shape.length - 1) - u][v] = surface1.shape[u][v];
                }
            }

            surface1.shape = flippedShape;
        }

        if (!Arrays.deepEquals(surface0.shape, surface1.shape)) {
            return PortalLinkResult.SHAPE_MISMATCH_ERROR;
        }

        for (BlockPos p : surface0.positions) {
            if (surface1.positions.contains(p)) {
                return PortalLinkResult.LINK_TO_SELF_ERROR;
            }
        }

        // origin = blockpos - (uAxis * uOffset + vAxis * vOffset)

        int xOrigin0 = pos0.getX() - (surface0.uAxis.getDirectionVec().getX() * surfaceCoord0[0] + surface0.vAxis.getDirectionVec().getX() * surfaceCoord0[1]);
        int yOrigin0 = pos0.getY() - (surface0.uAxis.getDirectionVec().getY() * surfaceCoord0[0] + surface0.vAxis.getDirectionVec().getY() * surfaceCoord0[1]);
        int zOrigin0 = pos0.getZ() - (surface0.uAxis.getDirectionVec().getZ() * surfaceCoord0[0] + surface0.vAxis.getDirectionVec().getZ() * surfaceCoord0[1]);
        int xOrigin1 = pos1.getX() - (surface1.uAxis.getDirectionVec().getX() * surfaceCoord1[0] + surface1.vAxis.getDirectionVec().getX() * surfaceCoord1[1]);
        int yOrigin1 = pos1.getY() - (surface1.uAxis.getDirectionVec().getY() * surfaceCoord1[0] + surface1.vAxis.getDirectionVec().getY() * surfaceCoord1[1]);
        int zOrigin1 = pos1.getZ() - (surface1.uAxis.getDirectionVec().getZ() * surfaceCoord1[0] + surface1.vAxis.getDirectionVec().getZ() * surfaceCoord1[1]);

        surface0.origin = new BlockPos(xOrigin0, yOrigin0, zOrigin0);
        surface1.origin = new BlockPos(xOrigin1, yOrigin1, zOrigin1);

        for (BlockPos position : surface0.positions) {
            linkBlock(position, surface0, surface1);
        }

        for (BlockPos position : surface1.positions) {
            linkBlock(position, surface1, surface0);
        }

        surface0.destSurface = surface1;
        surface1.destSurface = surface0;

        return PortalLinkResult.SUCCESS;
    }

    private void linkBlock(BlockPos position, PortalSurface surface0, PortalSurface surface1) {
        World world = this.getWorld();
        if (world != null) {

            TileEntity tile = world.getTileEntity(position);
            if (tile instanceof PortalTileEntity) {

                PortalTileEntity portalTile = (PortalTileEntity) tile;
                Vec3i d = position.subtract(surface0.origin);
                portalTile.surfaceCoordU = surface0.uAxis.getAxisDirection().getOffset() * surface0.uAxis.getAxis().getCoordinate(d.getX(), d.getY(), d.getZ());
                portalTile.surfaceCoordV = surface0.vAxis.getAxisDirection().getOffset() * surface0.vAxis.getAxis().getCoordinate(d.getX(), d.getY(), d.getZ());
                portalTile.destPos = surface1.origin.offset(surface1.uAxis, portalTile.surfaceCoordU).offset(surface1.vAxis, portalTile.surfaceCoordV);
                portalTile.surface = surface0;

                Vector3d translation = new Vector3d();
                translation.setX(portalTile.destPos.getX() - position.getX());
                translation.setX(portalTile.destPos.getY() - position.getY());
                translation.setX(portalTile.destPos.getZ() - position.getZ());

                double horizontalAngle = Math.toRadians(surface1.direction.getHorizontalAngle() - surface0.direction.getHorizontalAngle());
                Quat4d rotation = new Quat4d();
                rotation.set(new AxisAngle4d(0.0, 1.0, 0.0, horizontalAngle));

                portalTile.transformation = new Matrix4d(rotation, translation, 1.0);

                portalTile.markDirty();
            }
        }
    }

    private PortalSurface buildPortalSurface(int[] surfaceCoord) {
        PortalSurface surface = new PortalSurface();

        surface.direction = this.getBlockState().get(PortalBlock.FACING);

        if (surface.direction.equals(Direction.NORTH)) {
            surface.uAxis = Direction.EAST;
            surface.vAxis = Direction.UP;
        } else if (surface.direction.equals(Direction.EAST)) {
            surface.uAxis = Direction.NORTH;
            surface.vAxis = Direction.UP;
        } else if (surface.direction.equals(Direction.SOUTH)) {
            surface.uAxis = Direction.WEST;
            surface.vAxis = Direction.UP;
        } else if (surface.direction.equals(Direction.WEST)) {
            surface.uAxis = Direction.SOUTH;
            surface.vAxis = Direction.UP;
        } else {
            return null;
        }

        Set<BlockPos> surfaceSet = new HashSet<>();
        walkSearch(new Direction[]{surface.uAxis, surface.uAxis.getOpposite(), surface.vAxis, surface.vAxis.getOpposite()}, surface.direction, new HashSet<>(), surfaceSet, surface.bounds);
        surface.positions.addAll(surfaceSet);

        int xSize = surface.bounds[PortalSurface.X_MAX] - surface.bounds[PortalSurface.X_MIN];
        int ySize = surface.bounds[PortalSurface.Y_MAX] - surface.bounds[PortalSurface.Y_MIN];
        int zSize = surface.bounds[PortalSurface.Z_MAX] - surface.bounds[PortalSurface.Z_MIN];

        if (surface.direction.equals(Direction.NORTH)) {
            surface.shape = new boolean[xSize][ySize];

            for (int u = 0; u < xSize; u++) {
                for (int v = 0; v < ySize; v++) {
                    BlockPos pos = new BlockPos(
                            surface.bounds[PortalSurface.X_MIN] + u,
                            surface.bounds[PortalSurface.Y_MIN] + v,
                            surface.bounds[PortalSurface.Z_MIN]
                    );
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
                    BlockPos pos = new BlockPos(
                            surface.bounds[PortalSurface.X_MIN] + ((xSize - 1) - u),
                            surface.bounds[PortalSurface.Y_MIN] + v,
                            surface.bounds[PortalSurface.Z_MIN]
                    );
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
                    BlockPos pos = new BlockPos(
                            surface.bounds[PortalSurface.X_MIN],
                            surface.bounds[PortalSurface.Y_MIN] + v,
                            surface.bounds[PortalSurface.Z_MIN] + u
                    );
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
                    BlockPos pos = new BlockPos(
                            surface.bounds[PortalSurface.X_MIN],
                            surface.bounds[PortalSurface.Y_MIN] + v,
                            surface.bounds[PortalSurface.Z_MIN] + ((zSize - 1) - u)
                    );
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

    private void walkSearch(Direction[] searchPlane, Direction
            direction, Set<BlockPos> visited, Set<BlockPos> surface, int[] bounds) {
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

    public boolean isLinked() {
        return this.destPos != null && this.surface != null;
    }

    public BlockPos getDestPos() {
        return destPos;
    }

    public PortalSurface getSurface() {
        return surface;
    }

    public PortalSurface getDestSurface() {
        return surface.destSurface;
    }

    public int getSurfaceCoordU() {
        return surfaceCoordU;
    }

    public int getSurfaceCoordV() {
        return surfaceCoordV;
    }

    public static class PortalSurface implements INBTSerializable<CompoundNBT> {
        public static final int X_MIN = 0;
        public static final int Y_MIN = 1;
        public static final int Z_MIN = 2;
        public static final int X_MAX = 3;
        public static final int Y_MAX = 4;
        public static final int Z_MAX = 5;
        
        private List<BlockPos> positions = new ArrayList<>();
        private Direction direction = null;
        private boolean[][] shape = null;
        private int[] bounds = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        private Direction uAxis;
        private Direction vAxis;
        private BlockPos origin;
        private PortalSurface destSurface;

        private PortalSurface() {}

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT positionsNBT = new ListNBT();
            positions.stream().map(NBTUtil::writeBlockPos).forEach(positionsNBT::add);
            nbt.put("positions", positionsNBT);
            nbt.putInt("direction", direction.getIndex());
            ListNBT shapeNBT = new ListNBT();
            for (boolean[] booleans : shape) {
                ListNBT shapeRow = new ListNBT();
                for (boolean bool : booleans) {
                    shapeRow.add(new ByteNBT((byte) (bool ? 1 : 0)));
                }
                shapeNBT.add(shapeRow);
            }
            nbt.put("shape", shapeNBT);
            nbt.putInt("shapeU", shape.length);
            nbt.putInt("shapeV", shape[0].length);
            nbt.putIntArray("bounds", bounds);
            nbt.putInt("uAxis", uAxis.getIndex());
            nbt.putInt("vAxis", vAxis.getIndex());
            nbt.put("origin", NBTUtil.writeBlockPos(origin));
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            ListNBT positionsNBT = nbt.getList("positions", 10);
            positionsNBT.stream().map(CompoundNBT.class::cast).map(NBTUtil::readBlockPos).forEach(positions::add);
            direction = Direction.byIndex(nbt.getInt("direction"));
            ListNBT shapeNBT = nbt.getList("shape", 9);
            int shapeU = nbt.getInt("shapeU");
            int shapeV = nbt.getInt("shapeV");
            shape = new boolean[shapeU][shapeV];
            for (int u = 0; u < shapeU; u++) {
                ListNBT list = shapeNBT.getList(u);
                for (int v = 0; v < shapeV; v++) {
                    shape[u][v] = ((ByteNBT) list.get(v)).getByte() == 1;
                }
            }
            bounds = nbt.getIntArray("bounds");
            uAxis = Direction.byIndex(nbt.getInt("uAxis"));
            vAxis = Direction.byIndex(nbt.getInt("vAxis"));
            origin = NBTUtil.readBlockPos(nbt.getCompound("origin"));
        }

        public List<BlockPos> getPositions() {
            return Collections.unmodifiableList(positions);
        }

        public Direction getDirection() {
            return direction;
        }

        public BlockPos getMinBounds() {
            return new BlockPos(this.bounds[X_MIN], this.bounds[Y_MIN], this.bounds[Z_MIN]);
        }

        public BlockPos getMaxBounds() {
            return new BlockPos(this.bounds[X_MAX], this.bounds[Y_MAX], this.bounds[Z_MAX]);
        }

        public Direction getUAxis() {
            return uAxis;
        }

        public Direction getVAxis() {
            return vAxis;
        }

        public BlockPos getOrigin() {
            return origin;
        }

        public PortalSurface getDestSurface() {
            return destSurface;
        }
    }
}
