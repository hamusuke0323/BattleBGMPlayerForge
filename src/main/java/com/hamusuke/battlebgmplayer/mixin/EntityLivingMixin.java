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
            MobSetTargetPlayerS2CPacket packet = new MobSetTargetPlayerS2CPacket(false, (EntityLiving) (Object) this);
            this.getServer().getPlayerList().getPlayers().forEach(entityPlayerMP -> NetworkManager.sendToClient(packet, entityPlayerMP));
        }
    }

    @Inject(method = "setAttackTarget", at = @At("TAIL"))
    private void setTarget(@Nullable EntityLivingBase target, CallbackInfo ci) {
        if (target instanceof EntityPlayerMP && !target.equals(this.currentTargetedPlayer) && !this.world.isRemote) {
            this.currentTargetedPlayer = (EntityPlayerMP) target;
            NetworkManager.sendToClient(new MobSetTargetPlayerS2CPacket(true, (EntityLiving) (Object) this), this.currentTargetedPlayer);
        }
    }
}
