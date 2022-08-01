package com.hamusuke.battlebgmplayer.mixin.client;

import net.minecraft.client.audio.MusicTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MusicTicker.MusicType.class)
public final class MusicTypeMixin {
    @Inject(method = "getMinDelay", at = @At("RETURN"), cancellable = true)
    private void getMinDelay(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == MusicTicker.MusicType.GAME) {
            cir.setReturnValue(600);
            cir.cancel();
        }
    }

    @Inject(method = "getMaxDelay", at = @At("RETURN"), cancellable = true)
    private void getMaxDelay(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == MusicTicker.MusicType.GAME) {
            cir.setReturnValue(1200);
            cir.cancel();
        }
    }
}
