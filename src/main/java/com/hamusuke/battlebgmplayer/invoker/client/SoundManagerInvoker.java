package com.hamusuke.battlebgmplayer.invoker.client;

import net.minecraft.client.audio.ISound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public interface SoundManagerInvoker {
    SoundSystem getSoundSystem();

    default void stop(ISound soundInstance) {
    }

    default void pause(ISound soundInstance) {
    }

    default void resume(ISound soundInstance) {
    }

    default boolean isStopped(@NotNull ISound soundInstance) {
        return false;
    }
}
