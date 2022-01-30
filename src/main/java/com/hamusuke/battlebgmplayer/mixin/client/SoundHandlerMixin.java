package com.hamusuke.battlebgmplayer.mixin.client;

import com.hamusuke.battlebgmplayer.invoker.client.SoundHandlerInvoker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SideOnly(Side.CLIENT)
@Mixin(SoundHandler.class)
public final class SoundHandlerMixin implements SoundHandlerInvoker {
    @Shadow
    @Final
    private SoundManager sndManager;

    @Shadow
    @Final
    private SoundRegistry soundRegistry;

    @Override
    public SoundRegistry getSoundRegistry() {
        return this.soundRegistry;
    }

    @Override
    public SoundManager getSoundManager() {
        return this.sndManager;
    }
}
