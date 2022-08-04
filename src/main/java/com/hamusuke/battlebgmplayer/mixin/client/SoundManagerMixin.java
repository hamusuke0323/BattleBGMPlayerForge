package com.hamusuke.battlebgmplayer.mixin.client;

import com.hamusuke.battlebgmplayer.invoker.client.SoundManagerInvoker;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import paulscode.sound.SoundSystem;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
@Mixin(SoundManager.class)
public abstract class SoundManagerMixin implements SoundManagerInvoker {
    private static final Logger LOGGER = LogManager.getLogger();

    @Shadow
    private boolean loaded;

    @Shadow
    @Final
    private Map<ISound, String> invPlayingSounds;

    @Shadow
    @Final
    private List<String> pausedChannels;

    @Override
    public final void pause(ISound soundInstance) {
        if (this.loaded) {
            String handle = this.invPlayingSounds.get(soundInstance);
            if (handle != null) {
                this.getSoundSystem().pause(handle);
                this.pausedChannels.add(handle);
            }
        }
    }

    @Override
    public final void resume(ISound soundInstance) {
        if (this.loaded) {
            String handle = this.invPlayingSounds.get(soundInstance);
            if (handle != null) {
                this.getSoundSystem().play(handle);
                this.pausedChannels.remove(handle);
            }
        }
    }

    @Override
    public final SoundSystem getSoundSystem() {
        SoundSystem soundSystem;

        try {
            Field sndSystem = ((SoundManager) (Object) this).getClass().getDeclaredField("field_148620_e");
            sndSystem.setAccessible(true);
            soundSystem = (SoundSystem) sndSystem.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Field sndSystem = ((SoundManager) (Object) this).getClass().getDeclaredField("sndSystem");
                sndSystem.setAccessible(true);
                soundSystem = (SoundSystem) sndSystem.get(this);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                LOGGER.error("Error occurred while getting private field, Minecraft will crash.", ex);
                soundSystem = null;
            }
        }

        return soundSystem;
    }
}
