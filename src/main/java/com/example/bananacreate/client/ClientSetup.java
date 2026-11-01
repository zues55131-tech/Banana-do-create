package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.registry.ModBlockEntities;
import com.example.bananacreate.registry.ModEntities;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.joml.Vector3f;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.example.bananacreate.registry.ModMenus;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import com.example.bananacreate.registry.ModFluids;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = BananaCreate.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BANANA_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.CYBER_ROCKET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.MINI_GOBLIN.get(), MiniGoblinRenderer::new);
        event.registerEntityRenderer(ModEntities.KASANE_TETO.get(), KasaneTetoRenderer::new);
    }

    private static final String[] ROTORS = {
            "blood_extractor_rotor", "blood_centrifuge_rotor", "titan_treadmill_rotor",
            "chaos_dynamo_rotor", "death_siphon_rotor", "fear_seal_rotor",
            "blood_extractor_piston", "blood_centrifuge_agitator",
            "titan_treadmill_halo", "chaos_dynamo_halo", "death_siphon_halo", "fear_seal_halo"};

    /** Modelo de uma peca movel (ex.: "blood_extractor_rotor", "blood_extractor_piston"). */
    private static ModelResourceLocation part(String name) {
        return ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "block/" + name));
    }

    private static MachineRenderer.Part spin(String machine) {
        return new MachineRenderer.Part(part(machine + "_rotor"), MachineRenderer.Motion.SPIN);
    }

    private static MachineRenderer.Part halo(String machine) {
        return new MachineRenderer.Part(part(machine + "_halo"), MachineRenderer.Motion.ORBIT);
    }

    private static final ParticleOptions BLOOD_DUST = new DustParticleOptions(new Vector3f(0.7f, 0.05f, 0.1f), 1.0f);

    /** Carrega os modelos dos rotores (nao pertencem a nenhum blockstate). */
    @SubscribeEvent
    public static void registerRotorModels(ModelEvent.RegisterAdditional event) {
        for (String n : ROTORS) event.register(part(n));
    }

    /** Renderers que fazem as maquinas girarem como as do Create. */
    @SubscribeEvent
    public static void registerMachineRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BLOOD_EXTRACTOR.get(), c -> new MachineRenderer<>(BLOOD_DUST,
                spin("blood_extractor"),
                new MachineRenderer.Part(part("blood_extractor_piston"), MachineRenderer.Motion.PISTON)));
        event.registerBlockEntityRenderer(ModBlockEntities.BLOOD_CENTRIFUGE.get(), c -> new MachineRenderer<>(BLOOD_DUST,
                spin("blood_centrifuge"),
                new MachineRenderer.Part(part("blood_centrifuge_agitator"), MachineRenderer.Motion.SPIN_REVERSE_FAST)));
        event.registerBlockEntityRenderer(ModBlockEntities.TITAN_TREADMILL.get(), c -> new MachineRenderer<>(ParticleTypes.CLOUD,
                spin("titan_treadmill"), halo("titan_treadmill")));
        event.registerBlockEntityRenderer(ModBlockEntities.IMPLANT_TABLE.get(), ImplantTableRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CHAOS_DYNAMO.get(), c -> new MachineRenderer<>(ParticleTypes.PORTAL,
                spin("chaos_dynamo"), halo("chaos_dynamo")));
        event.registerBlockEntityRenderer(ModBlockEntities.DEATH_SIPHON.get(), c -> new MachineRenderer<>(ParticleTypes.SOUL,
                spin("death_siphon"), halo("death_siphon")));
        event.registerBlockEntityRenderer(ModBlockEntities.FEAR_SEAL.get(), c -> new MachineRenderer<>(ParticleTypes.SMOKE,
                spin("fear_seal"), halo("fear_seal")));
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.IMPLANT_EQUIP.get(), ImplantEquipScreen::new);
        event.register(ModMenus.IMPLANT_REMOVAL.get(), ImplantRemovalScreen::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(KasaneTetoModel.LAYER, KasaneTetoModel::createBodyLayer);
        event.registerLayerDefinition(MiniGoblinModel.LAYER, MiniGoblinModel::createBodyLayer);
        event.registerLayerDefinition(CyberwareModel.LAYER, CyberwareModel::createBodyLayer);
    }

    /** Cyberware visivel no corpo de qualquer jogador (skin wide e slim). */
    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new CyberwareLayer(renderer, event.getEntityModels()));
            }
        }
    }

    /** Sangue = textura de agua tingida de vermelho. */
    @SubscribeEvent
    public static void registerFluidExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.withDefaultNamespace("block/water_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.withDefaultNamespace("block/water_flow");
            }

            @Override
            public int getTintColor() {
                return 0xFFA01020;
            }
        }, ModFluids.BLOOD_TYPE.get());
    }
}
