package com.hamusuke.battlebgmplayer.network;

import com.hamusuke.battlebgmplayer.network.handler.ContactServerMobC2SPacketHandler;
import com.hamusuke.battlebgmplayer.network.handler.ContactServerMobS2CPacketHandler;
import com.hamusuke.battlebgmplayer.network.handler.MobSetTargetPlayerPacketHandler;
import com.hamusuke.battlebgmplayer.network.packet.c2s.ContactServerMobC2SPacket;
import com.hamusuke.battlebgmplayer.network.packet.s2c.ContactServerMobS2CPacket;
import com.hamusuke.battlebgmplayer.network.packet.s2c.MobSetTargetPlayerS2CPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class NetworkManager {
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("bbp.ntf2c");

    public static void init() {
        INSTANCE.registerMessage(MobSetTargetPlayerPacketHandler.class, MobSetTargetPlayerS2CPacket.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(ContactServerMobC2SPacketHandler.class, ContactServerMobC2SPacket.class, 1, Side.SERVER);
        INSTANCE.registerMessage(ContactServerMobS2CPacketHandler.class, ContactServerMobS2CPacket.class, 2, Side.CLIENT);
    }

    public static void sendToClient(IMessage packet, EntityPlayerMP serverPlayer) {
        INSTANCE.sendTo(packet, serverPlayer);
    }

    public static void sendToServer(IMessage packet) {
        INSTANCE.sendToServer(packet);
    }
}
