package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ModArmor {
    public static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, BananaCreate.MOD_ID);

    /**
     * Armadura do Rei Banana: MELHOR que netherita.
     * Defesa total 24 (netherita 20), resistencia 4 (netherita 3),
     * repulsao 0.2 por peca (netherita 0.1) e mais durabilidade (fator 50, netherita 37).
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BANANA_LORD = MATERIALS.register("banana_lord",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 4);
                        map.put(ArmorItem.Type.LEGGINGS, 7);
                        map.put(ArmorItem.Type.CHESTPLATE, 9);
                        map.put(ArmorItem.Type.HELMET, 4);
                        map.put(ArmorItem.Type.BODY, 13);
                    }),
                    25,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(ModItems.GOLDEN_BANANA.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "banana_lord"))),
                    4.0f,
                    0.2f));
}
