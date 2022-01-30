package com.hamusuke.battlebgmplayer.client;

import com.google.common.collect.Sets;
import com.hamusuke.battlebgmplayer.BattleBGMPlayer;
import com.hamusuke.battlebgmplayer.client.sound.BattleSound;
import com.hamusuke.battlebgmplayer.client.sounds.BattleSoundManager;
import com.hamusuke.battlebgmplayer.invoker.client.MusicTickerInvoker;
import com.hamusuke.battlebgmplayer.invoker.client.SoundHandlerInvoker;
import com.hamusuke.battlebgmplayer.invoker.client.SoundManagerInvoker;
import com.hamusuke.battlebgmplayer.network.MobSetTargetPlayerS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@SideOnly(Side.CLIENT)
public final class BattleBGMPlayerClient {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final int RESUME_MUSIC_TICKS = 200;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static BattleBGMPlayerClient INSTANCE;
    private static final Random RANDOM = new Random();
    private final Set<EntityLiving> mobs = Sets.newConcurrentHashSet();
    private int tick;
    private final AtomicBoolean started = new AtomicBoolean();
    private final BattleSoundManager battleSoundManager;
    @Nullable
    private ISound currentMusic;
    @Nullable
    private BattleSound currentBattleMusic;
    private int chooseNextTicks;
    private int startResumeMusicTicks;

    private BattleBGMPlayerClient() {
        INSTANCE = this;
        this.battleSoundManager = new BattleSoundManager(Loader.instance().getConfigDir().toPath().resolve(BattleBGMPlayer.MOD_ID));
        this.battleSoundManager.load();
    }

    public void handle(final MobSetTargetPlayerS2CPacket packet) {
        WorldClient level = mc.world;
        if (level != null) {
            Entity entity = level.getEntityByID(packet.getEntityId());
            if (entity instanceof EntityLiving) {
                EntityLiving mob = (EntityLiving) entity;
                if (packet.isStart()) {
                    if (this.mobs.add(mob) && !this.started.get()) {
                        this.play(mob);
                    }
                } else {
                    this.mobs.remove(mob);
                }
            }
        }
    }

    private void play(EntityLiving mob) {
        if (!this.started.get()) {
            if (this.currentBattleMusic != null && this.getSoundEngineInvoker().isStopped(this.currentBattleMusic)) {
                this.currentBattleMusic = null;
            }

            if (this.chooseNextTicks <= 0) {
                if (this.currentBattleMusic != null) {
                    this.getSoundEngineInvoker().stop(this.currentBattleMusic);
                    this.currentBattleMusic.stop();
                }

                this.currentBattleMusic = null;
            }

            boolean battleMusicChanged = false;
            BattleSound previous = this.currentBattleMusic;
            this.currentBattleMusic = this.battleSoundManager.choose(this.currentBattleMusic, mob, mc.player);
            if (!this.currentBattleMusic.equals(previous)) {
                battleMusicChanged = true;
            }

            if (!this.currentBattleMusic.getSoundLocation().equals(SoundHandler.MISSING_SOUND.getSoundLocation()) && ((SoundHandlerInvoker) mc.getSoundHandler()).getSoundRegistry().containsKey(this.currentBattleMusic.getSoundLocation())) {
                this.pauseCurrentMusic();

                if (!battleMusicChanged) {
                    if (this.currentBattleMusic.isVolumeZero()) {
                        this.getSoundEngineInvoker().resume(this.currentBattleMusic);
                    }
                    this.currentBattleMusic.resume();
                } else {
                    mc.getSoundHandler().playSound(this.currentBattleMusic);
                }
                this.started.set(true);
            }
        }
    }

    private void pauseCurrentMusic() {
        this.currentMusic = ((MusicTickerInvoker) mc.getMusicTicker()).getCurrentMusic();
        if (this.currentMusic != null && mc.getSoundHandler().isSoundPlaying(this.currentMusic)) {
            this.getSoundEngineInvoker().pause(this.currentMusic);
        }
    }

    private void stop() {
        if (this.started.get()) {
            this.started.set(false);
            if (this.currentBattleMusic != null) {
                this.currentBattleMusic.pause();
            }
            this.startResumeMusicTicks = RESUME_MUSIC_TICKS;
            this.chooseNextTicks = MathHelper.getInt(RANDOM, 200, 1200);
        }
    }

    private void stopAll() {
        this.stop();

        if (this.currentBattleMusic != null) {
            this.getSoundEngineInvoker().stop(this.currentBattleMusic);
        }

        this.currentBattleMusic = null;
        this.chooseNextTicks = 0;
    }

    private void check() {
        synchronized (this.mobs) {
            this.mobs.removeIf(mob -> !mob.isEntityAlive());
        }

        if (this.isDuringBattle()) {
            this.pauseCurrentMusic();
        }

        if (this.mobs.isEmpty() && this.started.get()) {
            this.stop();
        }
    }

    @SubscribeEvent
    public void onSoundLoad(final SoundLoadEvent event) {
        this.battleSoundManager.load();
    }

    @SubscribeEvent
    public void onPlaySound(final PlaySoundEvent event) {
        if (this.started.get() && event.getSound().getCategory() == SoundCategory.MUSIC) {
            event.setResultSound(null);
        }
    }

    @SubscribeEvent
    public void onLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        this.mobs.clear();
        this.stopAll();
    }

    @SubscribeEvent
    public void onLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        this.mobs.clear();
        this.stopAll();
    }

    @SubscribeEvent
    public void onTickEnd(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (this.tick++ >= 20) {
                this.tick = 0;
                this.check();
            }

            if (this.currentBattleMusic != null && this.currentBattleMusic.isVolumeZero()) {
                this.getSoundEngineInvoker().pause(this.currentBattleMusic);
            }

            if (this.chooseNextTicks > 0) {
                this.chooseNextTicks--;
            }

            if (this.startResumeMusicTicks > 0) {
                this.startResumeMusicTicks--;
                if (this.startResumeMusicTicks <= 0 && this.currentMusic != null) {
                    this.getSoundEngineInvoker().resume(this.currentMusic);
                }
            }
        }
    }

    public boolean isDuringBattle() {
        return this.started.get();
    }

    private SoundManagerInvoker getSoundEngineInvoker() {
        return ((SoundHandlerInvoker) mc.getSoundHandler()).getSoundManagerInvoker();
    }

    public static BattleBGMPlayerClient getInstance() {
        if (INSTANCE == null) {
            new BattleBGMPlayerClient();
        }

        return INSTANCE;
    }
}
