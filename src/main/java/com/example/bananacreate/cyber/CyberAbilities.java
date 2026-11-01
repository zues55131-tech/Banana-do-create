package com.example.bananacreate.cyber;

import com.example.bananacreate.entity.CyberRocket;
import com.example.bananacreate.registry.ModAttachments;
import com.example.bananacreate.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Habilidades ativas do cyberware, disparadas pelas teclas do cliente. */
public class CyberAbilities {
    public static final int TOGGLE_BLADES = 0;
    public static final int LAUNCH = 1;
    public static final int WIRE = 2;
    public static final int SLAM = 3;
    public static final int DASH = 4;
    public static final int GRAPPLE = 5;

    public static void perform(ServerPlayer p, int action) {
        CyberwareData d = CyberwareData.of(p);
        switch (action) {
            case TOGGLE_BLADES -> toggleBlades(p, d);
            case LAUNCH -> launch(p, d);
            case WIRE -> wire(p, d);
            case SLAM -> slam(p, d);
            case DASH -> dash(p, d);
            case GRAPPLE -> {
                if (d.has(Cyberware.MONOWIRE)) GrappleHandler.toggle(p);
            }
            default -> { }
        }
    }

    private static boolean onCooldown(ServerPlayer p, Item handle) {
        return p.getCooldowns().isOnCooldown(handle);
    }

    private static void toggleBlades(ServerPlayer p, CyberwareData d) {
        if (!d.has(Cyberware.MANTIS_BLADES)) return;
        boolean out = !d.bladesOut();
        p.setData(ModAttachments.CYBERWARE, d.withBlades(out));
        CyberNet.syncToAll(p);
        p.level().playSound(null, p.getX(), p.getY(), p.getZ(),
                out ? SoundEvents.CHAIN_PLACE : SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.0f, out ? 1.4f : 0.9f);
        p.displayClientMessage(Component.translatable(out
                ? "message.bananacreate.blades_out" : "message.bananacreate.blades_in"), true);
    }

    private static void launch(ServerPlayer p, CyberwareData d) {
        if (!d.has(Cyberware.PROJECTILE_LAUNCHER)) return;
        Item handle = ModItems.PROJECTILE_LAUNCHER.get();
        if (onCooldown(p, handle)) return;

        boolean creative = p.getAbilities().instabuild;
        if (!creative) {
            ItemStack ammo = ItemStack.EMPTY;
            for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
                ItemStack s = p.getInventory().getItem(i);
                if (s.is(Items.GUNPOWDER)) {
                    ammo = s;
                    break;
                }
            }
            if (ammo.isEmpty()) {
                p.displayClientMessage(Component.translatable("message.bananacreate.no_ammo"), true);
                return;
            }
            ammo.shrink(1);
        }

