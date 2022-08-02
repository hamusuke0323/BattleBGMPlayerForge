package com.hamusuke.battlebgmplayer.mixin.client;

import com.hamusuke.battlebgmplayer.invoker.client.SoundEventAccessorInvoker;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@SideOnly(Side.CLIENT)
@Mixin(SoundEventAccessor.class)
public final class SoundEventAccessorMixin implements SoundEventAccessorInvoker {
    @Shadow
    @Final
    private List<ISoundEventAccessor<Sound>> accessorList;

    @Override
    public List<ISoundEventAccessor<Sound>> getAccessors() {
        return this.accessorList;
    }
}
