package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.item.ImplantItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Mostra "o que o item faz" em todo item do addon. Como o JEI monta o tooltip do mesmo jeito
 * que o inventario, a descricao tambem aparece la. Texto vem de "<chave do item>.desc" no lang.
 */
@EventBusSubscriber(modid = BananaCreate.MOD_ID, value = Dist.CLIENT)
public class ItemTooltips {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if (item instanceof ImplantItem) return; // implantes ja tem tooltip proprio
        if (!BananaCreate.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())) return;
        String key = item.getDescriptionId() + ".desc";
        if (I18n.exists(key)) {
            event.getToolTip().add(1, Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }
}
