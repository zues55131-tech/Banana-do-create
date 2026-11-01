package com.example.bananacreate.item;

import com.example.bananacreate.cyber.CyberNet;
import com.example.bananacreate.cyber.Cyberware;
import com.example.bananacreate.cyber.CyberwareData;
import com.example.bananacreate.registry.ModAttachments;
import com.example.bananacreate.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Bisturi cromado: remove um implante instalado e devolve o item. */
public class ChromeScalpelItem extends Item {
    public ChromeScalpelItem(Properties properties) {
        super(properties);
    }

    public static Item itemFor(Cyberware c) {
        return switch (c) {
            case GORILLA_ARMS -> ModItems.GORILLA_ARMS.get();
            case MANTIS_BLADES -> ModItems.MANTIS_BLADES.get();
            case PROJECTILE_LAUNCHER -> ModItems.PROJECTILE_LAUNCHER.get();
            case MONOWIRE -> ModItems.MONOWIRE.get();
        };
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack scalpel = player.getItemInHand(hand);
        CyberwareData data = CyberwareData.of(player);

        Cyberware found = null;
        for (Cyberware c : Cyberware.values()) {
            if (data.has(c)) found = c; // remove o ultimo da lista
        }
        if (found == null) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.bananacreate.nothing_to_remove"), true);
            }
            return InteractionResultHolder.fail(scalpel);
        }

        if (!level.isClientSide) {
            player.setData(ModAttachments.CYBERWARE, data.without(found));
            if (player instanceof ServerPlayer sp) CyberNet.syncToAll(sp);
            ItemStack back = new ItemStack(itemFor(found));
            if (!player.getInventory().add(back)) player.drop(back, false);
            if (!player.getAbilities().instabuild) {
                player.hurt(level.damageSources().magic(), 3.0f);
                scalpel.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 0.8f, 1.4f);
            player.displayClientMessage(Component.translatable("message.bananacreate.removed",
                    Component.translatable("item.bananacreate." + found.key())), true);
        }
        return InteractionResultHolder.sidedSuccess(scalpel, level.isClientSide());
    }
}
