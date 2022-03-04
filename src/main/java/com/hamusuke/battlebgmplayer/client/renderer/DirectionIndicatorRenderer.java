package com.hamusuke.battlebgmplayer.client.renderer;

import com.hamusuke.battlebgmplayer.BattleBGMPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DirectionIndicatorRenderer {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation ARROW = new ResourceLocation(BattleBGMPlayer.MOD_ID, "textures/arrow.png");
    private static final float radToDeg = 180.0F / (float) Math.PI;
    private static final float degToRad = (float) Math.PI / 180.0F;

    public static void render(RenderGameOverlayEvent event, EntityLiving mob) {
        Entity entity = mc.getRenderManager().renderViewEntity;

        if (entity != null) {
            double scaledWidth = event.getResolution().getScaledWidth_double();
            double scaledHeight = event.getResolution().getScaledHeight_double();
            double scaledWidthHalf = scaledWidth / 2.0D;
            double scaledHeightHalf = scaledHeight / 2.0D;
            double mobX = mob.posX;
            double mobZ = mob.posZ;
            double x = entity.posX - mobX;
            double z = entity.posZ - mobZ;
            float phi = (float) MathHelper.atan2(x, z);
            double xz = Math.sqrt(x * x + z * z);
            double scaledDistance = Math.sqrt(Math.pow(scaledWidthHalf, 2.0D) + Math.pow(scaledHeightHalf, 2.0D));
            xz += scaledDistance;

            float angleRad = -phi - entity.rotationYaw * degToRad - (float) Math.PI;
            double displayX = MathHelper.clamp(MathHelper.sin(angleRad) * xz + scaledWidthHalf, 8.0D, scaledWidth - 8.0D);
            double displayY = MathHelper.clamp(-MathHelper.cos(angleRad) * xz + scaledHeightHalf, 8.0D, scaledHeight - 8.0D);

            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.translate(displayX, displayY, -90.0D);

            float rotateAngle = MathHelper.wrapDegrees(angleRad * radToDeg);

            GlStateManager.rotate(rotateAngle, 0.0F, 0.0F, 1.0F);

            if ((rotateAngle >= 45.0F && rotateAngle <= 180.0F) || (rotateAngle <= -45.0F && rotateAngle >= -180.0F)) {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                mc.getTextureManager().bindTexture(ARROW);
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferBuilder.pos(-8.0D, 8.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
                bufferBuilder.pos(8.0D, 8.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
                bufferBuilder.pos(8.0D, -8.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
                bufferBuilder.pos(-8.0D, -8.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
                tessellator.draw();
            }

            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();

            mc.getTextureManager().bindTexture(Gui.ICONS);
        }
    }
}
