package com.example.bananacreate.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/** Espada de banana: 20% de chance de fazer o alvo escorregar (lancado para frente e lento). */
public class BananaSwordItem extends SwordItem {
    public BananaSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        BananaPerks.slip(target, attacker);
        return result;
    }
}
