package com.example.bananacreate.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Anima as maquinas cineticas do addon como as do Create: todas as pecas
 * respondem ao RPM recebido. Cada peca tem um tipo de movimento:
 * giro, giro inverso rapido, pistao (sobe e desce como a prensa) e orbita.
 * Usa so a API de render do Minecraft (nao depende dos internals do Flywheel).
 */
public class MachineRenderer<T extends KineticBlockEntity> implements BlockEntityRenderer<T> {
    public enum Motion { SPIN, SPIN_REVERSE_FAST, PISTON, ORBIT }

    public record Part(ModelResourceLocation model, Motion motion) {}

    private final Part[] parts;
    private final ParticleOptions particle;

    public MachineRenderer(ParticleOptions particle, Part... parts) {
        this.parts = parts;
        this.particle = particle;
    }

    @Override
    public void render(T be, float partialTick, PoseStack ps, MultiBufferSource buffer, int light, int overlay) {
        Level level = be.getLevel();
        if (level == null) return;
        BlockState state = be.getBlockState();
        if (!state.hasProperty(RotatedPillarKineticBlock.AXIS)) return;
        Direction.Axis axis = state.getValue(RotatedPillarKineticBlock.AXIS);
        BlockPos pos = be.getBlockPos();
        float speed = be.getSpeed();

        // mesma formula do Create: graus = tempo * RPM * 3/10 (+ fase pelo eixo da posicao)
        float time = level.getGameTime() + partialTick;
        float angle = (time * speed * 3f / 10f) % 360f;
        if (speed != 0 && (pos.getX() + pos.getY() + pos.getZ()) % 2 != 0) {
            angle += 22.5f;
        }

        VertexConsumer vc = buffer.getBuffer(RenderType.cutoutMipped());
        for (Part part : parts) {
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(part.model());
            ps.pushPose();
            ps.translate(0.5, 0.5, 0.5);
            // os modelos sao feitos com o eixo em Y: alinha com o eixo real do bloco
            if (axis == Direction.Axis.Z) ps.mulPose(Axis.XP.rotationDegrees(90));
            else if (axis == Direction.Axis.X) ps.mulPose(Axis.ZP.rotationDegrees(-90));

            switch (part.motion()) {
                case SPIN -> ps.mulPose(Axis.YP.rotationDegrees(angle));
                case SPIN_REVERSE_FAST -> ps.mulPose(Axis.YP.rotationDegrees(-angle * 2f));
                case PISTON -> {
                    // desce e sobe duas vezes por volta: 0 .. -0.28 bloco
                    float down = 0.5f - 0.5f * (float) Math.cos(Math.toRadians(angle * 2f));
                    ps.translate(0, -0.28f * (speed == 0 ? 0 : down), 0);
                }
                case ORBIT -> {
                    ps.mulPose(Axis.YP.rotationDegrees(-angle * 0.5f));
                    ps.translate(0, speed == 0 ? 0 : 0.03f * Math.sin(Math.toRadians(angle * 3f)), 0);
                }
            }
            ps.translate(-0.5, -0.5, -0.5);

            Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                    .renderModel(ps.last(), vc, state, model, 1f, 1f, 1f, light, OverlayTexture.NO_OVERLAY);
            ps.popPose();
        }

        // particulas enquanto a maquina trabalha
        if (particle != null && speed != 0 && level.random.nextFloat() < 0.04f) {
            level.addParticle(particle,
                    pos.getX() + 0.25 + level.random.nextDouble() * 0.5,
                    pos.getY() + 1.05,
                    pos.getZ() + 0.25 + level.random.nextDouble() * 0.5,
                    0, 0.02, 0);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(T be) {
        return false;
    }
}
