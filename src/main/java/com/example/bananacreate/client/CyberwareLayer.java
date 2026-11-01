package com.example.bananacreate.client;

import com.example.bananacreate.cyber.Cyberware;
import com.example.bananacreate.cyber.CyberwareData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Desenha o cyberware no corpo do jogador (visivel para todos), seguindo os bracos/tronco. */
public class CyberwareLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final CyberwareModel model;

    public CyberwareLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent,
                          EntityModelSet models) {
        super(parent);
        this.model = new CyberwareModel(models.bakeLayer(CyberwareModel.LAYER));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (player.isInvisible()) return;
        CyberwareData data = CyberwareData.of(player);
        if (data.installed() == 0) return;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(CyberwareModel.TEXTURE));
        int overlay = OverlayTexture.NO_OVERLAY;
        PlayerModel<AbstractClientPlayer> body = getParentModel();

        // Bracos de gorila
        if (data.has(Cyberware.GORILLA_ARMS)) {
            pose.pushPose();
            body.rightArm.translateAndRotate(pose);
            model.gorillaRight.render(pose, vc, light, overlay);
            pose.popPose();
            pose.pushPose();
            body.leftArm.translateAndRotate(pose);
            model.gorillaLeft.render(pose, vc, light, overlay);
            pose.popPose();
        }

        // Laminas de louva-a-deus: caixa sempre visivel, laminas so quando estendidas
        if (data.has(Cyberware.MANTIS_BLADES)) {
            pose.pushPose();
            body.rightArm.translateAndRotate(pose);
            model.bladeHousingRight.render(pose, vc, light, overlay);
            if (data.bladesOut()) model.bladeRight.render(pose, vc, light, overlay);
            pose.popPose();
            pose.pushPose();
            body.leftArm.translateAndRotate(pose);
            model.bladeHousingLeft.render(pose, vc, light, overlay);
            if (data.bladesOut()) model.bladeLeft.render(pose, vc, light, overlay);
            pose.popPose();
        }

        // Monofio: bracadeira no braco esquerdo
        if (data.has(Cyberware.MONOWIRE)) {
            pose.pushPose();
            body.leftArm.translateAndRotate(pose);
            model.spool.render(pose, vc, light, overlay);
            pose.popPose();
        }

        // Lancador de projeteis no ombro direito
        if (data.has(Cyberware.PROJECTILE_LAUNCHER)) {
            pose.pushPose();
            body.body.translateAndRotate(pose);
            model.launcher.render(pose, vc, light, overlay);
            pose.popPose();
        }
    }
}
