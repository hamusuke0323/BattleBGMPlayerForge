package com.hamusuke.battlebgmplayer.mixin;

import com.hamusuke.battlebgmplayer.network.MobSetTargetPlayerS2CPacket;
import com.hamusuke.battlebgmplayer.network.NetworkManager;
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

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends EntityLivingBase {
    @Nullable
    private EntityPlayerMP currentTargetedPlayer;

    EntityLivingMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onDeath(@NotNull DamageSource cause) {
        super.onDeath(cause);

        if (!this.world.isRemote && this.getServer() != null) {
            this.sendToClient(false, this.getServer().getPlayerList().getPlayers());
        }
    }

    @Inject(method = "setAttackTarget", at = @At("TAIL"))
    private void setTarget(@Nullable EntityLivingBase target, CallbackInfo ci) {
        if (!this.world.isRemote && this.getServer() != null) {
            if (target instanceof EntityPlayerMP && !target.equals(this.currentTargetedPlayer)) {
                if (this.currentTargetedPlayer != null) {
                    this.sendToClient(false, this.currentTargetedPlayer);
                }

                this.currentTargetedPlayer = (EntityPlayerMP) target;
                this.sendToClient(true, this.currentTargetedPlayer);
            } else if (target == null && this.currentTargetedPlayer != null) {
                this.currentTargetedPlayer = null;
                this.sendToClient(false, this.getServer().getPlayerList().getPlayers());
            }
        }
    }

    private void sendToClient(boolean start, EntityPlayerMP target) {
        this.sendToClient(start, Collections.singletonList(target));
    }

    private void sendToClient(boolean start, List<EntityPlayerMP> targets) {
        MobSetTargetPlayerS2CPacket packet = new MobSetTargetPlayerS2CPacket(start, (EntityLiving) (Object) this);
        targets.forEach(entityPlayerMP -> NetworkManager.sendToClient(packet, entityPlayerMP));
    }
}
