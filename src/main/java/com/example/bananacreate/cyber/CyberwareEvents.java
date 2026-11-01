package com.example.bananacreate.cyber;

import com.example.bananacreate.BananaCreate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Aplica os bonus corporais (atributos) e mantem o cliente sincronizado. */
@EventBusSubscriber(modid = BananaCreate.MOD_ID)
public class CyberwareEvents {
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, path);
    }

    private static final ResourceLocation GORILLA_DMG = id("gorilla_damage");
    private static final ResourceLocation GORILLA_KB = id("gorilla_knockback");
    private static final ResourceLocation GORILLA_REACH = id("gorilla_reach");
    private static final ResourceLocation GORILLA_MINE = id("gorilla_mining");
    private static final ResourceLocation GORILLA_SLOW = id("gorilla_weight");
    private static final ResourceLocation BLADE_DMG = id("blade_damage");
    private static final ResourceLocation BLADE_SPEED = id("blade_speed");
    private static final ResourceLocation BLADE_REACH = id("blade_reach");

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        Player p = event.getEntity();
        CyberwareData d = CyberwareData.of(p);

        boolean gorilla = d.has(Cyberware.GORILLA_ARMS);
        apply(p, Attributes.ATTACK_DAMAGE, GORILLA_DMG, 4.0, gorilla);
        apply(p, Attributes.ATTACK_KNOCKBACK, GORILLA_KB, 1.5, gorilla);
        apply(p, Attributes.BLOCK_INTERACTION_RANGE, GORILLA_REACH, 1.0, gorilla);
        apply(p, Attributes.MINING_EFFICIENCY, GORILLA_MINE, 3.0, gorilla);
        // peso extra dos bracos: um pouco mais lento
        applyScaled(p, Attributes.MOVEMENT_SPEED, GORILLA_SLOW, -0.1, gorilla);

        boolean blades = d.bladesActive();
        apply(p, Attributes.ATTACK_DAMAGE, BLADE_DMG, 5.0, blades);
        apply(p, Attributes.ATTACK_SPEED, BLADE_SPEED, 1.2, blades);
        apply(p, Attributes.ENTITY_INTERACTION_RANGE, BLADE_REACH, 0.6, blades);
    }

    private static void apply(Player p, Holder<Attribute> attr, ResourceLocation id, double amount, boolean on) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null) return;
        boolean has = inst.hasModifier(id);
        if (on && !has) {
            inst.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        } else if (!on && has) {
            inst.removeModifier(id);
        }
    }

    private static void applyScaled(Player p, Holder<Attribute> attr, ResourceLocation id, double amount, boolean on) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null) return;
        boolean has = inst.hasModifier(id);
        if (on && !has) {
            inst.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else if (!on && has) {
            inst.removeModifier(id);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) CyberNet.syncToAll(sp);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) CyberNet.syncToAll(sp);
    }

    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) CyberNet.syncToAll(sp);
    }

    /** Quando alguem passa a ver outro jogador, manda o cyberware dele. */
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer viewer && event.getTarget() instanceof ServerPlayer target) {
            CyberNet.syncTo(viewer, target);
        }
    }
}
