package com.hamusuke.battlebgmplayer.proxy;

import com.hamusuke.battlebgmplayer.network.packet.s2c.ContactServerMobS2CPacket;
import com.hamusuke.battlebgmplayer.network.packet.s2c.MobSetTargetPlayerS2CPacket;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(final FMLPreInitializationEvent event) {
    }

    public void onMessage(MobSetTargetPlayerS2CPacket packet) {
    }

    public void onMessage(ContactServerMobS2CPacket packet) {
    }
}
