package com.example.bananacreate.client;

import com.example.bananacreate.menu.ImplantEquipMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/** Tela da Mesa de Implantes (desenhada por codigo, sem textura propria). */
public class ImplantEquipScreen extends AbstractContainerScreen<ImplantEquipMenu> {
    public ImplantEquipScreen(ImplantEquipMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 148;
        this.inventoryLabelY = 55;
    }

    static void panel(GuiGraphics g, int x, int y, int w, int h, int accent) {
        g.fill(x, y, x + w, y + h, 0xFF14141A);
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, 0xFF30303A);
        g.fill(x + 2, y + 2, x + w - 2, y + 4, accent);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        panel(g, leftPos, topPos, imageWidth, imageHeight, 0xFF00E5FF);
        for (Slot s : menu.slots) {
            int sx = leftPos + s.x - 1, sy = topPos + s.y - 1;
            g.fill(sx, sy, sx + 18, sy + 18, 0xFF14141A);
            g.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF55555F);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, titleLabelX, titleLabelY, 0xFFFFFF, false);
        g.drawString(font, Component.translatable("gui.bananacreate.implant_table_hint"), 8, 44, 0x9AE8F0, false);
        g.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xC0C0C0, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }
}
