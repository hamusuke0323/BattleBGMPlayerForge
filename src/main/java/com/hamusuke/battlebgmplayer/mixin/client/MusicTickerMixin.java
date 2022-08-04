package com.hamusuke.battlebgmplayer.mixin.client;

import com.hamusuke.battlebgmplayer.client.BattleBGMPlayerClient;
import com.hamusuke.battlebgmplayer.invoker.client.MusicTickerInvoker;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
@Mixin(MusicTicker.class)
public final class MusicTickerMixin implements MusicTickerInvoker {
    @Shadow
    @Nullable
    private ISound currentMusic;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        BattleBGMPlayerClient client = BattleBGMPlayerClient.getInstance();
        if (client.isDuringBattle() || client.getChooseNextTicks() > 0) {
            ci.cancel();
        }
    }

    @Inject(method = "playMusic", at = @At("HEAD"), cancellable = true)
    private void playMusic(MusicTicker.MusicType requestedMusicType, CallbackInfo ci) {
        BattleBGMPlayerClient client = BattleBGMPlayerClient.getInstance();
        if (!client.isDuringBattle() && client.getChooseNextTicks() <= 0) {
            BattleBGMPlayerClient.getInstance().resetBattleMusic();
        } else {
            ci.cancel();
        }
    }

    @Nullable
    public ISound getCurrentMusic() {
        return this.currentMusic;
    }
}
