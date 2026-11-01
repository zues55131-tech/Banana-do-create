package com.example.bananacreate.block.entity;

import com.example.bananacreate.registry.ModBlockEntities;
import com.example.bananacreate.registry.ModItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Sifao da Morte (elemento Morte): girando, puxa monstros num raio e drena os
 * que chegam perto (dano de definhar). Quem morre ali vira Essencia de Morte.
 */
public class DeathSiphonBE extends KineticBlockEntity {
    public DeathSiphonBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DEATH_SIPHON.get(), pos, state);
    }

    @Override
    public float calculateStressApplied() {
        return 8.0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        float speed = Math.abs(getSpeed());
        if (speed < 8) return;

        double radius = 4.0 + speed / 32.0;
        Vec3 center = Vec3.atCenterOf(worldPosition);
        List<LivingEntity> mobs = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(worldPosition).inflate(radius), e -> e instanceof Enemy && e.isAlive() && !(e instanceof Player));

        for (LivingEntity m : mobs) {
            Vec3 pull = center.subtract(m.position());
            double dist = pull.length();
            if (dist > 1.2) {
                Vec3 v = pull.normalize().scale(0.08 + speed / 1500.0);
                m.setDeltaMovement(m.getDeltaMovement().add(v));
                m.hurtMarked = true;
            }
            if (dist < 2.2 && level.getGameTime() % 10 == 0) {
                m.hurt(level.damageSources().wither(), 2.0f);
                if (m.isDeadOrDying()) {
                    level.addFreshEntity(new ItemEntity(level, m.getX(), m.getY() + 0.3, m.getZ(),
                            new ItemStack(ModItems.DEATH_ESSENCE.get())));
                    if (level instanceof ServerLevel server) {
                        server.sendParticles(ParticleTypes.SOUL, m.getX(), m.getY() + 1, m.getZ(), 12, 0.3, 0.5, 0.3, 0.05);
                    }
                }
            }
        }
    }
}
