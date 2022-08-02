package com.hamusuke.battlebgmplayer.client.sound;

import com.hamusuke.battlebgmplayer.invoker.client.SoundEventAccessorInvoker;
import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class BattleSound extends PositionedSound implements ITickableSound, ResumableSoundInstance {
    protected final UUID uuid = UUID.randomUUID();
    protected boolean stopped;
    protected boolean pausing;
    protected int fade = 40;
    protected boolean paused;
    @Nullable
    protected final ISound previousSound;

    public BattleSound(ResourceLocation p_119587_, @Nullable ISound previousSound) {
        super(p_119587_, SoundCategory.MUSIC);
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.previousSound = previousSound;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    @Override
    public void pause() {
        this.pausing = true;
    }

    @Override
    public void resume() {
        this.pausing = false;
        this.paused = false;
    }

    @Override
    public boolean isDonePlaying() {
        return this.stopped;
    }

    @Override
    public void update() {
        if (this.pausing && this.fade > 0) {
            this.fade--;
            this.volume = MathHelper.clamp(this.fade * 0.025F, 0.0F, 1.0F);
        } else if (!this.pausing && this.fade < 40) {
            this.fade++;
            this.volume = MathHelper.clamp(this.fade * 0.025F, 0.0F, 1.0F);
        }
    }

    public boolean isVolumeZero() {
        return this.pausing && this.fade <= 0;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public SoundEventAccessor createAccessor(SoundHandler handler) {
        SoundEventAccessor accessor = super.createAccessor(handler);
        if (this.previousSound != null && accessor != null) {
            SoundEventAccessorInvoker invoker = (SoundEventAccessorInvoker) accessor;
            List<ISoundEventAccessor<Sound>> accessors = invoker.getAccessors();
            if (!accessors.isEmpty()) {
                int index = -1;

                for (int i = 0; i < accessors.size(); i++) {
                    ISoundEventAccessor<Sound> accessor1 = accessors.get(i);
                    if (accessor1.cloneEntry().getSoundLocation().equals(this.previousSound.getSound().getSoundLocation())) {
                        index = i;
                    }
                }

                int j = accessors.size();
                if (index >= 0) {
                    this.sound = index + 1 >= j ? accessors.get(0).cloneEntry() : accessors.get(index + 1).cloneEntry();
                }
            }
        }

        return accessor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BattleSound that = (BattleSound) o;
        return this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }
}
