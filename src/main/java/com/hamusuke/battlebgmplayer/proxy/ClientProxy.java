package com.hamusuke.battlebgmplayer.proxy;

import com.hamusuke.battlebgmplayer.client.BattleBGMPlayerClient;
import com.hamusuke.battlebgmplayer.client.gui.DebugScreen;
import com.hamusuke.battlebgmplayer.network.packet.s2c.ContactServerMobS2CPacket;
import com.hamusuke.battlebgmplayer.network.packet.s2c.MobSetTargetPlayerS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {
    private static final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(BattleBGMPlayerClient.getInstance());
    }

    @Override
    public void onMessage(MobSetTargetPlayerS2CPacket packet) {
        BattleBGMPlayerClient.getInstance().handle(packet);
    }

    @Override
    public void onMessage(ContactServerMobS2CPacket packet) {
        if (mc.currentScreen instanceof DebugScreen) {
            ((DebugScreen) mc.currentScreen).accept(packet);
        }
    }
}
