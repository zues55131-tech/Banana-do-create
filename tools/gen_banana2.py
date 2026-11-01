#!/usr/bin/env python3
"""Etapas 7+8: ferramentas de banana redesenhadas, novas variacoes de banana, itens de progressao,
folhas de bananeira (fronds) e cachos pendurados. Uso: python3 tools/gen_banana2.py (precisa de Pillow)"""
import json, math, os, random
from PIL import Image, ImageDraw

RES = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/resources/assets/bananacreate')
random.seed(23)


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
    return tuple(max(0, min(255, int(v * f))) for v in c[:3])


def outline(img, col):
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
        px[p] = tuple(col[:3]) + (255,)


def bez(p0, p1, p2, t):
    return ((1 - t) ** 2 * p0[0] + 2 * (1 - t) * t * p1[0] + t ** 2 * p2[0],
            (1 - t) ** 2 * p0[1] + 2 * (1 - t) * t * p1[1] + t ** 2 * p2[1])


def stroke(px, p0, p1, p2, rmax, rmin, body, light, dark, tipcol=None, tip_len=0.12):
    """Curva grossa em forma de banana; lado de dentro claro, de fora escuro, pontas marrons."""
    for i in range(0, 121):
        t = i / 120
        x, y = bez(p0, p1, p2, t)
        r = rmin + (rmax - rmin) * math.sin(math.pi * t)
        # normal aproximada
        x2, y2 = bez(p0, p1, p2, min(1, t + 0.01))
        x1, y1 = bez(p0, p1, p2, max(0, t - 0.01))
        tx, ty = x2 - x1, y2 - y1
        n = math.hypot(tx, ty) or 1
        nx, ny = -ty / n, tx / n
        for xx in range(16):
            for yy in range(16):
                dx, dy = xx + 0.5 - x, yy + 0.5 - y
                if math.hypot(dx, dy) <= r:
                    side = dx * nx + dy * ny
                    col = light if side < -0.35 * r else dark if side > 0.45 * r else body
                    if tipcol and (t < tip_len or t > 1 - tip_len):
                        col = tipcol
                    px[xx, yy] = col + (255,)


GREEN = (92, 176, 56); GREEN_D = (48, 120, 40)
Y = (250, 214, 58); Y_L = (255, 242, 140); Y_D = (212, 160, 28)
TIP = (92, 60, 24)
HANDLE = (116, 80, 38); HANDLE_L = (166, 122, 64)
OUT = (52, 34, 14)


def handle(px, a, b, wrap=True):
    n = int(max(abs(b[0] - a[0]), abs(b[1] - a[1])))
    for i in range(n + 1):
        x = round(a[0] + (b[0] - a[0]) * i / max(1, n))
        y = round(a[1] + (b[1] - a[1]) * i / max(1, n))
        px[x, y] = HANDLE_L + (255,)
        if x + 1 < 16:
            px[x + 1, y] = HANDLE + (255,)
        if wrap and i in (2, 3):
            px[x, y] = GREEN + (255,)
            if x + 1 < 16:
                px[x + 1, y] = GREEN_D + (255,)


def leaf_tie(px, x, y):
    for (dx, dy, c) in [(0, 0, GREEN), (1, 0, GREEN_D), (0, 1, GREEN_D), (-1, 0, GREEN), (0, -1, GREEN)]:
        if 0 <= x + dx < 16 and 0 <= y + dy < 16:
            px[x + dx, y + dy] = c + (255,)


