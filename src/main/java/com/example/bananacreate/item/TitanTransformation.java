package com.example.bananacreate.item;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

/**
 * Transformacao em tita estilo Attack on Titan: o mob fica paralisado, raios caem,
 * ele infla ate o tamanho final e explode em vapor e onda de choque (sem quebrar blocos).
 * O estado fica no NBT do mob (hora de inicio), entao sobrevive a recarregar o mundo.
 */
@EventBusSubscriber(modid = BananaCreate.MOD_ID)
public class TitanTransformation {
    public static final String KEY_START = "bananacreate_titan_start";
    private static final String KEY_HAD_NOAI = "bananacreate_titan_had_noai";
    /** 160 ticks = 8 s, o mesmo tamanho do tema (sounds/titan_transformation.ogg). A explosao cai no fim da musica. */
    public static final int DURATION = 160;
    private static final double FINAL_EXTRA_SCALE = 5.0; // mesmo valor de TitanSyringeItem.makeTitan
    private static final ResourceLocation GROW_ID =
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "titan_growing");

    public static boolean isTransforming(Mob mob) {
        return mob.getPersistentData().contains(KEY_START);
    }

    public static void start(Mob mob) {
        if (!(mob.level() instanceof ServerLevel level) || isTransforming(mob)) return;
        CompoundTag data = mob.getPersistentData();
        data.putLong(KEY_START, level.getGameTime());
        data.putBoolean(KEY_HAD_NOAI, mob.isNoAi());
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        mob.setTarget(null);
        mob.setPersistenceRequired();
        bolt(level, mob);
        // tema da transformacao: volume 4 = audivel ate ~64 blocos
        level.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                ModSounds.TITAN_TRANSFORMATION.get(), SoundSource.HOSTILE, 4.0f, 1.0f);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Mob mob) || !(mob.level() instanceof ServerLevel level)) return;
        CompoundTag data = mob.getPersistentData();
        if (!data.contains(KEY_START)) return;

        long elapsed = Math.max(0, level.getGameTime() - data.getLong(KEY_START));
        if (elapsed >= DURATION) {
            finish(level, mob, data);
            return;
        }

        float progress = elapsed / (float) DURATION;
        setGrowth(mob, FINAL_EXTRA_SCALE * progress * progress);
        mob.setDeltaMovement(Vec3.ZERO);

        double h = mob.getBbHeight();
        double w = Math.max(0.6, mob.getBbWidth());
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, mob.getX(), mob.getY() + h * 0.5, mob.getZ(),
                3 + (int) (progress * 8), w * 0.5, h * 0.4, w * 0.5, 0.15);
        level.sendParticles(ParticleTypes.CLOUD, mob.getX(), mob.getY() + 0.1, mob.getZ(),
                2, w * 0.5, 0.05, w * 0.5, 0.03);

        // batimento baixo por baixo da musica, acelerando perto do fim
        int beat = progress < 0.6f ? 20 : 10;
        if (elapsed % beat == 0) {
            level.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                    SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 0.8f, 0.6f + progress * 0.8f);
        }
        // raios no ritmo da musica (a parte forte comeca em ~2 s): 2 s, 4 s e 6 s
        if (elapsed == 40 || elapsed == 80 || elapsed == 120) {
            bolt(level, mob);
        }
    }

    private static void finish(ServerLevel level, Mob mob, CompoundTag data) {
        AttributeInstance scale = mob.getAttribute(Attributes.SCALE);
        if (scale != null) scale.removeModifier(GROW_ID);
        mob.setNoAi(data.getBoolean(KEY_HAD_NOAI));
        mob.setInvulnerable(false);
        data.remove(KEY_START);
        data.remove(KEY_HAD_NOAI);

        TitanSyringeItem.makeTitan(mob);
        explode(level, mob);
    }

    private static void setGrowth(Mob mob, double amount) {
        AttributeInstance scale = mob.getAttribute(Attributes.SCALE);
        if (scale == null) return;
        scale.removeModifier(GROW_ID);
        scale.addTransientModifier(new AttributeModifier(GROW_ID, amount, AttributeModifier.Operation.ADD_VALUE));
    }

    private static void bolt(ServerLevel level, Mob mob) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) return;
        bolt.moveTo(mob.getX(), mob.getY(), mob.getZ());
        bolt.setVisualOnly(true); // so o efeito: nao queima nem fere
        level.addFreshEntity(bolt);
    }

    /** Explosao de vapor + onda de choque que empurra tudo em volta, sem dano a blocos. */
    private static void explode(ServerLevel level, Mob mob) {
        double x = mob.getX(), y = mob.getY(), z = mob.getZ();
        bolt(level, mob);
        level.sendParticles(ParticleTypes.FLASH, x, y + mob.getBbHeight() * 0.5, z, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 1.0, z, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y + mob.getBbHeight() * 0.4, z,
                60, mob.getBbWidth() * 0.6, mob.getBbHeight() * 0.3, mob.getBbWidth() * 0.6, 0.08);
        // anel de vapor se espalhando pelo chao
        for (int i = 0; i < 48; i++) {
            double a = i * (Math.PI * 2 / 48);
            level.sendParticles(ParticleTypes.CLOUD, x, y + 0.2, z, 0, Math.cos(a), 0.02, Math.sin(a), 0.9);
        }
        level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 3.0f, 0.55f);
        level.playSound(null, x, y, z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 3.0f, 0.8f);

        List<LivingEntity> around = level.getEntitiesOfClass(LivingEntity.class,
                mob.getBoundingBox().inflate(9.0, 3.0, 9.0), e -> e != mob && e.isAlive());
        for (LivingEntity e : around) {
            e.knockback(1.8, x - e.getX(), z - e.getZ());
            e.setDeltaMovement(e.getDeltaMovement().add(0, 0.5, 0));
            e.hurtMarked = true;
        }
    }
}
