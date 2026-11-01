package com.example.bananacreate.block.entity;

import com.example.bananacreate.registry.ModBlockEntities;
import com.example.bananacreate.registry.ModItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Selo do Medo (elemento Medo): girando, afasta e enfraquece monstros (raio
 * cresce com o RPM). Monstros apavorados as vezes soltam Essencia de Medo.
 */
public class FearSealBE extends KineticBlockEntity {
    public FearSealBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FEAR_SEAL.get(), pos, state);
    }

    @Override
    public float calculateStressApplied() {
        return 6.0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        float speed = Math.abs(getSpeed());
        if (speed < 8 || level.getGameTime() % 10 != 0) return;

        double radius = 4.0 + speed / 16.0;
        Vec3 center = Vec3.atCenterOf(worldPosition);
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, new AABB(worldPosition).inflate(radius),
                m -> m instanceof Enemy && m.isAlive());

        for (Mob m : mobs) {
            m.setTarget(null);
            m.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
            Vec3 away = m.position().subtract(center).multiply(1, 0, 1);
            if (away.lengthSqr() > 1.0E-4) {
                Vec3 v = away.normalize().scale(0.35);
                m.setDeltaMovement(m.getDeltaMovement().add(v.x, 0.1, v.z));
                m.hurtMarked = true;
            }
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.SMOKE, m.getX(), m.getY() + 1.0, m.getZ(), 3, 0.2, 0.3, 0.2, 0.01);
            }
            if (level.random.nextInt(60) == 0) {
                level.addFreshEntity(new ItemEntity(level, m.getX(), m.getY() + 0.5, m.getZ(),
                        new ItemStack(ModItems.FEAR_ESSENCE.get())));
            }
        }
    }
}
