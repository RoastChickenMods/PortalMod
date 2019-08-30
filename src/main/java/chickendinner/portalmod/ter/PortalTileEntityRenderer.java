package chickendinner.portalmod.ter;

import chickendinner.portalmod.tileentity.PortalTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.*;

public class PortalTileEntityRenderer extends TileEntityRenderer<PortalTileEntity> {

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

        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock()
    }

    @Override
    public void render(PortalTileEntity portalTile, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(portalTile, x, y, z, partialTicks, destroyStage);

        if (portalTile.isLinked()) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            Vec3d eyePosition = player.getEyePosition(partialTicks);
            Vec3d lookVector = player.getLook(partialTicks);
            Vec3d transformedEyePosition = portalTile.getTransformedPoint(eyePosition);
            Vec3d transformedLookVector = portalTile.getTransformedVector(lookVector);

            float yaw = (float) Math.toDegrees(Math.atan2(transformedLookVector.getX(), transformedLookVector.getZ()));
            float pitch = (float) Math.toDegrees(Math.asin(-transformedLookVector.getY()));

//            float yaw = (float) (player.getYaw(partialTicks));
//            float pitch = (float) (player.getPitch(partialTicks));

            float px = portalTile.getPos().getX() + 0.5F;
            float py = portalTile.getPos().getY() + 0.5F;
            float pz = portalTile.getPos().getZ() + 0.5F;

//            System.out.println(String.format("%f, %f, %f", x, y, z));
            float ox = (float) (/*x + 0.5F + */(transformedEyePosition.getX() - eyePosition.getX()));
            float oy = (float) (/*y + 0.5F + */(transformedEyePosition.getY() - eyePosition.getY()));
            float oz = (float) (/*z + 0.5F + */(transformedEyePosition.getZ() - eyePosition.getZ()));

//            System.out.println(String.format("Rendering translated eye position: [%f, %f, %f] to [%f, %f, %f]", eyePosition.x, eyePosition.y, eyePosition.z, ox, oy, oz));

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
                    (float)(portalTile.getSurface().getOrigin().getX() - eyePosition.getX() + 0.5F),
                    (float)(portalTile.getSurface().getOrigin().getY() - eyePosition.getY() + 0.5F),
                    (float)(portalTile.getSurface().getOrigin().getZ() - eyePosition.getZ() + 0.5F)
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
