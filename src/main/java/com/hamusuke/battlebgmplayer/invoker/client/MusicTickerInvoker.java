package com.hamusuke.battlebgmplayer.invoker.client;

import net.minecraft.client.audio.ISound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public interface MusicTickerInvoker {
    @Nullable
    ISound getCurrentMusic();
}
