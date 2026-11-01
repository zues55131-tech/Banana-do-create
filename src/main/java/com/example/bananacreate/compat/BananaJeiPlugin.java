package com.example.bananacreate.compat;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.item.ImplantItem;
import com.example.bananacreate.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Integracao com o JEI: cada item/bloco do addon ganha uma pagina de informacao (botao "i" ou tecla U)
 * explicando o que faz e como obter. O texto vem de "<chave do item>.desc" no lang. As receitas do Create
 * (montagem sequencial, prensa, haunting...) o proprio JEI/Create ja mostram.
 */
@JeiPlugin
public class BananaJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ModItems.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            String key = item.getDescriptionId() + ".desc";
            if (!I18n.exists(key)) return;

            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable(key));
            if (item instanceof ImplantItem implant) {
                lines.add(Component.translatable("item.bananacreate." + implant.type().key() + ".key"));
                lines.add(Component.translatable("tooltip.bananacreate.implant_table"));
            }
            registration.addItemStackInfo(new ItemStack(item), lines.toArray(new Component[0]));
        });
    }
}
