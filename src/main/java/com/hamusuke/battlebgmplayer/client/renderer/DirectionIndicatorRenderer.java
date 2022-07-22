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
    private static final float toDeg = 180.0F / (float) Math.PI;
    private static final float toRad = (float) Math.PI / 180.0F;

    public static void render(RenderGameOverlayEvent event, EntityLiving mob) {
        Entity entity = mc.getRenderManager().renderViewEntity;

        if (entity != null) {
            double scaledWidth = event.getResolution().getScaledWidth_double();
            double scaledHeight = event.getResolution().getScaledHeight_double();
            double scaledWidthHalf = scaledWidth / 2.0D;
            double scaledHeightHalf = scaledHeight / 2.0D;
            float delta = event.getPartialTicks();
            double x = lerp(delta, entity.prevPosX, entity.posX) - lerp(delta, mob.prevPosX, mob.posX);
            double z = lerp(delta, entity.prevPosZ, entity.posZ) - lerp(delta, mob.prevPosZ, mob.posZ);
            float phi = (float) MathHelper.atan2(x, z);
            double xz = Math.sqrt(x * x + z * z);
            double d = Math.sqrt(Math.pow(scaledWidthHalf / 3.0D, 2.0D) + Math.pow(scaledHeightHalf / 3.0D, 2.0D));
            float angleRad = -phi - entity.rotationYaw * toRad - (float) Math.PI;
            GlStateManager.pushMatrix();
            GlStateManager.disableBlend();
            GlStateManager.translate(MathHelper.clamp((MathHelper.sin(angleRad) * (xz + d) + scaledWidthHalf), 8.0D, scaledWidth - 8.0D), MathHelper.clamp(-MathHelper.cos(angleRad) * (xz + d) + scaledHeightHalf, 8.0D, scaledHeight - 8.0D), -85.0D);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float rotateAngle = MathHelper.wrapDegrees(angleRad * toDeg);
            GlStateManager.rotate(rotateAngle, 0.0F, 0.0F, 1.0F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            mc.getTextureManager().bindTexture(ARROW);
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(-8.0D, 8.0D, 0.0D).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            bufferBuilder.pos(8.0D, 8.0D, 0.0D).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            bufferBuilder.pos(8.0D, -8.0D, 0.0D).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            bufferBuilder.pos(-8.0D, -8.0D, 0.0D).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            tessellator.draw();
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
            mc.getTextureManager().bindTexture(Gui.ICONS);
        }
    }

    private static double lerp(float delta, double start, double end) {
        return start + (end - start) * delta;
    }
}
