package chickendinner.portalmod.tileentity;

import chickendinner.portalmod.block.PortalBlock;
import chickendinner.portalmod.registry.ModTileTypes;
import chickendinner.portalmod.util.PortalLinkResult;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.*;

public class PortalTileEntity extends TileEntity implements ITickableTileEntity {

    private BlockPos destPos;
    private PortalSurface surface;
    private int surfaceCoordU;
    private int surfaceCoordV;
    private boolean relinkFlag = false;

    public PortalTileEntity() {
        super(ModTileTypes.PORTAL);
    }

    @Override
    public void tick() {
        World world = this.getWorld();

        if (world != null) {
            if (relinkFlag) {
                relinkFlag = false;

                if (!this.isLinked() && this.destPos != null) {
                    TileEntity tile = world.getTileEntity(this.destPos);

                    if (tile instanceof PortalTileEntity) {
                        PortalLinkResult portalLinkResult = this.linkPortal(((PortalTileEntity) tile));
                        if (portalLinkResult != PortalLinkResult.SUCCESS) {
                            System.err.println("Failed to load portal link: " + portalLinkResult.toString());
                        }
                    } else {
                        System.err.println(String.format("Failed to load portal link from [%d,%d,%d] to [%d,%d,%d]. Destination position is not a portal tile",
                                this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                                this.destPos.getX(), this.destPos.getY(), this.destPos.getZ())
                        );
                    }
                }
            }
        }
    }

    @Override
    public void remove() {
        this.unlinkPortal();
        super.remove();
    }

    @Override
    public void read(CompoundNBT compound) {
        if (compound.contains("destPos")) {
            this.destPos = NBTUtil.readBlockPos(compound.getCompound("destPos"));
            this.relinkFlag = true;
        }

        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (destPos != null) {
            compound.put("destPos", NBTUtil.writeBlockPos(this.destPos));
        }

        return super.write(compound);
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
            TileEntity tile = world.getTileEntity(position);

            if (tile instanceof PortalTileEntity) {
                Vec3i d = position.subtract(surface0.origin);
                PortalTileEntity portalTile = (PortalTileEntity) tile;
                portalTile.surfaceCoordU = surface0.uAxis.getAxisDirection().getOffset() * surface0.uAxis.getAxis().getCoordinate(d.getX(), d.getY(), d.getZ());
                portalTile.surfaceCoordV = surface0.vAxis.getAxisDirection().getOffset() * surface0.vAxis.getAxis().getCoordinate(d.getX(), d.getY(), d.getZ());
                portalTile.destPos = surface1.origin.offset(surface1.uAxis, portalTile.surfaceCoordU).offset(surface1.vAxis, portalTile.surfaceCoordV);
                portalTile.surface = surface0;
                portalTile.markDirty();
            }
        }

        for (BlockPos position : surface1.positions) {
            TileEntity tile = world.getTileEntity(position);

            if (tile instanceof PortalTileEntity) {
                Vec3i d = position.subtract(surface1.origin);
                PortalTileEntity portalTile = (PortalTileEntity) tile;
                portalTile.surfaceCoordU = surface1.uAxis.getAxisDirection().getOffset() * surface1.uAxis.getAxis().getCoordinate(d.getX(), d.getY(), d.getZ());
                portalTile.surfaceCoordV = surface1.vAxis.getAxisDirection().getOffset() * surface1.vAxis.getAxis().getCoordinate(d.getX(), d.getY(), d.getZ());
                portalTile.destPos = surface0.origin.offset(surface0.uAxis, portalTile.surfaceCoordU).offset(surface0.vAxis, portalTile.surfaceCoordV);
                portalTile.surface = surface1;
                portalTile.markDirty();
            }
        }

        surface0.destSurface = surface1;
        surface1.destSurface = surface0;

        return PortalLinkResult.SUCCESS;
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

    public int getSurfaceCoordU() {
        return surfaceCoordU;
    }

    public int getSurfaceCoordV() {
        return surfaceCoordV;
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
        public BlockPos origin;
        public PortalSurface destSurface;
    }
}
