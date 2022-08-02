package com.hamusuke.battlebgmplayer.client;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.hamusuke.battlebgmplayer.BattleBGMPlayer;
import com.hamusuke.battlebgmplayer.client.renderer.DirectionIndicatorRenderer;
import com.hamusuke.battlebgmplayer.client.sound.BattleSound;
import com.hamusuke.battlebgmplayer.client.sounds.BattleSoundManager;
import com.hamusuke.battlebgmplayer.invoker.client.MusicTickerInvoker;
import com.hamusuke.battlebgmplayer.invoker.client.SoundHandlerInvoker;
import com.hamusuke.battlebgmplayer.invoker.client.SoundManagerInvoker;
import com.hamusuke.battlebgmplayer.network.packet.s2c.MobSetTargetPlayerS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@SideOnly(Side.CLIENT)
public final class BattleBGMPlayerClient {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static BattleBGMPlayerClient INSTANCE;
    private static final Random RANDOM = new Random();
    private final Set<EntityLiving> clientMobs = Sets.newConcurrentHashSet();
    private final AtomicBoolean started = new AtomicBoolean();
    private final BattleSoundManager battleSoundManager;
    @Nullable
    private EntityLiving recentMob;
    @Nullable
    private BattleSound currentBattleMusic;
    private int chooseNextTicks;
    private int tickCount;

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
                    if (this.clientMobs.add(mob)) {
                        this.recentMob = mob;

                        if (!this.isDuringBattle()) {
                            this.play(this.recentMob);
                        }
                    }
                } else {
                    this.clientMobs.remove(mob);
                    this.stopIfPlayerIsNotTargeted();
                }
            }
        }
    }

    private boolean shouldPlayBattleMusic() {
        return this.isDuringBattle() && (this.currentBattleMusic == null || !mc.getSoundHandler().isSoundPlaying(this.currentBattleMusic));
    }

    private void stopIfPlayerIsNotTargeted() {
        if (this.clientMobs.isEmpty() || !this.isDuringBattle()) {
            this.stop();
        }
    }

    private void play(EntityLiving mob) {
        if (this.currentBattleMusic != null && !mc.getSoundHandler().isSoundPlaying(this.currentBattleMusic)) {
            this.currentBattleMusic = null;
        }

        boolean battleMusicChanged = false;
        BattleSound previous = this.currentBattleMusic;
        this.currentBattleMusic = this.battleSoundManager.choose(this.currentBattleMusic, mob, mc.player);
        if (!this.currentBattleMusic.equals(previous)) {
            battleMusicChanged = true;
        }

        if (!this.currentBattleMusic.getSoundLocation().equals(SoundHandler.MISSING_SOUND.getSoundLocation()) && ((SoundHandlerInvoker) mc.getSoundHandler()).getSoundRegistry().containsKey(this.currentBattleMusic.getSoundLocation())) {
            if (!battleMusicChanged) {
                if (this.currentBattleMusic.isVolumeZero()) {
                    this.getSoundEngineInvoker().resume(this.currentBattleMusic);
                }
                this.currentBattleMusic.resume();
            } else {
                mc.getSoundHandler().playSound(this.currentBattleMusic);
            }

            this.started.set(true);
            this.stopCurrentMusic();
        }
    }

    private void stopCurrentMusic() {
        ISound currentMusic = ((MusicTickerInvoker) mc.getMusicTicker()).getCurrentMusic();
        if (currentMusic != null && mc.getSoundHandler().isSoundPlaying(currentMusic)) {
            this.getSoundEngineInvoker().stop(currentMusic);
        }
    }

    private void stop() {
        this.chooseNextTicks = MathHelper.getInt(RANDOM, 1200, 2400);
        this.started.set(false);
        this.clientMobs.clear();
        if (this.currentBattleMusic != null) {
            this.currentBattleMusic.pause();
        }
    }

    public void stopAll() {
        this.stop();

        if (this.currentBattleMusic != null) {
            this.getSoundEngineInvoker().stop(this.currentBattleMusic);
        }

        this.currentBattleMusic = null;
        this.chooseNextTicks = 0;
        this.clientMobs.clear();
    }

    public void reloadBattleSoundManager() {
        this.battleSoundManager.load();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            this.clientMobs.forEach(entityLiving -> DirectionIndicatorRenderer.render(event, entityLiving));
        }
    }

    @SubscribeEvent
    public void onPlaySound(final PlaySoundEvent event) {
        if (this.isDuringBattle() && event.getSound().getCategory() == SoundCategory.MUSIC) {
            event.setResultSound(null);
        }
    }

    @SubscribeEvent
    public void onLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        this.stopAll();
    }

    @SubscribeEvent
    public void onLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        this.stopAll();
    }

    @SubscribeEvent
    public void onTickEnd(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (this.tickCount++ >= 20) {
                this.tickCount = 0;

                if (this.recentMob != null && this.recentMob.isEntityAlive() && this.shouldPlayBattleMusic()) {
                    this.play(this.recentMob);
                }

                if (!this.clientMobs.isEmpty()) {
                    this.clientMobs.removeIf(entityLiving -> entityLiving.dimension != mc.player.dimension || !entityLiving.isEntityAlive());
                    this.stopIfPlayerIsNotTargeted();
                }
            }

            if (this.currentBattleMusic != null && !this.currentBattleMusic.isPaused() && this.currentBattleMusic.isVolumeZero()) {
                this.getSoundEngineInvoker().pause(this.currentBattleMusic);
                this.currentBattleMusic.setPaused(true);
            }

            if (this.chooseNextTicks > 0) {
                this.chooseNextTicks--;
                if (this.chooseNextTicks <= 0) {
                    if (this.currentBattleMusic != null) {
                        this.getSoundEngineInvoker().stop(this.currentBattleMusic);
                        this.currentBattleMusic.stop();
                    }

                    this.currentBattleMusic = null;
                }
            }
        }
    }

    public boolean isDuringBattle() {
        return this.started.get();
    }

    public int getChooseNextTicks() {
        return this.chooseNextTicks;
    }

    private SoundManagerInvoker getSoundEngineInvoker() {
        return ((SoundHandlerInvoker) mc.getSoundHandler()).getSoundManagerInvoker();
    }

    public ImmutableSet<EntityLiving> getImmutableClientMobs() {
        return ImmutableSet.copyOf(this.clientMobs);
    }

    public static BattleBGMPlayerClient getInstance() {
        if (INSTANCE == null) {
            new BattleBGMPlayerClient();
        }

        return INSTANCE;
    }
}
