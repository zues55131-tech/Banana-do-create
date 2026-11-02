package com.example.bananacreate.item;

import com.example.bananacreate.entity.BananaKing;
import com.example.bananacreate.registry.ModBlocks;
import com.example.bananacreate.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** Reliquia do Rei Banana: clique numa Tabua de Bananeira para invocar o boss. */
public class BananaRelicItem extends Item {
    public BananaRelicItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(ModBlocks.BANANA_PLANKS.get())) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.sidedSuccess(true);
        }
        if (!server.getEntitiesOfClass(BananaKing.class, new AABB(pos).inflate(80.0)).isEmpty()) {
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(Component.translatable("message.bananacreate.banana_king.already"), true);
            }
            return InteractionResult.FAIL;
        }

        BananaKing boss = ModEntities.BANANA_KING.get().create(server);
        if (boss == null) return InteractionResult.FAIL;
        boss.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, context.getHorizontalDirection().toYRot(), 0.0f);
        boss.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
        server.addFreshEntity(boss);
        server.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 0.8f, 1.4f);

        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
