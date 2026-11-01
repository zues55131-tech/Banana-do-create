package com.example.bananacreate.item;

import com.example.bananacreate.cyber.CyberNet;
import com.example.bananacreate.cyber.Cyberware;
import com.example.bananacreate.cyber.CyberwareData;
import com.example.bananacreate.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/** Implante corporal: instalado pela Mesa de Implantes (dolorido). */
public class ImplantItem extends Item {
    private final Cyberware type;

    public ImplantItem(Cyberware type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public Cyberware type() {
        return type;
    }

    /** Implantes nao se instalam mais com botao direito: use a Mesa de Implantes. */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("message.bananacreate.use_table"), true);
        }
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    /** Instala o implante no corpo de {@code player} (so no servidor). A mesa consome o item depois. */
    public static boolean tryInstall(Player player, Cyberware type, ItemStack stack) {
        CyberwareData data = CyberwareData.of(player);
        boolean creative = player.getAbilities().instabuild;
        if (data.has(type)) {
            player.displayClientMessage(Component.translatable("message.bananacreate.already_installed"), true);
            return false;
        }
        if (!creative && player.getHealth() <= 8.0f) {
            player.displayClientMessage(Component.translatable("message.bananacreate.too_weak"), true);
            return false;
        }
        player.setData(ModAttachments.CYBERWARE, data.with(type));
        if (player instanceof ServerPlayer sp) CyberNet.syncToAll(sp);
        if (!creative) {
            player.hurt(player.level().damageSources().magic(), 6.0f);
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.8f, 1.6f);
        player.displayClientMessage(Component.translatable("message.bananacreate.installed",
                Component.translatable("item.bananacreate." + type.key())), true);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.bananacreate." + type.key() + ".desc")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.bananacreate." + type.key() + ".key")
                .withStyle(net.minecraft.ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("tooltip.bananacreate.implant_table")
                .withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
    }
}