def tool(kind):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); px = im.load()
    if kind == 'pickaxe':
        handle(px, (1, 14), (11, 4))
        stroke(px, (3.5, 2.5), (14.5, 0.5), (13.5, 12.5), 2.0, 0.9, Y, Y_L, Y_D, TIP)
        leaf_tie(px, 11, 5)
    elif kind == 'axe':
        handle(px, (1, 14), (10, 5))
        # cabeca larga: meia-lua de banana grossa com gume marrom na ponta de fora
        d = ImageDraw.Draw(im)
        d.polygon([(7, 3), (11, 1), (14, 3), (15, 8), (13, 12), (10, 9), (9, 6)], fill=Y + (255,))
        d.polygon([(7, 3), (11, 1), (13, 2), (10, 5), (9, 6)], fill=Y_L + (255,))
        d.polygon([(13, 12), (15, 8), (14, 6), (12, 9), (10, 9)], fill=Y_D + (255,))
        for (x, y) in [(15, 7), (15, 8), (14, 10), (13, 12)]:
            px[x, y] = TIP + (255,)
        px[12, 5] = (255, 250, 200, 255)
        leaf_tie(px, 9, 6)
    elif kind == 'shovel':
        handle(px, (1, 14), (9, 6))
        # pa: casca de banana aberta (oval amarelo com pintinhas)
        for x in range(16):
            for y in range(16):
                u = ((x - 11.5) + (y - 3.5)) / 1.414
                v = ((x - 11.5) - (y - 3.5)) / 1.414
                if (u / 3.6) ** 2 + (v / 2.6) ** 2 <= 1:
                    c = Y_L if v < -0.9 else Y_D if v > 1.2 else Y
                    px[x, y] = c + (255,)
        for (x, y) in [(10, 3), (12, 5), (11, 2), (13, 4)]:
            px[x, y] = TIP + (255,)
        leaf_tie(px, 9, 6)
    elif kind == 'hoe':
        handle(px, (1, 14), (11, 4))
        # lamina de banana curva saindo para a esquerda, ponta apontando para baixo
        stroke(px, (12.5, 2.5), (6.0, -0.5), (3.0, 6.5), 1.4, 0.6, Y, Y_L, Y_D, TIP, tip_len=0.14)
        leaf_tie(px, 11, 4)
    elif kind == 'sword':
        # lamina curva de banana + guarda de folha
        stroke(px, (5.5, 10.5), (12.5, 8.5), (14.5, 0.5), 1.8, 0.5, Y, Y_L, Y_D, TIP, tip_len=0.08)
        handle(px, (1, 14), (4, 11), wrap=False)
        for (x, y, c) in [(5, 9, GREEN), (6, 10, GREEN), (7, 11, GREEN_D), (4, 8, GREEN_D), (3, 12, GREEN), (2, 13, GREEN_D)]:
            pass
        for (x, y, c) in [(4, 10, GREEN), (5, 11, GREEN), (6, 12, GREEN_D), (7, 9, GREEN_D), (3, 9, GREEN_D), (2, 8, GREEN)]:
            px[x, y] = c + (255,)
    outline(im, OUT)
    save(im, f'textures/item/banana_{kind}.png')


for k in ('pickaxe', 'axe', 'shovel', 'hoe', 'sword'):
    tool(k)


# ------------------------------------------------------------------ variacoes de banana
def banana(body, light, dark, tip, stem, name, sparkle=None, spots=None):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); px = im.load()
    stroke(px, (3.0, 12.5), (13.5, 13.5), (12.5, 3.0), 2.4, 0.9, body, light, dark)
    for (x, y) in [(2, 13), (3, 13), (2, 12)]:
        px[x, y] = tip + (255,)
    for (x, y) in [(12, 2), (13, 2), (12, 1), (13, 1)]:
        px[x, y] = stem + (255,)
    if spots:
        for (x, y) in spots:
            if px[x, y][3]:
                px[x, y] = spots_col + (255,) if False else (30, 22, 10, 255)
    outline(im, shade(dark, 0.45))
    if sparkle:
        for (x, y) in sparkle:
            if px[x, y][3] == 0:
                px[x, y] = (255, 255, 255, 255)
    save(im, name)


banana((226, 52, 70), (255, 130, 140), (150, 24, 44), (70, 30, 24), (90, 110, 40), 'textures/item/red_banana.png',
       sparkle=[(5, 5), (11, 10)])
banana((104, 76, 40), (146, 112, 64), (60, 40, 22), (30, 22, 12), (70, 70, 30), 'textures/item/rotten_banana.png',
       spots=[(6, 11), (8, 10), (10, 8), (9, 6), (7, 12), (11, 5)])
# banana cosmica: corpo roxo-azulado com estrelas e brilho
banana((110, 70, 220), (170, 140, 255), (56, 30, 140), (30, 20, 70), (90, 220, 240), 'textures/item/cosmic_banana.png',
       sparkle=[(4, 6), (13, 7), (7, 3), (10, 12), (2, 9), (14, 12)])

