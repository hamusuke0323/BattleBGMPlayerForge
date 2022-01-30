package com.hamusuke.battlebgmplayer;

import com.google.common.collect.Sets;
import com.hamusuke.battlebgmplayer.network.MobSetTargetPlayerS2CPacket;
import com.hamusuke.battlebgmplayer.network.NetworkManager;
import com.hamusuke.battlebgmplayer.proxy.CommonProxy;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Set;

@Mod(modid = BattleBGMPlayer.MOD_ID, name = BattleBGMPlayer.NAME, version = BattleBGMPlayer.VERSION)
public final class BattleBGMPlayer {
    public static final String MOD_ID = "battlebgmplayer";
    public static final String NAME = "Battle BGM Player";
    public static final String VERSION = "1.0.0";
    @SidedProxy(modId = MOD_ID, serverSide = "com.hamusuke.battlebgmplayer.proxy.CommonProxy", clientSide = "com.hamusuke.battlebgmplayer.proxy.ClientProxy")
    public static CommonProxy PROXY;
    private final Set<EntityLiving> targeting = Sets.newHashSet();

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        PROXY.preInit(event);
        NetworkManager.init();
    }

    @Mod.EventHandler
    public void onServerStopped(final FMLServerStoppingEvent event) {
        this.targeting.clear();
    }

    @SubscribeEvent
    public void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.targeting.removeIf(entityLiving -> !entityLiving.isEntityAlive());
        }
    }

    @SubscribeEvent
    public void onSetAttackTarget(final LivingSetAttackTargetEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityLiving) {
            EntityLiving mob = (EntityLiving) entity;
            EntityLivingBase target = event.getTarget();
            if (target instanceof EntityPlayerMP) {
                if (this.targeting.add(mob)) {
                    MobSetTargetPlayerS2CPacket packet = new MobSetTargetPlayerS2CPacket(true, mob);
                    NetworkManager.sendToClient(packet, (EntityPlayerMP) target);
                    //((WorldServer) mob.world).playerEntities.forEach(player -> NetworkManager.sendToClient(packet, (EntityPlayerMP) player));
                }
            } else if (target == null) {
                this.targeting.remove(mob);
                MobSetTargetPlayerS2CPacket packet = new MobSetTargetPlayerS2CPacket(false, mob);
                ((WorldServer) mob.world).playerEntities.forEach(player -> NetworkManager.sendToClient(packet, (EntityPlayerMP) player));
            }
        }
    }
}
