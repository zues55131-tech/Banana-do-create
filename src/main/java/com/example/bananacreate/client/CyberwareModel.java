package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Pecas 3D do cyberware vestivel. Tudo desenhado na textura 64x64
 * textures/entity/cyberware.png (gerada por tools/gen_models.py).
 * As coordenadas sao relativas ao pivo do braco (ou do tronco) do jogador.
 */
public class CyberwareModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "cyberware"), "main");
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "textures/entity/cyberware.png");

    // bracos de gorila (cobrem o braco normal, mais grossos e compridos)
    public final ModelPart gorillaRight, gorillaLeft;
    // laminas de louva-a-deus (parte do braco)
    public final ModelPart bladeHousingRight, bladeHousingLeft, bladeRight, bladeLeft;
    // lancador no ombro direito (relativo ao tronco) e bracadeira do monofio no braco esquerdo
    public final ModelPart launcher, spool;

    public CyberwareModel(ModelPart root) {
        gorillaRight = root.getChild("gorilla_right");
        gorillaLeft = root.getChild("gorilla_left");
        bladeHousingRight = root.getChild("blade_housing_right");
        bladeHousingLeft = root.getChild("blade_housing_left");
        bladeRight = root.getChild("blade_right");
        bladeLeft = root.getChild("blade_left");
        launcher = root.getChild("launcher");
        spool = root.getChild("spool");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ---- Gorila: pelo (ombro), cromo (antebraco), punho, piston hidraulico
        root.addOrReplaceChild("gorilla_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5f, -3.5f, -3.5f, 7, 9, 7)
                .texOffs(0, 16).addBox(-5.0f, 5.5f, -4.0f, 8, 9, 8)
                .texOffs(0, 34).addBox(-4.5f, 14.5f, -3.5f, 7, 3, 7)
                .texOffs(32, 0).addBox(-6.2f, 0.5f, -1.0f, 2, 10, 2),
                PartPose.ZERO);
        root.addOrReplaceChild("gorilla_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5f, -3.5f, -3.5f, 7, 9, 7)
                .texOffs(0, 16).addBox(-3.0f, 5.5f, -4.0f, 8, 9, 8)
                .texOffs(0, 34).addBox(-2.5f, 14.5f, -3.5f, 7, 3, 7)
                .texOffs(32, 0).addBox(4.2f, 0.5f, -1.0f, 2, 10, 2),
                PartPose.ZERO);

        // ---- Louva-a-deus: caixa no antebraco + lamina que sai do punho (com ponta curva)
        root.addOrReplaceChild("blade_housing_right", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-3.5f, 5.0f, 2.0f, 5, 4, 2), PartPose.ZERO);
        root.addOrReplaceChild("blade_housing_left", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-1.5f, 5.0f, 2.0f, 5, 4, 2), PartPose.ZERO);

        PartDefinition br = root.addOrReplaceChild("blade_right", CubeListBuilder.create()
                .texOffs(40, 8).addBox(-1.5f, 8.0f, -1.5f, 1, 14, 3), PartPose.ZERO);
        br.addOrReplaceChild("tip", CubeListBuilder.create()
                .texOffs(50, 8).addBox(-1.5f, 0.0f, -1.0f, 1, 6, 2),
                PartPose.offsetAndRotation(0, 22, 0, -0.4f, 0, 0));
        PartDefinition bl = root.addOrReplaceChild("blade_left", CubeListBuilder.create()
                .texOffs(40, 8).addBox(0.5f, 8.0f, -1.5f, 1, 14, 3), PartPose.ZERO);
        bl.addOrReplaceChild("tip", CubeListBuilder.create()
                .texOffs(50, 8).addBox(0.5f, 0.0f, -1.0f, 1, 6, 2),
                PartPose.offsetAndRotation(0, 22, 0, -0.4f, 0, 0));

        // ---- Lancador no ombro direito (espaco do tronco)
        root.addOrReplaceChild("launcher", CubeListBuilder.create()
                .texOffs(30, 46).addBox(-8.0f, -3.0f, -2.5f, 5, 3, 5)
                .texOffs(0, 46).addBox(-7.5f, -7.0f, -6.0f, 4, 4, 10)
                .texOffs(52, 46).addBox(-7.0f, -6.5f, -8.0f, 3, 3, 2),
                PartPose.ZERO);

        // ---- Bracadeira do monofio (braco esquerdo)
        root.addOrReplaceChild("spool", CubeListBuilder.create()
                .texOffs(30, 54).addBox(-2.0f, 7.0f, -3.0f, 6, 3, 6), PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
