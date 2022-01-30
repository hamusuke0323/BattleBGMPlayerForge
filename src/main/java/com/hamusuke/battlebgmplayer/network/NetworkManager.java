package com.hamusuke.battlebgmplayer.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class NetworkManager {
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("bbp.ntf2c");

    public static void init() {
        INSTANCE.registerMessage(MobSetTargetPlayerPacketHandler.class, MobSetTargetPlayerS2CPacket.class, 0, Side.CLIENT);
    }

    public static void sendToClient(IMessage packet, EntityPlayerMP serverPlayer) {
        INSTANCE.sendTo(packet, serverPlayer);
    }
}
