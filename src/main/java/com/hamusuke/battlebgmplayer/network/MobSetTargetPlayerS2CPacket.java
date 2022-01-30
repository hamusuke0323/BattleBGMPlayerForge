package com.hamusuke.battlebgmplayer.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class MobSetTargetPlayerS2CPacket implements IMessage {
    private boolean start;
    private int entityId;

    public MobSetTargetPlayerS2CPacket(boolean start, EntityLiving mob) {
        this.start = start;
        this.entityId = mob.getEntityId();
    }

    public MobSetTargetPlayerS2CPacket() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.start = buf.readBoolean();
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.start);
        buf.writeInt(this.entityId);
    }

    public boolean isStart() {
        return this.start;
    }

    public int getEntityId() {
        return this.entityId;
    }
}
