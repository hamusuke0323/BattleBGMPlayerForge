package com.hamusuke.battlebgmplayer.invoker.client;

import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.Sound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public interface SoundEventAccessorInvoker {
    default List<ISoundEventAccessor<Sound>> getAccessors() {
        return Collections.emptyList();
    }
}
