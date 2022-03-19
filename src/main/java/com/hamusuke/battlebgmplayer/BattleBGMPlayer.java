package com.hamusuke.battlebgmplayer;

import com.hamusuke.battlebgmplayer.network.NetworkManager;
import com.hamusuke.battlebgmplayer.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BattleBGMPlayer.MOD_ID, name = BattleBGMPlayer.NAME, version = BattleBGMPlayer.VERSION, guiFactory = "com.hamusuke.battlebgmplayer.client.gui.ConfigFactory")
public final class BattleBGMPlayer {
    public static final String MOD_ID = "battlebgmplayer";
    public static final String NAME = "Battle BGM Player";
    public static final String VERSION = "1.1.0";
    @SidedProxy(modId = MOD_ID, serverSide = "com.hamusuke.battlebgmplayer.proxy.CommonProxy", clientSide = "com.hamusuke.battlebgmplayer.proxy.ClientProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        PROXY.preInit(event);
        NetworkManager.init();
    }
}
