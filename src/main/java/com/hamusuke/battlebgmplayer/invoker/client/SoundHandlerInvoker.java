package com.hamusuke.battlebgmplayer.invoker.client;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface SoundHandlerInvoker {
    SoundManager getSoundManager();

    SoundRegistry getSoundRegistry();

    default SoundManagerInvoker getSoundManagerInvoker() {
        return (SoundManagerInvoker) this.getSoundManager();
    }
}
