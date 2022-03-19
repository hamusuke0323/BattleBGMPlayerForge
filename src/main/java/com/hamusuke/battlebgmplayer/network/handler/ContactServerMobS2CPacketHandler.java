package com.hamusuke.battlebgmplayer.network.handler;

import com.hamusuke.battlebgmplayer.BattleBGMPlayer;
import com.hamusuke.battlebgmplayer.network.packet.s2c.ContactServerMobS2CPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class ContactServerMobS2CPacketHandler implements IMessageHandler<ContactServerMobS2CPacket, IMessage> {
    @Override
    public IMessage onMessage(ContactServerMobS2CPacket message, MessageContext ctx) {
        BattleBGMPlayer.PROXY.onMessage(message);
        return null;
    }
}
