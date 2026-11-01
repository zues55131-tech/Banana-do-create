package com.example.bananacreate.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Pa de banana: as vezes solta Banana Verde ao cavar. */
public class BananaShovelItem extends ShovelItem {
    public BananaShovelItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        BananaPerks.mine(level, pos);
        return super.mineBlock(stack, level, state, pos, miner);
    }
}
