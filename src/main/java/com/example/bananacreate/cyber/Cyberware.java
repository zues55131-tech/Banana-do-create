package com.example.bananacreate.cyber;

import java.util.Locale;

/** Implantes corporais de V. Cada um e um bit no mascara guardada no jogador. */
public enum Cyberware {
    GORILLA_ARMS(1),
    MANTIS_BLADES(2),
    PROJECTILE_LAUNCHER(4),
    MONOWIRE(8);

    public final int bit;

    Cyberware(int bit) {
        this.bit = bit;
    }

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }
}
