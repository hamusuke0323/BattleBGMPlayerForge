package com.hamusuke.battlebgmplayer.network;

import com.hamusuke.battlebgmplayer.BattleBGMPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class MobSetTargetPlayerPacketHandler implements IMessageHandler<MobSetTargetPlayerS2CPacket, IMessage> {
    @Override
    public IMessage onMessage(MobSetTargetPlayerS2CPacket message, MessageContext ctx) {
        return BattleBGMPlayer.PROXY.onMessage(message, ctx);
    }
}
