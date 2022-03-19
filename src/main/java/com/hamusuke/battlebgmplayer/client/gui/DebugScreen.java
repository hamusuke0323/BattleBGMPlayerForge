package com.hamusuke.battlebgmplayer.client.gui;

import com.google.common.collect.Lists;
import com.hamusuke.battlebgmplayer.client.BattleBGMPlayerClient;
import com.hamusuke.battlebgmplayer.network.NetworkManager;
import com.hamusuke.battlebgmplayer.network.packet.c2s.ContactServerMobC2SPacket;
import com.hamusuke.battlebgmplayer.network.packet.s2c.ContactServerMobS2CPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class DebugScreen extends GuiScreen {
    @Nullable
    private final GuiScreen parent;
    private TargetingMobsList list;

    public DebugScreen(@Nullable GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.list = new TargetingMobsList();
        this.addButton(new GuiButton(0, this.width / 4, this.height - 20, this.width / 2, 20, I18n.format("gui.back")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void accept(ContactServerMobS2CPacket packet) {
        synchronized (this.list.entries) {
            this.list.entries.forEach(entry -> {
                if (entry.clientMob.getEntityId() == packet.getEntityId()) {
                    entry.attackTargetInfo = packet.getAttackTargetInfo();
                    entry.currentTargetedPlayerInfo = packet.getCurrentTargetedPlayerInfo();
                }
            });
        }
    }

    @Override
    protected void actionPerformed(@NotNull GuiButton button) {
        this.mc.displayGuiScreen(this.parent);
    }

    final class TargetingMobsList extends GuiListExtended {
        private final List<Entry> entries = Lists.newArrayList();

        public TargetingMobsList() {
            super(DebugScreen.this.mc, DebugScreen.this.width, DebugScreen.this.height, 20, DebugScreen.this.height - 20, 30);
            this.entries.addAll(BattleBGMPlayerClient.getInstance().getImmutableClientMobs().stream().map(Entry::new).collect(Collectors.toList()));
        }

        @Override
        @NotNull
        public IGuiListEntry getListEntry(int index) {
            return this.entries.get(index);
        }

        @Override
        protected int getSize() {
            return this.entries.size();
        }

        final class Entry implements IGuiListEntry {
            private final EntityLiving clientMob;
            private final String mobInfo;
            private String attackTargetInfo = "contacting the server...";
            private String currentTargetedPlayerInfo = "contacting the server...";

            private Entry(EntityLiving clientMob) {
                this.clientMob = clientMob;
                this.mobInfo = this.clientMob.toString();
                NetworkManager.sendToServer(new ContactServerMobC2SPacket(this.clientMob));
            }

            @Override
            public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
            }

            @Override
            public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
                DebugScreen.this.fontRenderer.drawStringWithShadow(this.mobInfo, 0, y, 16777215);
                DebugScreen.this.fontRenderer.drawStringWithShadow("this.attackTarget(Server Side) = " + this.attackTargetInfo, 0, y + 10, 16777215);
                DebugScreen.this.fontRenderer.drawStringWithShadow("this.currentTargetedPlayer(Server Side) = " + this.currentTargetedPlayerInfo, 0, y + 20, 16777215);
            }

            @Override
            public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
                return false;
            }

            @Override
            public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            }
        }
    }
}
