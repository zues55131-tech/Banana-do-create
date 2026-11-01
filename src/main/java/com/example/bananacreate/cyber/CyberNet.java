package com.example.bananacreate.cyber;

import com.example.bananacreate.BananaCreate;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Rede: servidor -> cliente (estado do cyberware) e cliente -> servidor (teclas de habilidade). */
@EventBusSubscriber(modid = BananaCreate.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CyberNet {

    public record SyncPayload(int entityId, int installed, boolean bladesOut) implements CustomPacketPayload {
        public static final Type<SyncPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "cyber_sync"));
        public static final StreamCodec<ByteBuf, SyncPayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, SyncPayload::entityId,
                ByteBufCodecs.VAR_INT, SyncPayload::installed,
                ByteBufCodecs.BOOL, SyncPayload::bladesOut,
                SyncPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ActionPayload(int action) implements CustomPacketPayload {
        public static final Type<ActionPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "cyber_action"));
        public static final StreamCodec<ByteBuf, ActionPayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ActionPayload::action,
                ActionPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SyncPayload.TYPE, SyncPayload.CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> ClientCyberHandler.onSync(payload)));
        registrar.playToServer(ActionPayload.TYPE, ActionPayload.CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof ServerPlayer sp) {
                        CyberAbilities.perform(sp, payload.action());
                    }
                }));
    }

    /** Manda o estado para quem esta vendo o jogador (e para ele mesmo). */
    public static void syncToAll(ServerPlayer player) {
        CyberwareData d = CyberwareData.of(player);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new SyncPayload(player.getId(), d.installed(), d.bladesOut()));
    }

    /** Manda o estado de {@code target} apenas para {@code viewer}. */
    public static void syncTo(ServerPlayer viewer, ServerPlayer target) {
        CyberwareData d = CyberwareData.of(target);
        PacketDistributor.sendToPlayer(viewer, new SyncPayload(target.getId(), d.installed(), d.bladesOut()));
    }
}
