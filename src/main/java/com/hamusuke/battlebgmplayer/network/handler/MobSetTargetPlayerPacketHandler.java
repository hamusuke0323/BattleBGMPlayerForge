package com.hamusuke.battlebgmplayer.network.handler;

import com.hamusuke.battlebgmplayer.BattleBGMPlayer;
import com.hamusuke.battlebgmplayer.network.packet.s2c.MobSetTargetPlayerS2CPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class MobSetTargetPlayerPacketHandler implements IMessageHandler<MobSetTargetPlayerS2CPacket, IMessage> {
    @Override
    public IMessage onMessage(MobSetTargetPlayerS2CPacket message, MessageContext ctx) {
        BattleBGMPlayer.PROXY.onMessage(message);
        return null;
    }
}
