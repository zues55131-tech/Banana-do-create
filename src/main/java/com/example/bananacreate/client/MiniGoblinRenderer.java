package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.entity.MiniGoblin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MiniGoblinRenderer extends MobRenderer<MiniGoblin, MiniGoblinModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "textures/entity/mini_goblin.png");

    public MiniGoblinRenderer(EntityRendererProvider.Context context) {
        super(context, new MiniGoblinModel(context.bakeLayer(MiniGoblinModel.LAYER)), 0.3f);
    }

    @Override
    protected void scale(MiniGoblin goblin, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.8f, 0.8f, 0.8f);
    }

    @Override
    public ResourceLocation getTextureLocation(MiniGoblin goblin) {
        return TEXTURE;
    }
}
