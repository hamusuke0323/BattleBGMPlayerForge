package com.hamusuke.battlebgmplayer.proxy;

import com.hamusuke.battlebgmplayer.network.MobSetTargetPlayerS2CPacket;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
    public void preInit(final FMLPreInitializationEvent event) {
    }

    public void onMessage(MobSetTargetPlayerS2CPacket packet, MessageContext context) {
    }
}
