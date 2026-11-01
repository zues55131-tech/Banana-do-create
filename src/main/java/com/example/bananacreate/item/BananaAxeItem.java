package com.example.bananacreate.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Machado de banana: as vezes solta Banana Verde ao cortar. */
public class BananaAxeItem extends AxeItem {
    public BananaAxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        BananaPerks.mine(level, pos);
        return super.mineBlock(stack, level, state, pos, miner);
    }
}
