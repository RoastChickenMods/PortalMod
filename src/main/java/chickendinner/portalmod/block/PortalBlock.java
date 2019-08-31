package chickendinner.portalmod.block;

import chickendinner.portalmod.reference.ModBlocks;
import chickendinner.portalmod.tileentity.PortalTileEntity;
import net.minecraft.block.*;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Objects;

public class PortalBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape TOP = Block.makeCuboidShape(0, 16, 0, 16, 15.999, 16);
    private static final VoxelShape BOTTOM = Block.makeCuboidShape(0, 0, 0, 16, 0.001, 16);
    private static final VoxelShape NORTH = Block.makeCuboidShape(0, 0, 0, 16, 16, 0.001);
    private static final VoxelShape EAST = Block.makeCuboidShape(16, 0, 0, 15.999, 16, 16);
    private static final VoxelShape SOUTH = Block.makeCuboidShape(0, 0, 16, 16, 16, 15.999);
    private static final VoxelShape WEST = Block.makeCuboidShape(0, 0, 0, 0.001, 16, 16);

    public PortalBlock(Properties properties) {
        super(properties);
    }

    private VoxelShape getShapeFromDir(Direction dir) {
        switch (dir) {
            case DOWN:
                return BOTTOM;
            case UP:
                return TOP;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
        }
        return VoxelShapes.empty();
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entityIn) {
//        if (!world.isRemote) {
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof PortalTileEntity) {
                PortalTileEntity portalTile = (PortalTileEntity) tileEntity;

                if (portalTile.isLinked()) {

                    Vec3d entityPosition = entityIn.getPositionVector();
                    Vec3d entityLookVector = entityIn.getLook(1.0F);

                    Vec3d transformedPosition = portalTile.getTransformedPoint(entityPosition);
                    Vec3d transformedLookVector = portalTile.getTransformedVector(entityLookVector);

                    double x = transformedPosition.getX();
                    double y = transformedPosition.getY();
                    double z = transformedPosition.getZ();

                    double xzLen = MathHelper.sqrt(transformedLookVector.getX() * transformedLookVector.getX() + transformedLookVector.getZ() * transformedLookVector.getZ());
                    entityIn.rotationPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(transformedLookVector.getY(), xzLen) * (double)(180F / (float)Math.PI))));
                    entityIn.rotationYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(transformedLookVector.getZ(), transformedLookVector.getX()) * (double)(180F / (float)Math.PI)) - 90.0F);
//                    entityIn.prevRotationPitch = entityIn.rotationPitch;
//                    entityIn.prevRotationYaw = entityIn.rotationYaw;
//
//                    if (entityIn instanceof ClientPlayerEntity) {
//                        ((ClientPlayerEntity) entityIn).renderArmPitch = ((ClientPlayerEntity) entityIn).prevRenderArmPitch = entityIn.rotationPitch;
//                        ((ClientPlayerEntity) entityIn).renderArmYaw = ((ClientPlayerEntity) entityIn).prevRenderArmYaw = entityIn.rotationYaw;
//                    }

                    if (!world.isRemote) {
                        entityIn.setPositionAndUpdate(x, y, z);
                        portalTile.unlinkPortal();
                    }

//                    entityIn.posX = entityIn.prevPosX = entityIn.lastTickPosX = x;
//                    entityIn.posY = entityIn.prevPosY = entityIn.lastTickPosY = y;
//                    entityIn.posZ = entityIn.prevPosZ = entityIn.lastTickPosZ = z;



//            BlockPos destPos = portalTile.getDestPos();
//            if (destPos == null) return;
//            BlockState destState = world.getBlockState(destPos);
//            Direction newDir = destState.get(FACING);
//            float rY = newDir.getHorizontalAngle() - entityIn.getYaw(0);
//            Vec3d offset = new Vec3d(destPos);
//            Vec3d old = entityIn.getPositionVec();
//            Vec3d oldOffset = old.subtract(pos.getX(), pos.getY(), pos.getZ());
//            oldOffset.rotateYaw(rY).scale(-1);
//            entityIn.setPositionAndRotation(offset.getX() + oldOffset.x % 1, offset.getY() + oldOffset.y % 1, offset.getZ() + oldOffset.z % 1, entityIn.getYaw(0) + rY, entityIn.getPitch(0));
                }
            }
        }
        super.onEntityCollision(state, world, pos, entityIn);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        Direction front = state.get(FACING);
        VoxelShape shape = getShapeFromDir(front.getOpposite());
        for (Direction value : Direction.values()) {
            if (value == front || value.getOpposite() == front) {
                continue;
            }
            if (world.getBlockState(pos.offset(value)).getBlock() == ModBlocks.PORTAL) {
                continue;
            }
            shape = VoxelShapes.or(shape, getShapeFromDir(value));
        }
        if (context != null && context.getEntity() != null) {
            shape = VoxelShapes.or(getShape(state, world, pos, null), getShapeFromDir(state.get(FACING)));
        }
        return shape;
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
        return VoxelShapes.or(getShape(state, world, pos, null), getShapeFromDir(state.get(FACING)));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState == state;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileEntity tile = world.getTileEntity(pos);
        ItemStack heldItem = player.getHeldItem(hand);

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof BlockItem && ((BlockItem) heldItem.getItem()).getBlock().getDefaultState().equals(Blocks.STONE.getDefaultState())) {
            if (tile instanceof PortalTileEntity) {
                PortalTileEntity portalTile = (PortalTileEntity) tile;

                BlockPos destPos = portalTile.getDestPos();

                if (destPos != null) {
                    world.setBlockState(destPos, Blocks.STONE.getDefaultState());
                    return true;
                }

            }
        }

        return false;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context)).with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PortalTileEntity();
    }
}
