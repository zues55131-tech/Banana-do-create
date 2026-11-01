#!/usr/bin/env python3
"""Etapa 1: texturas de bananeira, ferramentas, variacoes de banana, Liga/Celula/Nucleo de Banana.
Uso: python3 tools/gen_banana.py   (precisa de Pillow)"""
import json, math, os, random
from PIL import Image, ImageDraw

RES = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/resources/assets/bananacreate')
random.seed(11)


def save(img, rel):
    p = os.path.join(RES, rel)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    img.save(p)


def jwrite(rel, obj):
    p = os.path.join(RES, rel)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w') as f:
        json.dump(obj, f, indent=2)


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + (255,)


def noise(img, amt=8):
    px = img.load()
    for x in range(img.width):
        for y in range(img.height):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            d = random.randint(-amt, amt)
            px[x, y] = (max(0, min(255, r + d)), max(0, min(255, g + d)), max(0, min(255, b + d)), a)


def outline(img, col):
    """Contorno escuro em volta dos pixels opacos (estilo item do Minecraft)."""
    px = img.load()
    todo = []
    for x in range(img.width):
        for y in range(img.height):
            if px[x, y][3] != 0:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < img.width and 0 <= ny < img.height and px[nx, ny][3] != 0:
                    todo.append((x, y))
                    break
    for p in todo:
        px[p] = col + (255,)


# ------------------------------------------------------------------ bananas
def banana(body, light, dark, tip, stem, name, sparkle=False):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    px = im.load()
    # curva de bezier: ponta inferior esquerda -> ponta superior direita, barriga para baixo/direita
    p0, p1, p2 = (3.0, 12.5), (13.5, 13.5), (12.5, 3.0)
    for i in range(0, 61):
        t = i / 60
        x = (1 - t) ** 2 * p0[0] + 2 * (1 - t) * t * p1[0] + t ** 2 * p2[0]
        y = (1 - t) ** 2 * p0[1] + 2 * (1 - t) * t * p1[1] + t ** 2 * p2[1]
        r = 0.9 + 1.5 * math.sin(math.pi * t)  # afina nas pontas
        for xx in range(16):
            for yy in range(16):
                d = math.hypot(xx + 0.5 - x, yy + 0.5 - y)
                if d <= r:
                    # lado de dentro da curva = claro, de fora = escuro
                    side = (xx + 0.5 - x) + (yy + 0.5 - y)
                    px[xx, yy] = (light if side < -0.4 else dark if side > 0.9 else body) + (255,)
    # pontas
    for (x, y) in [(2, 13), (3, 13), (2, 12)]:
        px[x, y] = tip + (255,)
    for (x, y) in [(12, 2), (13, 2), (12, 1), (13, 1)]:
        px[x, y] = stem + (255,)
    outline(im, shade(dark, 0.45)[:3])
    if sparkle:
        for (x, y) in [(5, 6), (13, 6), (7, 3), (10, 11)]:
            if px[x, y][3] == 0:
                px[x, y] = (255, 255, 230, 255)
        px[6, 10] = (255, 255, 255, 255)
    save(im, name)


banana((250, 214, 58), (255, 240, 130), (206, 160, 30), (74, 50, 20), (96, 130, 40), 'textures/item/banana.png')
banana((142, 196, 62), (186, 226, 110), (92, 146, 36), (60, 90, 25), (80, 110, 36), 'textures/item/green_banana.png')
banana((255, 200, 30), (255, 244, 150), (214, 140, 10), (120, 70, 10), (200, 150, 20), 'textures/item/golden_banana.png', sparkle=True)

# ------------------------------------------------------------------ ferramentas
HANDLE = (116, 80, 38)
HANDLE_L = (160, 116, 62)
METAL = (240, 200, 60)
METAL_L = (255, 238, 140)
METAL_D = (188, 138, 28)
GREEN = (120, 176, 58)
OUT = (52, 36, 14)

CH = {'l': METAL_L, 'M': METAL, 'm': METAL_D, 'g': GREEN, 'H': HANDLE, 'h': HANDLE_L}

HEADS = {
    'pickaxe': [
        "................",
        "................",
        "......llllll....",
        "....llMMMMMMm...",
        "...lMMm....MMm..",
        "...lMm......Mm..",
        "...Mm........Mm.",
        "................",
        "................", "................", "................", "................",
        "................", "................", "................", "................"],
    'axe': [
        "................",
        "................",
        "......lll.......",
        "....llMMMl......",
        "...lMMMMMMm.....",
        "...MMMMMMMm.....",
        "....mMMMmm......",
        ".....mmm........",
        "................", "................", "................", "................",
        "................", "................", "................", "................"],
    'shovel': [
        "................",
        "............ll..",
        "...........lMMl.",
        "..........lMMMm.",
        "..........MMMMm.",
        "...........mMmm.",
        "................",
        "................", "................", "................", "................", "................",
        "................", "................", "................", "................"],
    'hoe': [
        "................",
        "................",
        "......lllll.....",
        "....lMMMMMMm....",
        "....Mm...MMm....",
        "....M....mm.....",
        "................",
        "................", "................", "................", "................", "................",
        "................", "................", "................", "................"],
    'sword': [
        "..............ll",
        ".............lMl",
        "............lMm.",
        "...........lMm..",
        "..........lMm...",
        ".........lMm....",
        "........lMm.....",
        ".......lMm......",
        "......mmm.......",
        ".....mMm........",
        "................", "................", "................", "................",
        "................", "................"],
}


