package chickendinner.portalmod.ter;

import chickendinner.portalmod.PortalMod;
import chickendinner.portalmod.block.PortalBlock;
import chickendinner.portalmod.tileentity.PortalTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.PositionTextureVertex;
import net.minecraft.client.renderer.model.TexturedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class PortalTileEntityRenderer extends TileEntityRenderer<PortalTileEntity> {

    private ResourceLocation frontTexture = new ResourceLocation(PortalMod.ID, "textures/block/portal_front.png");
    private ResourceLocation sideTexture = new ResourceLocation(PortalMod.ID, "textures/block/portal_side.png");
    private static final ResourceLocation endTexture = new ResourceLocation("textures/entity/end_portal.png");
    private static final Random random = new Random(31100L);
    private static final FloatBuffer modelViewMatrix = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

    private Map<BitSet, QuadModel> connections = new HashMap<>(); // bitset bits correspond to the sides connected. Model is created as needed.

    private RendererModel debugCube;
    private RendererModel debugVector;
    private RendererModel debugLine;

    public PortalTileEntityRenderer() {
        this.debugCube = new RendererModel(new Model());
        this.debugVector = new RendererModel(new Model());
        this.debugLine = new RendererModel(new Model());

        this.debugCube.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        this.debugVector.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        this.debugLine.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
    }

    @Override
    public void render(PortalTileEntity portalTile, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(portalTile, x, y, z, partialTicks, destroyStage);

        random.setSeed(31100L);
        World world = portalTile.getWorld();

        if (world != null) {

            BlockState state = portalTile.getBlockState();
            BlockPos blockPos = portalTile.getPos();
            Direction front = state.get(PortalBlock.FACING);

            QuadModel model = this.getConnectedQuadModel(world, state, blockPos);

//        GlStateManager.disableTexture();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) x, (float) y, (float) z);
            GlStateManager.translatef(+0.5F, +0.5F, +0.5F);
            GlStateManager.rotatef(front.getHorizontalAngle(), 0.0F, -1.0F, 0.0F);
            GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
            GlStateManager.color3f(1.0F, 1.0F, 1.0F);

            this.bindTexture(this.sideTexture);
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            model.draw(1.0F);


            this.bindTexture(endTexture);
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
            GlStateManager.disableLighting();
            GlStateManager.getMatrix(GL_MODELVIEW_MATRIX, modelViewMatrix);
            GlStateManager.getMatrix(GL_PROJECTION_MATRIX, projectionMatrix);
            double d0 = x * x + y * y + z * z;
            int i = this.getPasses(d0);
            boolean flag = false;
            GameRenderer gamerenderer = Minecraft.getInstance().gameRenderer;

            for (int j = 0; j < i; ++j) {
                GlStateManager.pushMatrix();
                float f1 = 2.0F / (float) (18 - j);
                if (j == 0) {
//                    this.bindTexture(END_SKY_TEXTURE);
                    f1 = 0.15F;
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                }

                if (j >= 1) {
                    this.bindTexture(endTexture);
                    flag = true;
                    gamerenderer.setupFogColor(true);
                }

                if (j == 1) {
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                }

                GlStateManager.texGenMode(GlStateManager.TexGen.S, GL_EYE_LINEAR);
                GlStateManager.texGenMode(GlStateManager.TexGen.T, GL_EYE_LINEAR);
                GlStateManager.texGenMode(GlStateManager.TexGen.R, GL_EYE_LINEAR);
                GlStateManager.texGenParam(GlStateManager.TexGen.S, GL_EYE_PLANE, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGenParam(GlStateManager.TexGen.T, GL_EYE_PLANE, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GlStateManager.texGenParam(GlStateManager.TexGen.R, GL_EYE_PLANE, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                GlStateManager.enableTexGen(GlStateManager.TexGen.S);
                GlStateManager.enableTexGen(GlStateManager.TexGen.T);
                GlStateManager.enableTexGen(GlStateManager.TexGen.R);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(GL_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                GlStateManager.translatef(0.5F, 0.5F, 0.0F);
                GlStateManager.scalef(0.5F, 0.5F, 1.0F);
                float f2 = (float) (j + 1);
                GlStateManager.translatef(17.0F / f2, (2.0F + f2 / 1.5F) * ((float) (Util.milliTime() % 800000L) / 800000.0F), 0.0F);
                GlStateManager.rotatef((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.scalef(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);
                GlStateManager.multMatrix(projectionMatrix);
                GlStateManager.multMatrix(modelViewMatrix);

                float f3 = (random.nextFloat() * 0.5F + 0.1F) * f1;
                float f4 = (random.nextFloat() * 0.5F + 0.4F) * f1;
                float f5 = (random.nextFloat() * 0.5F + 0.5F) * f1;
                GlStateManager.color3f(f3, f4, f5);
                model.draw(1.0F);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
//                this.bindTexture(END_SKY_TEXTURE);
            }

            GlStateManager.disableBlend();
            GlStateManager.disableTexGen(GlStateManager.TexGen.S);
            GlStateManager.disableTexGen(GlStateManager.TexGen.T);
            GlStateManager.disableTexGen(GlStateManager.TexGen.R);
            GlStateManager.enableLighting();
            if (flag) {
                gamerenderer.setupFogColor(false);
            }

//            model.draw(1.0F);

            GlStateManager.enableLighting();
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.popMatrix();

//        GlStateManager.enableTexture();


            if (portalTile.isLinked()) {
                ClientPlayerEntity player = Minecraft.getInstance().player;
                Vec3d eyePosition = player.getEyePosition(partialTicks);
                Vec3d lookVector = player.getLook(partialTicks);
                Vec3d transformedEyePosition = portalTile.getTransformedPoint(eyePosition);
                Vec3d transformedLookVector = portalTile.getTransformedVector(lookVector);

                float yaw = (float) Math.toDegrees(Math.atan2(transformedLookVector.getX(), transformedLookVector.getZ()));
                float pitch = (float) Math.toDegrees(Math.asin(-transformedLookVector.getY()));

                float ox = (float) (transformedEyePosition.getX() - eyePosition.getX());
                float oy = (float) (transformedEyePosition.getY() - eyePosition.getY());
                float oz = (float) (transformedEyePosition.getZ() - eyePosition.getZ());

                float r0 = 0.125F;
                float r1 = 0.25F;
                float l1 = 2.0F;

                GlStateManager.disableTexture();

                GlStateManager.disableDepthTest();
                glBegin(GL_LINES);
                glVertex3f((float) (x + 0.5F), (float) (y + 0.5F), (float) (z + 0.5F));
                glVertex3f(ox, oy, oz);
                glEnd();
                GlStateManager.enableDepthTest();

//            GlStateManager.disableDepthTest();
                GlStateManager.pushMatrix();
                GlStateManager.translatef(ox, oy, oz);
                GlStateManager.rotatef(yaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(pitch, 1.0F, 0.0F, 0.0F);
                GlStateManager.translatef(-r0, -r0, -r0);
                GlStateManager.scalef(2.0F * r0, 2.0F * r0, 2.0F * r0);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.debugCube.render(1.0F);

                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.5F - r1 * 0.5F, 0.5F - r1 * 0.5F, 0.5F);
                GlStateManager.scalef(r1, r1, l1);
                GlStateManager.color4f(1.0F, 0.0F, 0.0F, 1.0F);
                this.debugVector.render(1.0F);
                GlStateManager.popMatrix();

                GlStateManager.popMatrix();

//            GlStateManager.enableDepthTest();


                GlStateManager.disableDepthTest();
                GlStateManager.pushMatrix();
                GlStateManager.translatef(
                        (float) (portalTile.getSurface().getOrigin().getX() - eyePosition.getX() + 0.5F),
                        (float) (portalTile.getSurface().getOrigin().getY() - eyePosition.getY() + 0.5F),
                        (float) (portalTile.getSurface().getOrigin().getZ() - eyePosition.getZ() + 0.5F)
                );
                GlStateManager.translatef(-r0, -r0, -r0);
                GlStateManager.scalef(2.0F * r0, 2.0F * r0, 2.0F * r0);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.debugCube.render(1.0F);

                GlStateManager.popMatrix();
                GlStateManager.enableDepthTest();


                GlStateManager.enableTexture();
            }
        }
    }

    protected int getPasses(double distanceSq) {
        int i;
        if (distanceSq > 36864.0D) {
            i = 1;
        } else if (distanceSq > 25600.0D) {
            i = 3;
        } else if (distanceSq > 16384.0D) {
            i = 5;
        } else if (distanceSq > 9216.0D) {
            i = 7;
        } else if (distanceSq > 4096.0D) {
            i = 9;
        } else if (distanceSq > 1024.0D) {
            i = 11;
        } else if (distanceSq > 576.0D) {
            i = 13;
        } else if (distanceSq > 256.0D) {
            i = 14;
        } else {
            i = 15;
        }

        return i;
    }

    protected float getOffset() {
        return 0.9F;
    }

    private FloatBuffer getBuffer(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
        this.buffer.clear();
        this.buffer.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
        this.buffer.flip();
        return this.buffer;
    }

    private QuadModel getConnectedQuadModel(World world, BlockState blockState, BlockPos blockPos) {
        Direction front = blockState.get(PortalBlock.FACING);

        BitSet key = new BitSet(6);
        key.set(0, !blockState.isSideInvisible(world.getBlockState(blockPos.down()), Direction.DOWN));
        key.set(1, !blockState.isSideInvisible(world.getBlockState(blockPos.up()), Direction.UP));
        key.set(2, true);
        key.set(3, false);
        key.set(4, !blockState.isSideInvisible(world.getBlockState(blockPos.offset(front.rotateY())), front.rotateY()));
        key.set(5, !blockState.isSideInvisible(world.getBlockState(blockPos.offset(front.rotateYCCW())), front.rotateYCCW()));

        return connections.computeIfAbsent(key, bits -> {
            boolean[] sides = new boolean[6];
            for (int i = 0; i < 6; i++)
                sides[i] = bits.get(i);

            QuadModel model = new QuadModel();
            createCubeQuads(model.quadList, null, sides, 0, 0, 0.0F, 0.0F, 0.0F, 1, 1, 1, 1.0F, 1.0F);
            return model;
        });
    }

    public void createCubeQuads(List<TexturedQuad> quadList, boolean[] inside, boolean[] outside, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight) {
        float x1 = x + (float) dx;
        float y1 = y + (float) dy;
        float z1 = z + (float) dz;

        PositionTextureVertex v000 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        PositionTextureVertex v001 = new PositionTextureVertex(x, y, z1, 0.0F, 0.0F);
        PositionTextureVertex v010 = new PositionTextureVertex(x, y1, z, 0.0F, 0.0F);
        PositionTextureVertex v011 = new PositionTextureVertex(x, y1, z1, 0.0F, 0.0F);
        PositionTextureVertex v100 = new PositionTextureVertex(x1, y, z, 0.0F, 0.0F);
        PositionTextureVertex v101 = new PositionTextureVertex(x1, y, z1, 0.0F, 0.0F);
        PositionTextureVertex v110 = new PositionTextureVertex(x1, y1, z, 0.0F, 0.0F);
        PositionTextureVertex v111 = new PositionTextureVertex(x1, y1, z1, 0.0F, 0.0F);

        boolean insideQuad, outsideQuad;

        // y-min (down)
        insideQuad = inside != null && Direction.DOWN.getIndex() < inside.length && inside[Direction.DOWN.getIndex()];
        outsideQuad = outside != null && Direction.DOWN.getIndex() < outside.length && outside[Direction.DOWN.getIndex()];
        this.createQuad(quadList, insideQuad, outsideQuad, v101, v001, v000, v100, texU + dz, texV, texU + dz + dx, texV + dz, textureWidth, textureHeight);

        // y-max (up)
        insideQuad = inside != null && Direction.UP.getIndex() < inside.length && inside[Direction.UP.getIndex()];
        outsideQuad = outside != null && Direction.UP.getIndex() < outside.length && outside[Direction.UP.getIndex()];
        this.createQuad(quadList, insideQuad, outsideQuad, v110, v010, v011, v111, texU + dz + dx, texV + dz, texU + dz + dx + dx, texV, textureWidth, textureHeight);

        // z-min (north)
        insideQuad = inside != null && Direction.NORTH.getIndex() < inside.length && inside[Direction.NORTH.getIndex()];
        outsideQuad = outside != null && Direction.NORTH.getIndex() < outside.length && outside[Direction.NORTH.getIndex()];
        this.createQuad(quadList, insideQuad, outsideQuad, v100, v000, v010, v110, texU + dz, texV + dz, texU + dz + dx, texV + dz + dy, textureWidth, textureHeight);

        // z-max (south)
        insideQuad = inside != null && Direction.SOUTH.getIndex() < inside.length && inside[Direction.SOUTH.getIndex()];
        outsideQuad = outside != null && Direction.SOUTH.getIndex() < outside.length && outside[Direction.SOUTH.getIndex()];
        this.createQuad(quadList, insideQuad, outsideQuad, v001, v101, v111, v011, texU + dz + dx + dz, texV + dz, texU + dz + dx + dz + dx, texV + dz + dy, textureWidth, textureHeight);

        // x-min (west)
        insideQuad = inside != null && Direction.WEST.getIndex() < inside.length && inside[Direction.WEST.getIndex()];
        outsideQuad = outside != null && Direction.WEST.getIndex() < outside.length && outside[Direction.WEST.getIndex()];
        this.createQuad(quadList, insideQuad, outsideQuad, v000, v001, v011, v010, texU, texV + dz, texU + dz, texV + dz + dy, textureWidth, textureHeight);

        // x-max (east)
        insideQuad = inside != null && Direction.EAST.getIndex() < inside.length && inside[Direction.EAST.getIndex()];
        outsideQuad = outside != null && Direction.EAST.getIndex() < outside.length && outside[Direction.EAST.getIndex()];
        this.createQuad(quadList, insideQuad, outsideQuad, v101, v100, v110, v111, texU + dz + dx, texV + dz, texU + dz + dx + dz, texV + dz + dy, textureWidth, textureHeight);
    }

    public void createQuad(List<TexturedQuad> quadList, boolean inside, boolean outside, PositionTextureVertex vert0, PositionTextureVertex vert1, PositionTextureVertex vert2, PositionTextureVertex vert3, int u0, int v0, int u1, int v1, float textureWidth, float textureHeight) {
        if (outside)
            quadList.add(new TexturedQuad(new PositionTextureVertex[]{vert0, vert1, vert2, vert3}, u0, v0, u1, v1, textureWidth, textureHeight));

        if (inside)
            quadList.add(new TexturedQuad(new PositionTextureVertex[]{vert3, vert2, vert1, vert0}, u0, v0, u1, v1, textureWidth, textureHeight));
    }

    private static class QuadModel {
        public List<TexturedQuad> quadList = new ArrayList<>();
        public int displayList;
        public boolean compiled;

        public void draw(float scale) {
            if (!this.compiled) {
                this.compiled = true;
                this.compileDisplayList(scale);
            }

            GlStateManager.callList(this.displayList);
        }

        private void compileDisplayList(float scale) {
            GlStateManager.deleteLists(this.displayList, 1);
            this.displayList = GLAllocation.generateDisplayLists(1);
            GlStateManager.newList(this.displayList, GL_COMPILE);
            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

            for (TexturedQuad quad : this.quadList) {
                quad.draw(bufferbuilder, scale);
            }

            GlStateManager.endList();
            this.compiled = true;
        }
    }
}
