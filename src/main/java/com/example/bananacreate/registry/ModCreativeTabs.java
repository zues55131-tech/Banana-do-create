package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BananaCreate.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bananacreate.main"))
                    .icon(() -> new ItemStack(ModItems.BANANA_CANNON.get()))
                    .displayItems((params, output) -> {
                        // Bananeira
                        output.accept(ModItems.BANANA_LOG.get());
                        output.accept(ModItems.BANANA_PLANKS.get());
                        output.accept(ModItems.BANANA_LEAVES.get());
                        output.accept(ModItems.BANANA_SAPLING.get());
                        output.accept(ModItems.BANANA.get());
                        output.accept(ModItems.GREEN_BANANA.get());
                        output.accept(ModItems.GOLDEN_BANANA.get());
                        output.accept(ModItems.RED_BANANA.get());
                        output.accept(ModItems.ROTTEN_BANANA.get());
                        output.accept(ModItems.COSMIC_BANANA.get());
                        output.accept(ModItems.BANANA_PULP.get());
                        output.accept(ModItems.BANANA_HANDLE.get());
                        output.accept(ModItems.BANANA_ALLOY.get());
                        output.accept(ModItems.BANANA_CELL.get());
                        output.accept(ModItems.BANANA_CORE.get());
                        output.accept(ModItems.BANANA_PEEL.get());
                        output.accept(ModItems.BANANA_SWORD.get());
                        output.accept(ModItems.BANANA_PICKAXE.get());
                        output.accept(ModItems.BANANA_AXE.get());
                        output.accept(ModItems.BANANA_SHOVEL.get());
                        output.accept(ModItems.BANANA_HOE.get());
                        output.accept(ModItems.BANANA_CANNON.get());
                        // Sangue / paranormal
                        output.accept(ModItems.BLOOD_BUCKET.get());
                        output.accept(ModItems.BLOOD_VIAL.get());
                        output.accept(ModItems.BLOOD_BLADE.get());
                        output.accept(ModItems.OTHERSIDE_EYE.get());
                        output.accept(ModItems.BLOOD_EXTRACTOR.get());
                        output.accept(ModItems.BLOOD_CENTRIFUGE.get());
                        // Titas e copyright
                        output.accept(ModItems.TITAN_SYRINGE.get());
                        output.accept(ModItems.TITAN_TREADMILL.get());
                        output.accept(ModItems.COPYRIGHT_HAMMER.get());
                        // Mini goblin
                        output.accept(ModItems.MINI_GOBLIN_SPAWN_EGG.get());
                        output.accept(ModItems.KASANE_TETO_SPAWN_EGG.get());
                        output.accept(ModItems.GOBLIN_HAIR_TUFT.get());
                        output.accept(ModItems.HAIR_PLATE.get());
                        output.accept(ModItems.GOBLIN_HAIR_SWORD.get());
                        // Blocos roboticos
                        output.accept(ModItems.CHROME_CASING.get());
                        output.accept(ModItems.SERVO_ARM.get());
                        output.accept(ModItems.CHROME_COGWHEEL.get());
                        // Elementos paranormais (Create)
                        output.accept(ModItems.CHAOS_DYNAMO.get());
                        output.accept(ModItems.DEATH_SIPHON.get());
                        output.accept(ModItems.FEAR_SEAL.get());
                        output.accept(ModItems.DEATH_ESSENCE.get());
                        output.accept(ModItems.CHAOS_SHARD.get());
                        output.accept(ModItems.FEAR_ESSENCE.get());
                        output.accept(ModItems.KNOWLEDGE_ESSENCE.get());
                        // Cyberware corporal
                        output.accept(ModItems.GORILLA_ARMS.get());
                        output.accept(ModItems.MANTIS_BLADES.get());
                        output.accept(ModItems.PROJECTILE_LAUNCHER.get());
                        output.accept(ModItems.MONOWIRE.get());
                        output.accept(ModItems.CHROME_SCALPEL.get());
                        output.accept(ModItems.IMPLANT_TABLE.get());
                        output.accept(ModItems.IMPLANT_REMOVAL_TABLE.get());
                        // Cyberware
                        output.accept(ModItems.CHROME_PLATE.get());
                        output.accept(ModItems.NEURAL_CHIP.get());
                        output.accept(ModItems.CHROME_ARM_BLADES.get());
                        output.accept(ModItems.OVERCLOCK_INJECTOR.get());
                    })
                    .build());
}
