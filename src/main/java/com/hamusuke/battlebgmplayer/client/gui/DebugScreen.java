package com.hamusuke.battlebgmplayer.client.gui;

import com.google.common.collect.Lists;
import com.hamusuke.battlebgmplayer.client.BattleBGMPlayerClient;
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

    @Override
    protected void actionPerformed(@NotNull GuiButton button) {
        this.mc.displayGuiScreen(this.parent);
    }

    final class TargetingMobsList extends GuiListExtended {
        private final List<Entry> entries = Lists.newArrayList();

        public TargetingMobsList() {
            super(DebugScreen.this.mc, DebugScreen.this.width, DebugScreen.this.height, 20, DebugScreen.this.height - 20, 10);
            this.entries.addAll(BattleBGMPlayerClient.getInstance().getImmutableMobs().stream().map(Entry::new).collect(Collectors.toList()));
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
            private final String mobInfo;
            private int x;
            private int y;

            private Entry(EntityLiving mob) {
                this.mobInfo = mob.toString();
            }

            @Override
            public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
                this.x = x;
                this.y = y;
            }

            @Override
            public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
                DebugScreen.this.fontRenderer.drawStringWithShadow(this.mobInfo, this.x, this.y, 16777215);
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