# polpa de banana (monte cremoso com pintas)
im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(im); px = im.load()
d.ellipse([2, 6, 13, 13], fill=(246, 222, 120, 255))
d.ellipse([3, 4, 12, 10], fill=(252, 238, 160, 255))
d.ellipse([5, 3, 10, 7], fill=(255, 246, 190, 255))
for (x, y) in [(5, 9), (8, 11), (10, 8), (7, 6), (11, 11)]:
    px[x, y] = (96, 64, 28, 255)
for x in range(3, 13):
    if px[x, 12][3]:
        px[x, 12] = (206, 170, 70, 255)
outline(im, OUT); save(im, 'textures/item/banana_pulp.png')

# nucleo incompleto, banana cosmica incompleta, placa cromada incompleta
im = Image.open(os.path.join(RES, 'textures/item/banana_core.png')).convert('RGBA'); px = im.load()
for x in range(16):
    for y in range(16):
        r, g, b, a = px[x, y]
        if a:
            px[x, y] = (int(r * 0.6 + 50), int(g * 0.6 + 50), int(b * 0.6 + 40), 255)
save(im, 'textures/item/incomplete_banana_core.png')

im = Image.open(os.path.join(RES, 'textures/item/cosmic_banana.png')).convert('RGBA'); px = im.load()
for x in range(16):
    for y in range(16):
        r, g, b, a = px[x, y]
        if a:
            gcol = int((r + g + b) / 3)
            px[x, y] = (int(gcol * 0.8 + 40), int(gcol * 0.8 + 36), int(gcol * 0.7 + 30), 255)
save(im, 'textures/item/incomplete_cosmic_banana.png')

im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(im); px = im.load()
d.polygon([(2, 10), (11, 10), (14, 6), (5, 6)], fill=(150, 154, 166, 255))
d.polygon([(2, 10), (11, 10), (11, 12), (2, 12)], fill=(104, 108, 120, 255))
d.polygon([(11, 10), (14, 6), (14, 8), (11, 12)], fill=(84, 88, 100, 255))
for (x, y) in [(6, 8), (9, 8), (8, 7), (10, 9)]:
    px[x, y] = (250, 214, 58, 255)
outline(im, (40, 42, 52)); save(im, 'textures/item/incomplete_chrome_plate.png')

# chapa cromada nova: mais brilhante
im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(im); px = im.load()
d.polygon([(2, 10), (11, 10), (14, 6), (5, 6)], fill=(214, 220, 232, 255))
d.polygon([(2, 10), (11, 10), (11, 12), (2, 12)], fill=(150, 156, 172, 255))
d.polygon([(11, 10), (14, 6), (14, 8), (11, 12)], fill=(120, 126, 142, 255))
d.line([(5, 6), (14, 6)], fill=(255, 255, 255, 255))
for (x, y) in [(5, 9), (8, 8)]:
    px[x, y] = (250, 214, 58, 255)
outline(im, (40, 42, 52)); save(im, 'textures/item/chrome_plate.png')

