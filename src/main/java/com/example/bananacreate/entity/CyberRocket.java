package com.example.bananacreate.entity;

import com.example.bananacreate.registry.ModEntities;
import com.example.bananacreate.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Mini-foguete do lancador de projeteis: reto, explode ao tocar (sem destruir blocos). */
public class CyberRocket extends ThrowableItemProjectile {
    public CyberRocket(EntityType<? extends CyberRocket> type, Level level) {
        super(type, level);
    }

    public CyberRocket(Level level, LivingEntity owner) {
        super(ModEntities.CYBER_ROCKET.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MINI_ROCKET.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.01;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            level().addParticle(net.minecraft.core.particles.ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 0, 0, 0);
        } else if (tickCount > 80) {
            detonate();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide) {
            result.getEntity().hurt(damageSources().thrown(this, getOwner()), 5.0f);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) detonate();
    }

    private void detonate() {
        level().explode(this, getX(), getY(), getZ(), 2.0f, Level.ExplosionInteraction.NONE);
        discard();
    }
}
