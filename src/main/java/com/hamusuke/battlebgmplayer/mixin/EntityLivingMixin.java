package com.hamusuke.battlebgmplayer.mixin;

import com.hamusuke.battlebgmplayer.invoker.EntityLivingInvoker;
import com.hamusuke.battlebgmplayer.network.NetworkManager;
import com.hamusuke.battlebgmplayer.network.packet.s2c.MobSetTargetPlayerS2CPacket;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends EntityLivingBase implements EntityLivingInvoker {
    @Nullable
    private EntityPlayerMP currentTargetedPlayer;
    private final AtomicBoolean sent = new AtomicBoolean();

    EntityLivingMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onDeath(@NotNull DamageSource cause) {
        super.onDeath(cause);

        if (!this.world.isRemote && this.getServer() != null) {
            this.currentTargetedPlayer = null;
            this.sent.set(false);
            this.sendToClient(false, this.getServer().getPlayerList().getPlayers());
        }
    }

    @Inject(method = "setAttackTarget", at = @At("TAIL"))
    private void setTarget(@Nullable EntityLivingBase target, CallbackInfo ci) {
        if (!this.world.isRemote && this.getServer() != null) {
            if (target instanceof EntityPlayerMP) {
                if (!target.equals(this.currentTargetedPlayer)) {
                    if (this.currentTargetedPlayer != null) {
                        this.sendToClient(false, this.currentTargetedPlayer);
                    }

                    this.currentTargetedPlayer = (EntityPlayerMP) target;
                    this.sent.set(false);
                } else if (!this.sent.get()) {
                    this.sent.set(true);
                    this.sendToClient(true, this.currentTargetedPlayer);
                }
            } else if (target == null && this.currentTargetedPlayer != null) {
                this.currentTargetedPlayer = null;
                this.sendToClient(false, this.getServer().getPlayerList().getPlayers());
            }
        }
    }

    @Override
    @Nullable
    public EntityPlayerMP getCurrentTargetedPlayer() {
        return this.currentTargetedPlayer;
    }

    private void sendToClient(boolean start, EntityPlayerMP target) {
        this.sendToClient(start, Collections.singletonList(target));
    }

    private void sendToClient(boolean start, List<EntityPlayerMP> targets) {
        MobSetTargetPlayerS2CPacket packet = new MobSetTargetPlayerS2CPacket(start, (EntityLiving) (Object) this);
        targets.forEach(entityPlayerMP -> NetworkManager.sendToClient(packet, entityPlayerMP));
    }
}