def tool(kind):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    px = im.load()
    n = 10 if kind != 'sword' else 6
    for i in range(n):  # cabo diagonal (2 px de largura)
        x, y = 1 + i, 14 - i
        px[x, y] = HANDLE_L + (255,)
        if x + 1 < 16:
            px[x + 1, y] = HANDLE + (255,)
        if i in (3, 7):  # faixa de banana-fibra
            px[x, y] = (96, 140, 52, 255)
    for y, row in enumerate(HEADS[kind]):
        for x, ch in enumerate(row):
            if ch in CH:
                px[x, y] = CH[ch] + (255,)
    # folhinha verde na ligacao cabo/cabeca
    if kind != 'sword':
        px[10, 5] = GREEN + (255,)
        px[9, 6] = shade(GREEN, 0.8)
    outline(im, OUT)
    save(im, f'textures/item/banana_{kind}.png')


for k in HEADS:
    tool(k)

# ------------------------------------------------------------------ itens novos
# Cabo de Bananeira
im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); px = im.load()
for i in range(12):
    x, y = 2 + i, 13 - i
    px[x, y] = (172, 132, 70, 255); px[x + 1, y] = (126, 90, 42, 255)
    if i % 4 == 2:
        px[x, y] = (110, 150, 60, 255)
outline(im, OUT); save(im, 'textures/item/banana_handle.png')

# Liga de Banana (barra)
im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(im)
d.polygon([(2, 10), (10, 10), (14, 6), (6, 6)], fill=METAL + (255,))
d.polygon([(2, 10), (10, 10), (10, 12), (2, 12)], fill=METAL_D + (255,))
d.polygon([(10, 10), (14, 6), (14, 8), (10, 12)], fill=shade(METAL_D, 0.8))
d.line([(6, 6), (14, 6)], fill=METAL_L + (255,))
for (x, y) in [(5, 9), (8, 8), (11, 7)]:
    im.putpixel((x, y), GREEN + (255,))
outline(im, OUT); save(im, 'textures/item/banana_alloy.png')


def cell(body, glow, name, bolt=True):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(im)
    d.rectangle([5, 3, 10, 14], fill=body + (255,))
    d.rectangle([5, 3, 6, 14], fill=shade(body, 1.25))
    d.rectangle([9, 3, 10, 14], fill=shade(body, 0.75))
    d.rectangle([6, 1, 9, 2], fill=(150, 150, 160, 255))      # polo
    d.rectangle([5, 3, 10, 4], fill=(196, 150, 70, 255))      # tampa de latao
    d.rectangle([5, 13, 10, 14], fill=(196, 150, 70, 255))
    if bolt:
        for (x, y) in [(8, 6), (7, 7), (8, 8), (7, 9), (8, 10), (7, 11)]:
            im.putpixel((x, y), glow + (255,))
    outline(im, OUT); save(im, name)


cell((236, 196, 50), (255, 255, 200), 'textures/item/banana_cell.png')
cell((150, 140, 80), (200, 200, 170), 'textures/item/incomplete_banana_cell.png', bolt=False)

# Nucleo de Banana
im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(im)
d.ellipse([2, 2, 13, 13], fill=(70, 74, 86, 255), outline=(40, 42, 50, 255))
d.ellipse([4, 4, 11, 11], fill=(250, 214, 58, 255))
d.ellipse([6, 6, 9, 9], fill=(255, 255, 210, 255))
d.arc([2, 2, 13, 13], 200, 290, fill=(196, 150, 70, 255))
for (x, y) in [(2, 7), (13, 8), (7, 2), (8, 13)]:
    im.putpixel((x, y), (196, 150, 70, 255))
im.putpixel((5, 5), (255, 240, 150, 255))
outline(im, OUT); save(im, 'textures/item/banana_core.png')

# ------------------------------------------------------------------ bloco: tronco, tabuas, folhas, muda
def rgb(c, f=1.0):
    return shade(c, f)


# tronco: casca palida de bananeira com aneis de cicatriz de folha
LOG = (176, 162, 92)
im = Image.new('RGBA', (16, 16), LOG + (255,)); px = im.load()
for x in range(16):
    col = random.choice([0.92, 1.0, 1.06, 0.97])
    for y in range(16):
        px[x, y] = rgb(LOG, col)
for y in (1, 6, 11):
    for x in range(16):
        off = random.randint(0, 1)
        px[x, (y + off) % 16] = rgb((104, 90, 44), 1.0)
        px[x, (y + off + 1) % 16] = rgb((132, 118, 62), 1.0)