# ------------------------------------------------------------------ folhas (fronds com furos = cutout) e cachos
def frond(name, base, hi, lo):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); px = im.load()
    # nervura central diagonal com folíolos que saem para os dois lados
    for i in range(16):
        for (x, y, c) in [(i, 15 - i, lo), (min(15, i + 1), 15 - i, hi)]:
            px[x, y] = c + (255,)
    for i in range(0, 16):
        L = 3 + (i % 3)
        for k in range(1, L + 1):
            for (x, y) in [(i - k, 15 - i - k // 2 - 0), (i + k + 1, 15 - i + k // 2 + 0)]:
                if 0 <= x < 16 and 0 <= y < 16:
                    px[x, y] = (base if k % 2 else hi) + (255,)
    # segunda nervura paralela para cobrir mais area
    for i in range(16):
        x, y = i, (7 - i) % 16
        px[x, y] = lo + (255,)
        if x + 1 < 16:
            px[x + 1, y] = hi + (255,)
        for k in range(1, 4):
            for (xx, yy) in [(x - k, y - k // 2), (x + k + 1, y + k // 2)]:
                if 0 <= xx < 16 and 0 <= yy < 16:
                    px[xx, yy] = (base if k % 2 else shade(base, 0.8)) + (255,)
    # ruido leve de cor
    for x in range(16):
        for y in range(16):
            r, g, b, a = px[x, y]
            if a:
                dlt = random.randint(-8, 8)
                px[x, y] = (max(0, min(255, r + dlt)), max(0, min(255, g + dlt)), max(0, min(255, b + dlt)), 255)
    # garante que o bloco nao fique vazado demais: preenche ~65% dos buracos com folha escura
    for x in range(16):
        for y in range(16):
            if px[x, y][3] == 0 and random.random() < 0.5:
                px[x, y] = shade(lo, 0.9 + random.random() * 0.2) + (255,)
    save(im, name)


frond('textures/block/banana_leaves.png', (60, 160, 50), (104, 200, 76), (30, 100, 36))
frond('textures/block/banana_leaves_unripe.png', (60, 160, 50), (104, 200, 76), (30, 100, 36))
frond('textures/block/banana_leaves_fruit.png', (60, 160, 50), (104, 200, 76), (30, 100, 36))


def bunch(name, body, light, dark, tip):
    """Cacho pendurado: caule marrom no topo e 5 bananas curvas penduradas."""
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); px = im.load()
    for x in range(5, 11):
        px[x, 0] = (86, 62, 30, 255)
        px[x, 1] = (110, 80, 40, 255)
    for x in range(7, 9):
        for y in range(0, 3):
            px[x, y] = (70, 120, 40, 255)
    cols = [(2, 3), (4, 4), (7, 3), (10, 4), (12, 3)]
    for (cx, top) in cols:
        for y in range(top, 15):
            w = 2 if top + 1 < y < 13 else 1
            for x in range(cx, cx + w + (1 if y > top + 1 and y < 12 else 0)):
                if x < 16:
                    side = x - cx
                    px[x, y] = (light if side == 0 else body if side == 1 else dark) + (255,)
        px[cx, 14] = tip + (255,)
        px[min(15, cx + 1), 14] = tip + (255,)
    outline(im, shade(dark, 0.4))
    save(im, name)


bunch('textures/block/banana_bunch.png', Y, Y_L, Y_D, TIP)
bunch('textures/block/banana_bunch_unripe.png', (130, 190, 60), (180, 226, 100), (84, 140, 36), (60, 90, 25))

# ------------------------------------------------------------------ modelos
def item_model(name, handheld=False):
    jwrite(f'models/item/{name}.json', {"parent": "minecraft:item/handheld" if handheld else "minecraft:item/generated",
                                         "textures": {"layer0": f"bananacreate:item/{name}"}})


for n in ['red_banana', 'rotten_banana', 'cosmic_banana', 'banana_pulp', 'incomplete_banana_core',
          'incomplete_cosmic_banana', 'incomplete_chrome_plate']:
    item_model(n)

CUT = "minecraft:cutout_mipped"


def leaf_model(name, bunch_tex=None):
    elements = [{"from": [0, 0, 0], "to": [16, 16, 16],
                 "faces": {f: {"uv": [0, 0, 16, 16], "texture": "#all"} for f in ("down", "up", "north", "south", "west", "east")}}]
    if bunch_tex:
        # cacho: dois cubos cruzados pendurados abaixo da folha (nao ocupam o bloco de baixo por inteiro)
        elements.append({"from": [3, -9, 8], "to": [13, 4, 8],
                         "faces": {"north": {"uv": [0, 0, 16, 16], "texture": "#bunch"},
                                   "south": {"uv": [0, 0, 16, 16], "texture": "#bunch"}}})
        elements.append({"from": [8, -9, 3], "to": [8, 4, 13],
                         "faces": {"west": {"uv": [0, 0, 16, 16], "texture": "#bunch"},
                                   "east": {"uv": [0, 0, 16, 16], "texture": "#bunch"}}})
    tex = {"all": "bananacreate:block/" + name, "particle": "bananacreate:block/" + name}
    if bunch_tex:
        tex["bunch"] = "bananacreate:block/" + bunch_tex
    jwrite(f'models/block/{name}.json', {"parent": "minecraft:block/block", "render_type": CUT, "textures": tex, "elements": elements})


leaf_model('banana_leaves')
leaf_model('banana_leaves_unripe', 'banana_bunch_unripe')
leaf_model('banana_leaves_fruit', 'banana_bunch')
print('ok')
