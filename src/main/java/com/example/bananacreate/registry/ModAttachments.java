package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.cyber.CyberwareData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BananaCreate.MOD_ID);

    public static final Supplier<AttachmentType<CyberwareData>> CYBERWARE = ATTACHMENTS.register("cyberware",
            () -> AttachmentType.builder(() -> CyberwareData.EMPTY)
                    .serialize(CyberwareData.CODEC)
                    .copyOnDeath()
                    .build());
}
