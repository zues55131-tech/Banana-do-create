package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.entity.BananaKing;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BananaKingRenderer extends HumanoidMobRenderer<BananaKing, HumanoidModel<BananaKing>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "textures/entity/banana_king.png");

    public BananaKingRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.9f);
    }

    @Override
    public ResourceLocation getTextureLocation(BananaKing entity) {
        return TEXTURE;
    }
}
