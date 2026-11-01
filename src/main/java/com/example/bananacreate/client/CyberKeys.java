package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.cyber.CyberAbilities;
import com.example.bananacreate.cyber.CyberNet;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Teclas das habilidades de cyberware (podem ser trocadas em Controles). */
public class CyberKeys {
    private static final String CATEGORY = "key.categories.bananacreate";
    public static final KeyMapping BLADES = new KeyMapping("key.bananacreate.blades",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping LAUNCH = new KeyMapping("key.bananacreate.launch",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping WIRE = new KeyMapping("key.bananacreate.wire",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SLAM = new KeyMapping("key.bananacreate.slam",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);

    public static final KeyMapping DASH = new KeyMapping("key.bananacreate.dash",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, CATEGORY);

    public static final KeyMapping GRAPPLE = new KeyMapping("key.bananacreate.grapple",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);

    @EventBusSubscriber(modid = BananaCreate.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Mod {
        @SubscribeEvent
        public static void register(RegisterKeyMappingsEvent event) {
            event.register(BLADES);
            event.register(LAUNCH);
            event.register(WIRE);
            event.register(SLAM);
            event.register(DASH);
            event.register(GRAPPLE);
        }
    }

    @EventBusSubscriber(modid = BananaCreate.MOD_ID, value = Dist.CLIENT)
    public static class Game {
        @SubscribeEvent
        public static void onTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) return;
            while (BLADES.consumeClick()) send(CyberAbilities.TOGGLE_BLADES);
            while (LAUNCH.consumeClick()) send(CyberAbilities.LAUNCH);
            while (WIRE.consumeClick()) send(CyberAbilities.WIRE);
            while (SLAM.consumeClick()) send(CyberAbilities.SLAM);
            while (DASH.consumeClick()) send(CyberAbilities.DASH);
            while (GRAPPLE.consumeClick()) send(CyberAbilities.GRAPPLE);
        }

        private static void send(int action) {
            PacketDistributor.sendToServer(new CyberNet.ActionPayload(action));
        }
    }
}
