package com.hamusuke.battlebgmplayer.invoker;

import net.minecraft.entity.player.EntityPlayerMP;

public interface EntityLivingInvoker {
    default EntityPlayerMP getCurrentTargetedPlayer() {
        return null;
    }
}
