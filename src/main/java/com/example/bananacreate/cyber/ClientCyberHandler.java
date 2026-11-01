package com.example.bananacreate.cyber;

import com.example.bananacreate.registry.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/** Parte exclusiva do cliente do pacote de sincronizacao. */
public class ClientCyberHandler {
    public static void onSync(CyberNet.SyncPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        Entity e = mc.level.getEntity(payload.entityId());
        if (e instanceof Player p) {
            p.setData(ModAttachments.CYBERWARE, new CyberwareData(payload.installed(), payload.bladesOut()));
        }
    }
}
