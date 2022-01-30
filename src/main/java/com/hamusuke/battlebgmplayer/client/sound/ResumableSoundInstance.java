package com.hamusuke.battlebgmplayer.client.sound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ResumableSoundInstance {
    void stop();

    void pause();

    void resume();
}
