package com.hamusuke.battlebgmplayer.client.gui;

import com.hamusuke.battlebgmplayer.client.BattleBGMPlayerClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public class ConfigScreen extends GuiScreen {
    private final GuiScreen parentScreen;

    public ConfigScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.addButton(new GuiButton(0, this.width / 4, this.height / 2 - 30, this.width / 2, 20, "Reload Config"));
        this.addButton(new GuiButton(2, this.width / 4, this.height / 2 - 10, this.width / 2, 20, "Stop Playing Battle Music"));
        this.addButton(new GuiButton(3, this.width / 4, this.height / 2 + 10, this.width / 2, 20, "Display Debug Screen"));
        this.addButton(new GuiButton(1, this.width / 4, this.height - 20, this.width / 2, 20, I18n.format("gui.done")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(@NotNull GuiButton button) {
        switch (button.id) {
            case 0:
                BattleBGMPlayerClient.getInstance().reloadBattleSoundManager();
                break;
            case 1:
                this.mc.displayGuiScreen(this.parentScreen);
                break;
            case 2:
                BattleBGMPlayerClient.getInstance().stopAll();
                break;
            case 3:
                this.mc.displayGuiScreen(new DebugScreen(this));
                break;
        }
    }
}
