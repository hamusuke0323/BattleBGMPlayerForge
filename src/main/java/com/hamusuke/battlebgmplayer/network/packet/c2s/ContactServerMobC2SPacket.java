package com.hamusuke.battlebgmplayer.network.packet.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class ContactServerMobC2SPacket implements IMessage {
    private int mobId;

    public ContactServerMobC2SPacket() {
    }

    public ContactServerMobC2SPacket(EntityLiving clientMob) {
        this.mobId = clientMob.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.mobId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.mobId);
    }

    public int getMobId() {
        return this.mobId;
    }
}
