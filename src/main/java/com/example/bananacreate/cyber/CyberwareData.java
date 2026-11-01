package com.example.bananacreate.cyber;

import com.example.bananacreate.registry.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;

/** Estado de cyberware de um jogador (salvo no mundo, copiado na morte, sincronizado com o cliente). */
public record CyberwareData(int installed, boolean bladesOut) {
    public static final CyberwareData EMPTY = new CyberwareData(0, false);

    public static final Codec<CyberwareData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("installed").forGetter(CyberwareData::installed),
            Codec.BOOL.fieldOf("blades_out").forGetter(CyberwareData::bladesOut)
    ).apply(i, CyberwareData::new));

    public static CyberwareData of(Player player) {
        return player.getData(ModAttachments.CYBERWARE);
    }

    public boolean has(Cyberware c) {
        return (installed & c.bit) != 0;
    }

    /** Laminas de louva-a-deus instaladas E estendidas. */
    public boolean bladesActive() {
        return has(Cyberware.MANTIS_BLADES) && bladesOut;
    }

    public CyberwareData with(Cyberware c) {
        return new CyberwareData(installed | c.bit, bladesOut);
    }

    public CyberwareData without(Cyberware c) {
        boolean out = c == Cyberware.MANTIS_BLADES ? false : bladesOut;
        return new CyberwareData(installed & ~c.bit, out);
    }

    public CyberwareData withBlades(boolean out) {
        return new CyberwareData(installed, out);
    }
}
