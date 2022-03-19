package com.hamusuke.battlebgmplayer.network.handler;

import com.hamusuke.battlebgmplayer.invoker.EntityLivingInvoker;
import com.hamusuke.battlebgmplayer.network.packet.c2s.ContactServerMobC2SPacket;
import com.hamusuke.battlebgmplayer.network.packet.s2c.ContactServerMobS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class ContactServerMobC2SPacketHandler implements IMessageHandler<ContactServerMobC2SPacket, ContactServerMobS2CPacket> {
    @Override
    public ContactServerMobS2CPacket onMessage(ContactServerMobC2SPacket message, MessageContext ctx) {
        int id = message.getMobId();
        Entity serverEntity = ctx.getServerHandler().player.world.getEntityByID(id);
        if (serverEntity instanceof EntityLiving) {
            EntityLiving entityLiving = (EntityLiving) serverEntity;
            EntityLivingBase attackTarget = entityLiving.getAttackTarget();
            EntityPlayerMP targetedPlayer = ((EntityLivingInvoker) entityLiving).getCurrentTargetedPlayer();

            return new ContactServerMobS2CPacket(id, attackTarget == null ? "null" : attackTarget.toString(), targetedPlayer == null ? "null" : targetedPlayer.toString());
        }

        return null;
    }
}
