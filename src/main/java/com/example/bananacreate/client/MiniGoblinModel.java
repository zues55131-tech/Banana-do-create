package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.entity.MiniGoblin;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Modelo do Mini Goblin: cabecona com nariz comprido e orelhas pontudas, chapeu de folha,
 * colete, mochila com banana pendurada. Skin 64x64 (ver tools/gen_goblin.py).
 */
public class MiniGoblinModel extends HierarchicalModel<MiniGoblin> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "mini_goblin"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart earRight;
    private final ModelPart earLeft;
    private final ModelPart hatTip;
    private final ModelPart body;
    private final ModelPart armRight;
    private final ModelPart armLeft;
    private final ModelPart legRight;
    private final ModelPart legLeft;
    private final ModelPart banana;

    public MiniGoblinModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.earRight = head.getChild("ear_right");
        this.earLeft = head.getChild("ear_left");
        this.hatTip = head.getChild("hat").getChild("hat_tip");
        this.body = root.getChild("body");
        this.armRight = root.getChild("arm_right");
        this.armLeft = root.getChild("arm_left");
        this.legRight = root.getChild("leg_right");
        this.legLeft = root.getChild("leg_left");
        this.banana = body.getChild("backpack").getChild("banana");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                        .texOffs(32, 0).addBox(-1.0f, -3.0f, -6.0f, 2.0f, 3.0f, 2.0f), // nariz
                PartPose.offset(0.0f, 12.0f, 0.0f));
        head.addOrReplaceChild("ear_right",
                CubeListBuilder.create().texOffs(40, 0).addBox(-5.0f, -1.5f, -0.5f, 5.0f, 3.0f, 1.0f),
                PartPose.offsetAndRotation(-4.0f, -4.0f, 0.0f, 0.0f, 0.0f, -0.35f));
        head.addOrReplaceChild("ear_left",
                CubeListBuilder.create().texOffs(40, 5).addBox(0.0f, -1.5f, -0.5f, 5.0f, 3.0f, 1.0f),
                PartPose.offsetAndRotation(4.0f, -4.0f, 0.0f, 0.0f, 0.0f, 0.35f));
        PartDefinition hat = head.addOrReplaceChild("hat",
                CubeListBuilder.create().texOffs(0, 16).addBox(-3.0f, -11.0f, -3.0f, 6.0f, 3.0f, 6.0f),
                PartPose.ZERO);
        hat.addOrReplaceChild("hat_tip",
                CubeListBuilder.create().texOffs(24, 16).addBox(-1.5f, -3.0f, -1.5f, 3.0f, 3.0f, 3.0f),
                PartPose.offsetAndRotation(0.0f, -10.5f, 0.0f, -0.25f, 0.0f, 0.3f));

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 28).addBox(-3.5f, 0.0f, -2.0f, 7.0f, 7.0f, 4.0f),
                PartPose.offset(0.0f, 12.0f, 0.0f));
        PartDefinition pack = body.addOrReplaceChild("backpack",
                CubeListBuilder.create().texOffs(0, 42).addBox(-3.0f, 1.0f, 2.0f, 6.0f, 5.0f, 3.0f),
                PartPose.ZERO);
        pack.addOrReplaceChild("banana",
                CubeListBuilder.create().texOffs(20, 42).addBox(-1.0f, -4.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(1.5f, 2.0f, 4.0f, 0.4f, 0.0f, 0.5f));

        root.addOrReplaceChild("arm_right",
                CubeListBuilder.create().texOffs(24, 28).addBox(-2.0f, -1.0f, -1.0f, 2.0f, 6.0f, 2.0f),
                PartPose.offset(-3.5f, 13.0f, 0.0f));
        root.addOrReplaceChild("arm_left",
                CubeListBuilder.create().texOffs(32, 28).addBox(0.0f, -1.0f, -1.0f, 2.0f, 6.0f, 2.0f),
                PartPose.offset(3.5f, 13.0f, 0.0f));
        root.addOrReplaceChild("leg_right",
                CubeListBuilder.create().texOffs(40, 28).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(-1.8f, 19.0f, 0.0f));
        root.addOrReplaceChild("leg_left",
                CubeListBuilder.create().texOffs(48, 28).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offset(1.8f, 19.0f, 0.0f));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MiniGoblin goblin, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD;
        head.zRot = 0.0f;

        // andar: bracos e pernas opostos, passinhos curtos e rapidos
        float swing = Mth.cos(limbSwing * 0.9f) * 1.3f * limbSwingAmount;
        legRight.xRot = swing;
        legLeft.xRot = -swing;
        armRight.xRot = -swing * 0.8f;
        armLeft.xRot = swing * 0.8f;
        armRight.zRot = 0.05f;
        armLeft.zRot = -0.05f;
        armRight.yRot = 0.0f;
        armLeft.yRot = 0.0f;
        body.zRot = 0.0f;
        body.yRot = 0.0f;
        body.y = 12.0f;
        head.y = 12.0f;

        // orelhas e ponta do chapeu balancam sempre
        float flap = Mth.sin(ageInTicks * 0.12f) * 0.07f + Mth.cos(limbSwing * 0.9f) * 0.25f * limbSwingAmount;
        earRight.zRot = -0.35f - flap;
        earLeft.zRot = 0.35f + flap;
        hatTip.zRot = 0.3f + Mth.sin(ageInTicks * 0.1f) * 0.12f;
        banana.zRot = 0.5f + Mth.sin(ageInTicks * 0.15f + 1.0f) * 0.05f + swing * 0.2f;

        if (goblin.isDancing()) {
            float t = ageInTicks * 0.9f;
            armRight.xRot = -2.8f + Mth.sin(t) * 0.5f;
            armLeft.xRot = -2.8f + Mth.sin(t + Mth.PI) * 0.5f;
            armRight.zRot = 0.4f;
            armLeft.zRot = -0.4f;
            body.zRot = Mth.sin(t) * 0.12f;
            head.zRot = -body.zRot * 1.6f;
            earRight.zRot = -0.35f - Mth.sin(t * 2.0f) * 0.4f;
            earLeft.zRot = 0.35f + Mth.sin(t * 2.0f) * 0.4f;
            legRight.xRot = Mth.sin(t) * 0.7f;
            legLeft.xRot = -Mth.sin(t) * 0.7f;
        } else if (goblin.isTrading()) {
            // bracos cruzados na frente, esfregando as maos (comerciante)
            armRight.xRot = -0.9f + Mth.sin(ageInTicks * 0.4f) * 0.06f;
            armLeft.xRot = -0.9f - Mth.sin(ageInTicks * 0.4f) * 0.06f;
            armRight.yRot = -0.5f;
            armLeft.yRot = 0.5f;
        }
    }
}
