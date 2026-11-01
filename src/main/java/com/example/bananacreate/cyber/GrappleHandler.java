package com.example.bananacreate.cyber;

import com.example.bananacreate.BananaCreate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gancho do Monofio (tecla R): prende o fio no bloco que voce olha (ate 40 blocos) e te puxa ate la,
 * como a teia do Homem-Aranha. R de novo ou agachar solta. Termina ao chegar (com um impulso extra).
 */
@EventBusSubscriber(modid = BananaCreate.MOD_ID)
public class GrappleHandler {
    public static final double RANGE = 40.0;
    private static final int MAX_TICKS = 120;

    private static final class Grapple {
        final Vec3 anchor;
        int age;

        Grapple(Vec3 anchor) {
            this.anchor = anchor;
        }
    }

    /** So mexe no servidor (thread do jogo). */
    private static final Map<UUID, Grapple> ACTIVE = new HashMap<>();

    public static void toggle(ServerPlayer p) {
        if (ACTIVE.remove(p.getUUID()) != null) { // apertar de novo solta
            p.level().playSound(null, p.getX(), p.getY(), p.getZ(),
                    SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.0f, 1.4f);
            return;
        }
        Vec3 eye = p.getEyePosition();
        Vec3 end = eye.add(p.getLookAngle().scale(RANGE));
        BlockHitResult hit = p.level().clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, p));
        if (hit.getType() != HitResult.Type.BLOCK) {
            p.displayClientMessage(Component.translatable("message.bananacreate.grapple_no_target"), true);
            return;
        }
        ACTIVE.put(p.getUUID(), new Grapple(hit.getLocation()));
        ServerLevel level = p.serverLevel();
        level.playSound(null, p.getX(), p.getY(), p.getZ(),
                SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.6f);
        level.sendParticles(ParticleTypes.CRIT, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z,
                8, 0.1, 0.1, 0.1, 0.2);
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        Grapple g = ACTIVE.get(p.getUUID());
        if (g == null) return;

        g.age++;
        if (!p.isAlive() || p.isSpectator() || p.isPassenger()
                || !CyberwareData.of(p).has(Cyberware.MONOWIRE)
                || g.age > MAX_TICKS || (p.isShiftKeyDown() && g.age > 5)) {
            ACTIVE.remove(p.getUUID());
            return;
        }

        Vec3 hand = p.position().add(0, p.getBbHeight() * 0.7, 0);
        Vec3 to = g.anchor.subtract(hand);
        double dist = to.length();
        if (dist > RANGE + 10) {
            ACTIVE.remove(p.getUUID());
            return;
        }
        if (dist < 2.2) { // chegou: pequeno impulso para subir na borda e solta
            p.setDeltaMovement(p.getDeltaMovement().scale(0.5).add(0, 0.3, 0));
            p.hurtMarked = true;
            p.fallDistance = 0;
            ACTIVE.remove(p.getUUID());
            return;
        }

        Vec3 desired = to.normalize().scale(Math.min(1.7, 0.6 + dist * 0.08));
        Vec3 v = p.getDeltaMovement().lerp(desired, 0.4);
        p.setDeltaMovement(v);
        p.hurtMarked = true;
        p.fallDistance = 0;

        // fio visivel (a cada 2 ticks, um ponto por ~1.5 bloco)
        if (g.age % 2 == 0) {
            ServerLevel level = p.serverLevel();
            int steps = Math.max(2, (int) (dist / 1.5));
            for (int i = 0; i <= steps; i++) {
                Vec3 pt = hand.add(to.scale(i / (double) steps));
                level.sendParticles(ParticleTypes.END_ROD, pt.x, pt.y, pt.z, 1, 0, 0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ACTIVE.remove(event.getEntity().getUUID());
    }
}