        CyberRocket rocket = new CyberRocket(p.level(), p);
        rocket.setItem(new ItemStack(ModItems.MINI_ROCKET.get()));
        rocket.shootFromRotation(p, p.getXRot(), p.getYRot(), 0.0f, 2.2f, 0.4f);
        p.level().addFreshEntity(rocket);
        p.level().playSound(null, p.getX(), p.getY(), p.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0f, 0.7f);
        p.getCooldowns().addCooldown(handle, 30);
    }

    private static void wire(ServerPlayer p, CyberwareData d) {
        if (!d.has(Cyberware.MONOWIRE)) return;
        Item handle = ModItems.MONOWIRE.get();
        if (onCooldown(p, handle)) return;

        final double range = 9.0;
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        Vec3 end = eye.add(look.scale(range));

        List<LivingEntity> targets = p.level().getEntitiesOfClass(LivingEntity.class,
                p.getBoundingBox().expandTowards(look.scale(range)).inflate(1.5),
                e -> e != p && e.isAlive());
        int hit = 0;
        for (LivingEntity e : targets) {
            Vec3 rel = e.getBoundingBox().getCenter().subtract(eye);
            double t = rel.dot(look);
            if (t < 0 || t > range) continue;
            double perp = rel.subtract(look.scale(t)).length();
            if (perp > 1.3) continue;
            if (e.hurt(p.damageSources().playerAttack(p), 9.0f)) {
                e.addEffect(new MobEffectInstance(MobEffects.WITHER, 50, 0));
                Vec3 pull = p.position().subtract(e.position()).normalize().scale(0.6);
                e.setDeltaMovement(e.getDeltaMovement().add(pull.x, 0.2, pull.z));
                e.hurtMarked = true;
                hit++;
            }
        }

        ServerLevel level = p.serverLevel();
        for (int i = 0; i <= 24; i++) {
            Vec3 pt = eye.add(0, -0.25, 0).add(end.subtract(eye).scale(i / 24.0));
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pt.x, pt.y, pt.z, 1, 0.02, 0.02, 0.02, 0.0);
        }
        level.playSound(null, p.getX(), p.getY(), p.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.8f);
        p.getCooldowns().addCooldown(handle, hit > 0 ? 25 : 15);
    }

    private static void slam(ServerPlayer p, CyberwareData d) {
        if (!d.has(Cyberware.GORILLA_ARMS)) return;
        Item handle = ModItems.GORILLA_ARMS.get();
        if (onCooldown(p, handle)) return;

        ServerLevel level = p.serverLevel();
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                p.getBoundingBox().inflate(4.0, 1.5, 4.0), e -> e != p && e.isAlive());
        for (LivingEntity e : targets) {
            if (e.hurt(p.damageSources().playerAttack(p), 7.0f)) {
                double dx = p.getX() - e.getX();
                double dz = p.getZ() - e.getZ();
                e.knockback(1.6, dx, dz);
                e.setDeltaMovement(e.getDeltaMovement().add(0, 0.45, 0));
                e.hurtMarked = true;
            }
        }
        level.sendParticles(ParticleTypes.EXPLOSION, p.getX(), p.getY() + 0.2, p.getZ(), 6, 1.5, 0.1, 1.5, 0.0);
        level.sendParticles(ParticleTypes.CRIT, p.getX(), p.getY() + 0.5, p.getZ(), 30, 2.0, 0.3, 2.0, 0.2);
        level.playSound(null, p.getX(), p.getY(), p.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.7f, 1.3f);
        p.causeFoodExhaustion(1.5f);
        p.getCooldowns().addCooldown(handle, 100);
    }

    /** Y: dash ate a entidade que o jogador estiver olhando (ate 15 blocos), cortando ao chegar. */
    private static void dash(ServerPlayer p, CyberwareData d) {
        if (!d.has(Cyberware.MANTIS_BLADES)) return;
        Item handle = ModItems.MANTIS_BLADES.get();
        if (onCooldown(p, handle)) return;

        final double range = 15.0;
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        List<LivingEntity> candidates = p.level().getEntitiesOfClass(LivingEntity.class,
                p.getBoundingBox().expandTowards(look.scale(range)).inflate(2.0),
                e -> e != p && e.isAlive());

        LivingEntity target = null;
        double bestT = range + 1;
        for (LivingEntity e : candidates) {
            Vec3 center = e.getBoundingBox().getCenter();
            Vec3 rel = center.subtract(eye);
            double t = rel.dot(look);
            if (t < 1.0 || t > range) continue;
            double perp = rel.subtract(look.scale(t)).length();
            double reach = 0.6 + Math.max(e.getBbWidth(), e.getBbHeight() * 0.5) * 0.5;
            if (perp > reach) continue;
            // sem parede no caminho
            HitResult clip = p.level().clip(new ClipContext(eye, center, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, p));
            if (clip.getType() == HitResult.Type.BLOCK) continue;
            if (t < bestT) {
                bestT = t;
                target = e;
            }
        }
        if (target == null) {
            p.displayClientMessage(Component.translatable("message.bananacreate.dash_no_target"), true);
            return;
        }

        // chega colado no alvo (1.6 bloco antes do centro), na altura do alvo
        Vec3 from = p.position();
        Vec3 to = target.position();
        Vec3 flat = new Vec3(to.x - from.x, 0, to.z - from.z);
        if (flat.lengthSqr() < 1.0E-4) return;
        flat = flat.normalize();
        double stop = 1.6 + target.getBbWidth() * 0.5;
        Vec3 dest = new Vec3(to.x - flat.x * stop, Math.max(to.y, from.y), to.z - flat.z * stop);
        if (!p.level().noCollision(p, p.getBoundingBox().move(dest.subtract(from)))) {
            dest = new Vec3(dest.x, from.y, dest.z);
            if (!p.level().noCollision(p, p.getBoundingBox().move(dest.subtract(from)))) {
                p.displayClientMessage(Component.translatable("message.bananacreate.dash_blocked"), true);
                return;
            }
        }

        if (!d.bladesOut()) { // o dash estende as laminas sozinho
            p.setData(ModAttachments.CYBERWARE, d.withBlades(true));
            CyberNet.syncToAll(p);
        }

        ServerLevel level = p.serverLevel();
        Vec3 path = dest.subtract(from);
        int steps = Math.max(2, (int) (path.length() * 2));
        for (int i = 0; i <= steps; i++) {
            Vec3 pt = from.add(path.scale(i / (double) steps)).add(0, 1.0, 0);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pt.x, pt.y, pt.z, 2, 0.1, 0.3, 0.1, 0.02);
            if (i % 4 == 0) level.sendParticles(ParticleTypes.CLOUD, pt.x, pt.y - 0.5, pt.z, 1, 0.1, 0.05, 0.1, 0.0);
        }
        level.playSound(null, from.x, from.y, from.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 0.6f);

        float yaw = (float) (Math.toDegrees(Math.atan2(-flat.x, flat.z)));
        p.connection.teleport(dest.x, dest.y, dest.z, yaw, p.getXRot());
        p.fallDistance = 0;
        p.setDeltaMovement(flat.scale(0.25));
        p.hurtMarked = true;

        if (target.hurt(p.damageSources().playerAttack(p), 10.0f)) {
            target.knockback(0.6, -flat.x, -flat.z);
            level.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + target.getBbHeight() * 0.5,
                    target.getZ(), 1, 0, 0, 0, 0);
        }
        level.playSound(null, dest.x, dest.y, dest.z, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0f, 1.2f);
        p.getCooldowns().addCooldown(handle, 60);
    }
}
