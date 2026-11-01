package com.example.bananacreate.block.entity;

import com.example.bananacreate.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Dinamo do Caos (elemento Energia): gera rotacao sem combustivel, mas a
 * velocidade (e o sentido) muda aleatoriamente. Nos picos solta descargas
 * que ferem quem estiver colado nele.
 */
public class ChaosDynamoBE extends GeneratingKineticBlockEntity {
    private static final float CAPACITY_PER_RPM = 24.0f;
    private static final float[] SPEEDS = {-64, -32, -16, 8, 16, 32, 64, 128};

    private float speed = 16;
    private int timer = 40;

    public ChaosDynamoBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHAOS_DYNAMO.get(), pos, state);
    }

    @Override
    public float getGeneratedSpeed() {
        return speed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        this.lastCapacityProvided = CAPACITY_PER_RPM;
        return CAPACITY_PER_RPM;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        if (--timer <= 0) {
            timer = 60 + level.random.nextInt(140);
            float next = SPEEDS[level.random.nextInt(SPEEDS.length)];
            if (next != speed) {
                speed = next;
                updateGeneratedRotation();
                setChanged();
            }
            if (Math.abs(speed) >= 64 && level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.ELECTRIC_SPARK, worldPosition.getX() + 0.5,
                        worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 25, 0.7, 0.7, 0.7, 0.2);
                for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                        new AABB(worldPosition).inflate(1.5))) {
                    e.hurt(level.damageSources().lightningBolt(), 3.0f);
                }
            }
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("ChaosSpeed", speed);
        compound.putInt("ChaosTimer", timer);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (compound.contains("ChaosSpeed")) speed = compound.getFloat("ChaosSpeed");
        timer = compound.getInt("ChaosTimer");
    }
}
