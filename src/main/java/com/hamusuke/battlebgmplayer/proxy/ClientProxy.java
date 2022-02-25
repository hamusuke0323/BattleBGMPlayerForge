package com.hamusuke.battlebgmplayer.proxy;

import com.hamusuke.battlebgmplayer.client.BattleBGMPlayerClient;
import com.hamusuke.battlebgmplayer.network.MobSetTargetPlayerS2CPacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {
    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(BattleBGMPlayerClient.getInstance());
    }

    @Override
    public void onMessage(MobSetTargetPlayerS2CPacket packet, MessageContext context) {
        BattleBGMPlayerClient.getInstance().handle(packet);
    }
}