for x in (0, 5, 10):
    for y in range(16):
        if px[x, y][:3] == rgb(LOG, 0.92)[:3] or random.random() < 0.5:
            px[x, y] = rgb((120, 130, 56), 1.0)
noise(im, 5); save(im, 'textures/block/banana_log.png')

im = Image.new('RGBA', (16, 16), (214, 200, 130, 255)); d = ImageDraw.Draw(im)
d.rectangle([0, 0, 15, 15], outline=(150, 134, 70, 255))
d.rectangle([2, 2, 13, 13], outline=(176, 162, 92, 255))
d.rectangle([4, 4, 11, 11], outline=(150, 160, 80, 255))
d.rectangle([6, 6, 9, 9], fill=(120, 140, 60, 255))
noise(im, 4); save(im, 'textures/block/banana_log_top.png')

# tabuas: tabuas horizontais amarelas com veios e pregos
PL = (232, 204, 104)
im = Image.new('RGBA', (16, 16), PL + (255,)); d = ImageDraw.Draw(im); px = im.load()
for y in (0, 4, 8, 12):
    d.line([(0, y), (15, y)], fill=rgb(PL, 0.72))
    d.line([(0, y + 1), (15, y + 1)], fill=rgb(PL, 1.1))
for y in (2, 6, 10, 14):
    for x in range(16):
        if (x * 3 + y) % 7 == 0:
            px[x, y] = rgb(PL, 0.88)
for (x, y) in [(1, 2), (14, 2), (1, 6), (14, 6), (1, 10), (14, 10), (1, 14), (14, 14)]:
    px[x, y] = (120, 90, 40, 255)
noise(im, 5); save(im, 'textures/block/banana_planks.png')


def leaves(name, fruit=None):
    base = (46, 130, 40)
    im = Image.new('RGBA', (16, 16), base + (255,)); px = im.load()
    noise(im, 12)
    # nervura central diagonal + folíolos (padrao de folha de palmeira/bananeira)
    for i in range(16):
        px[i, 15 - i] = (24, 84, 28, 255)
        if i + 1 < 16:
            px[i + 1, 15 - i] = (96, 176, 70, 255)
    for i in range(1, 15, 2):
        for k in range(1, 4):
            for (x, y) in [(i - k, 15 - i - k), (i + k + 1, 15 - i + k)]:
                if 0 <= x < 16 and 0 <= y < 16:
                    px[x, y] = (30, 98, 34, 255) if k % 2 else (74, 156, 58, 255)
    if fruit:
        col, hi, lo = fruit
        for (cx, cy) in [(3, 3), (10, 2), (6, 8), (12, 9), (3, 13), (9, 13)]:
            for (dx, dy, c) in [(0, 0, col), (1, 0, hi), (0, 1, col), (1, 1, lo), (0, 2, lo)]:
                if cx + dx < 16 and cy + dy < 16:
                    px[cx + dx, cy + dy] = c + (255,)
    save(im, name)


leaves('textures/block/banana_leaves.png')
leaves('textures/block/banana_leaves_unripe.png', ((134, 192, 60), (176, 224, 100), (84, 138, 36)))
leaves('textures/block/banana_leaves_fruit.png', ((250, 214, 58), (255, 244, 150), (200, 150, 26)))

# muda: tufo pequeno de folhas de bananeira (fundo transparente)
im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); px = im.load()
for y in range(8, 16):
    px[7, y] = (150, 170, 80, 255); px[8, y] = (110, 130, 56, 255)
leaf = [((7, 9), (-1, -1)), ((8, 9), (1, -1)), ((7, 11), (-1, 0)), ((8, 11), (1, 0)), ((7, 7), (0, -1))]
for (sx, sy), (dx, dy) in leaf:
    x, y = sx, sy
    for k in range(5 if dy != 0 or dx == 0 else 4):
        x += dx if dx else 0
        y += dy if dy else 0
        if 0 <= x < 16 and 0 <= y < 16:
            px[x, y] = (62, 156, 54, 255) if k % 2 == 0 else (90, 186, 70, 255)
            if dx != 0 and 0 <= y + 1 < 16:
                px[x, y + 1] = (40, 112, 40, 255)
outline(im, (24, 70, 26)); save(im, 'textures/block/banana_sapling.png')

# ------------------------------------------------------------------ modelos
for n in ['green_banana', 'golden_banana', 'banana_handle', 'banana_alloy', 'banana_cell',
          'banana_core', 'incomplete_banana_cell']:
    jwrite(f'models/item/{n}.json', {"parent": "minecraft:item/generated",
                                      "textures": {"layer0": f"bananacreate:item/{n}"}})
jwrite('models/block/banana_leaves_unripe.json', {"parent": "minecraft:block/cube_all",
                                                   "textures": {"all": "bananacreate:block/banana_leaves_unripe"}})
jwrite('blockstates/banana_leaves.json', {"variants": {
    "age=0": {"model": "bananacreate:block/banana_leaves"},
    "age=1": {"model": "bananacreate:block/banana_leaves_unripe"},
    "age=2": {"model": "bananacreate:block/banana_leaves_fruit"}}})
print("ok")
