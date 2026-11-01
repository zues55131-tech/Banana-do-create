package com.example.bananacreate.item;

import com.example.bananacreate.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/** Poderes das ferramentas de banana (espada escorrega o alvo; ferramentas as vezes soltam banana verde). */
public final class BananaPerks {
    private BananaPerks() {}

    /** 20%: o alvo escorrega, e lancado para frente e fica lento por 2 s. */
    public static void slip(LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();
        if (level.isClientSide || attacker.getRandom().nextFloat() >= 0.20f) return;
        Vec3 look = attacker.getLookAngle();
        Vec3 dir = new Vec3(look.x, 0.0, look.z).normalize();
        target.setDeltaMovement(target.getDeltaMovement().add(dir.x * 0.9, 0.35, dir.z * 0.9));
        target.hurtMarked = true;
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.SLIME_BLOCK_FALL, SoundSource.PLAYERS, 1.0f, 1.3f);
        if (level instanceof ServerLevel server) {
            server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.BANANA_PEEL.get())),
                    target.getX(), target.getY() + 0.3, target.getZ(), 8, 0.3, 0.1, 0.3, 0.05);
        }
    }

    /** 6%: ao quebrar um bloco, solta uma Banana Verde. */
    public static void mine(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        if (level.getRandom().nextFloat() < 0.06f) {
            Block.popResource(level, pos, new ItemStack(ModItems.GREEN_BANANA.get()));
        }
    }
}
