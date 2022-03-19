package com.hamusuke.battlebgmplayer.network.packet.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class ContactServerMobS2CPacket implements IMessage {
    private int id;
    private String attackTargetInfo;
    private String currentTargetedPlayerInfo;

    public ContactServerMobS2CPacket() {
    }

    public ContactServerMobS2CPacket(int id, String attackTargetInfo, String currentTargetedPlayerInfo) {
        this.id = id;
        this.attackTargetInfo = attackTargetInfo;
        this.currentTargetedPlayerInfo = currentTargetedPlayerInfo;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.attackTargetInfo = ByteBufUtils.readUTF8String(buf);
        this.currentTargetedPlayerInfo = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.attackTargetInfo);
        ByteBufUtils.writeUTF8String(buf, this.currentTargetedPlayerInfo);
    }

    public int getEntityId() {
        return this.id;
    }

    public String getAttackTargetInfo() {
        return this.attackTargetInfo;
    }

    public String getCurrentTargetedPlayerInfo() {
        return this.currentTargetedPlayerInfo;
    }
}
