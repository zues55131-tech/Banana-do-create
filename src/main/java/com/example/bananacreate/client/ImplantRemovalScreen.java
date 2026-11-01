package com.example.bananacreate.client;

import com.example.bananacreate.cyber.Cyberware;
import com.example.bananacreate.cyber.CyberwareData;
import com.example.bananacreate.item.ChromeScalpelItem;
import com.example.bananacreate.menu.ImplantRemovalMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Tela da Mesa de Remocao: uma linha por implante; clicar numa linha instalada remove o implante. */
public class ImplantRemovalScreen extends AbstractContainerScreen<ImplantRemovalMenu> {
    private static final int ROW_X = 8, ROW_Y = 26, ROW_W = 160, ROW_H = 22, ROW_GAP = 4;

    public ImplantRemovalScreen(ImplantRemovalMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = ROW_Y + Cyberware.values().length * (ROW_H + ROW_GAP) + 22;
    }

    private int rowTop(int i) {
        return topPos + ROW_Y + i * (ROW_H + ROW_GAP);
    }

    private boolean installed(Cyberware c) {
        return minecraft != null && minecraft.player != null && CyberwareData.of(minecraft.player).has(c);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        ImplantEquipScreen.panel(g, leftPos, topPos, imageWidth, imageHeight, 0xFFFF3050);
        Cyberware[] all = Cyberware.values();
        for (int i = 0; i < all.length; i++) {
            int x = leftPos + ROW_X, y = rowTop(i);
            boolean on = installed(all[i]);
            boolean hover = on && mouseX >= x && mouseX < x + ROW_W && mouseY >= y && mouseY < y + ROW_H;
            g.fill(x, y, x + ROW_W, y + ROW_H, 0xFF14141A);
            g.fill(x + 1, y + 1, x + ROW_W - 1, y + ROW_H - 1,
                    !on ? 0xFF24242C : hover ? 0xFF9A2233 : 0xFF5A1A24);
            g.renderItem(new ItemStack(ChromeScalpelItem.itemFor(all[i])), x + 3, y + 3);
            Component name = Component.translatable("item.bananacreate." + all[i].key());
            g.drawString(font, name, x + 24, y + 7, on ? 0xFFFFFF : 0x707078, false);
            if (!on) {
                g.drawString(font, Component.translatable("gui.bananacreate.not_installed"),
                        x + ROW_W - 8 - font.width(Component.translatable("gui.bananacreate.not_installed")),
                        y + 7, 0x707078, false);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, titleLabelX, titleLabelY, 0xFFFFFF, false);
        g.drawString(font, Component.translatable("gui.bananacreate.removal_hint"),
                8, imageHeight - 16, 0xF0A0A8, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Cyberware[] all = Cyberware.values();
        for (int i = 0; i < all.length; i++) {
            int x = leftPos + ROW_X, y = rowTop(i);
            if (installed(all[i]) && mouseX >= x && mouseX < x + ROW_W && mouseY >= y && mouseY < y + ROW_H) {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }
}
