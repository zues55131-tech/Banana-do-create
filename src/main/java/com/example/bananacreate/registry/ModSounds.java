package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, BananaCreate.MOD_ID);

    /** Tema da transformacao em tita (8 s, mono para ter atenuacao por distancia). */
    public static final DeferredHolder<SoundEvent, SoundEvent> TITAN_TRANSFORMATION = SOUNDS.register(
            "titan_transformation",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "titan_transformation")));
}
