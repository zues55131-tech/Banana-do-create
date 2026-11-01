"""Gera as pecas moveis das maquinas (estilo Create) e texturas animadas (neon pulsando).

Uso: python3 tools/gen_anim_parts.py   (na raiz do projeto)
"""
import json, math, os, struct, zlib
from PIL import Image

A = "src/main/resources/assets/bananacreate"
MOD = "bananacreate"

def wj(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, indent=2)

# ---------------------------------------------------------- texturas neon animadas
FRAMES = 8
NEON = {"neon_cyan": (40, 230, 240), "neon_magenta": (255, 70, 210),
        "neon_red": (255, 40, 50), "neon_orange": (255, 130, 30)}
def pulse(i):  # 0.5 .. 1.0
    return 0.5 + 0.5 * (0.5 + 0.5 * math.sin(2 * math.pi * i / FRAMES))
for name, (r, g, b) in NEON.items():
    im = Image.new("RGBA", (16, 16 * FRAMES))
    for i in range(FRAMES):
        f = pulse(i)
        for y in range(16):
            for x in range(16):
                edge = x in (0, 15) or y in (0, 15)
                k = f * (0.75 if edge else 1.0)
                im.putpixel((x, i * 16 + y), (int(r * k), int(g * k), int(b * k), 255))
    im.save(f"{A}/textures/block/{name}.png")
    wj(f"{A}/textures/block/{name}.png.mcmeta", {"animation": {"frametime": 3, "interpolate": True}})

# ---------------------------------------------------------- paleta dos itens com brilho pulsando
src = Image.open(f"{A}/textures/item/palette.png").convert("RGBA")
base = src.crop((0, 0, 16, 16)) if src.size[1] >= 16 else src
def is_neon(px):
    r, g, b, a = px
    if a == 0: return False
    mx, mn = max(r, g, b), min(r, g, b)
    if r > 200 and g > 200 and b < 110: return False     # amarelos/dourados nao brilham
    return mx > 200 and (mx - mn) > 100
strip = Image.new("RGBA", (16, 16 * FRAMES))
for i in range(FRAMES):
    f = pulse(i) + 0.0
    for y in range(16):
        for x in range(16):
            px = base.getpixel((x, y))
            if is_neon(px):
                px = (int(px[0] * f), int(px[1] * f), int(px[2] * f), px[3])
            strip.putpixel((x, i * 16 + y), px)
strip.save(f"{A}/textures/item/palette.png")
wj(f"{A}/textures/item/palette.png.mcmeta", {"animation": {"frametime": 4, "interpolate": True}})

# ---------------------------------------------------------- modelos das pecas
def el(frm, to, tex):
    return {"from": list(frm), "to": list(to), "faces": {f: {"texture": tex} for f in ["north", "east", "south", "west", "up", "down"]}}

def part(name, elements, neon):
    wj(f"{A}/models/block/{name}.json", {
        "textures": {"metal": f"{MOD}:block/chrome_plain", "neon": f"{MOD}:block/{neon}",
                     "particle": f"{MOD}:block/chrome_plain"},
        "elements": elements})

# pistao do extrator: placa + 4 agulhas com ponta neon (desce e sobe como a prensa do Create)
needles = [el((3, 12.2, 3), (13, 13.6, 13), "#metal")]
for (x, z) in [(4, 4), (11, 4), (4, 11), (11, 11)]:
    needles.append(el((x, 8, z), (x + 1, 12.2, z + 1), "#metal"))
    needles.append(el((x, 7, z), (x + 1, 8, z + 1), "#neon"))
part("blood_extractor_piston", needles, "neon_red")

# agitador da centrifuga: pas em cruz com pontas neon (gira ao contrario, rapido)
blades = [el((1, 13, 7), (15, 14, 9), "#metal"), el((7, 13, 1), (9, 14, 15), "#metal"),
          el((7, 13, 7), (9, 14.6, 9), "#neon")]
for (x, z) in [(0.4, 7.4), (14.6, 7.4), (7.4, 0.4), (7.4, 14.6)]:
    blades.append(el((x, 13.1, z), (x + 1, 13.9, z + 1.2 if x in (0.4, 14.6) else z + 1), "#neon"))
part("blood_centrifuge_agitator", blades, "neon_red")

# aneis neon que orbitam (esteira, dinamo do caos, sifao da morte, selo do medo)
def halo(name, neon):
    ring = []
    for k in range(8):
        a = k * math.pi / 4
        x, z = 8 + 6.8 * math.cos(a), 8 + 6.8 * math.sin(a)
        ring.append(el((x - 0.7, 14.6, z - 0.7), (x + 0.7, 15.6, z + 0.7), "#neon" if k % 2 == 0 else "#metal"))
    part(name, ring, neon)
halo("titan_treadmill_halo", "neon_orange")
halo("chaos_dynamo_halo", "neon_magenta")
halo("death_siphon_halo", "neon_red")
halo("fear_seal_halo", "neon_cyan")
print("ok")
