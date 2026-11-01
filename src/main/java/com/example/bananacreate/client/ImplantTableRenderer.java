package com.example.bananacreate.client;

import com.example.bananacreate.block.entity.ImplantTableBE;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Mostra o implante colocado na mesa girando e flutuando em cima dela. */
public class ImplantTableRenderer implements BlockEntityRenderer<ImplantTableBE> {
    public ImplantTableRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(ImplantTableBE be, float partialTick, PoseStack ps, MultiBufferSource buffer,
                       int light, int overlay) {
        ItemStack stack = be.getItem();
        if (stack.isEmpty() || be.getLevel() == null) return;

        float t = be.getLevel().getGameTime() + partialTick;
        int lit = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above());
        ps.pushPose();
        ps.translate(0.5, 1.4 + Math.sin(t * 0.08) * 0.06, 0.5);
        ps.mulPose(Axis.YP.rotationDegrees(t * 2.5f));
        ps.scale(1.15f, 1.15f, 1.15f);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, lit,
                OverlayTexture.NO_OVERLAY, ps, buffer, be.getLevel(), (int) be.getBlockPos().asLong());
        ps.popPose();
    }
}
