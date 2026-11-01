#!/usr/bin/env python3
"""Etapa 6: textura do Mini Goblin novo (64x64). Uso: python3 tools/gen_goblin.py (precisa de Pillow)"""
import os, random
from PIL import Image

RES = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/resources/assets/bananacreate')
random.seed(7)
im = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
px = im.load()

SKIN = (96, 156, 60); SKIN_L = (122, 184, 78); SKIN_D = (66, 118, 44)
EAR_IN = (214, 120, 120)
EYE = (250, 220, 40); PUPIL = (30, 20, 10); MOUTH = (60, 24, 24); TOOTH = (240, 236, 210)
HAT = (176, 128, 52); HAT_D = (128, 88, 36); LEAF = (60, 150, 52); LEAF_D = (36, 104, 38)
VEST = (150, 96, 52); VEST_D = (108, 66, 34); BUTTON = (250, 214, 58)
PANTS = (84, 70, 120); PANTS_D = (60, 50, 90); BOOT = (70, 44, 24)
PACK = (122, 84, 44); PACK_D = (86, 56, 28); BAN = (250, 214, 58); BAN_D = (206, 160, 30)


def sh(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + (255,)


def fill(x, y, w, h, c, jitter=6):
    for xx in range(x, x + w):
        for yy in range(y, y + h):
            d = random.randint(-jitter, jitter)
            px[xx, yy] = (max(0, min(255, c[0] + d)), max(0, min(255, c[1] + d)), max(0, min(255, c[2] + d)), 255)


def box(u, v, w, h, d, c, top=None, jitter=6):
    """Pinta as 6 faces de um cubo no layout padrao de skin do Minecraft."""
    fill(u + d, v, w, d, top or sh(c, 1.15), jitter)          # topo
    fill(u + d + w, v, w, d, sh(c, 0.7), jitter)               # baixo
    fill(u, v + d, d, h, sh(c, 0.88), jitter)                  # lado dir
    fill(u + d, v + d, w, h, c, jitter)                        # frente
    fill(u + d + w, v + d, d, h, sh(c, 0.88), jitter)          # lado esq
    fill(u + 2 * d + w, v + d, w, h, sh(c, 0.8), jitter)       # costas


# ---- cabeca 8x8x8 em (0,0): frente = (8,8)-(16,16)
box(0, 0, 8, 8, 8, SKIN)
# olhos grandes amarelos com pupila, sobrancelha brava, boca de dentes tortos
for ex in (9, 13):
    px[ex, 11] = EYE + (255,); px[ex + 1, 11] = EYE + (255,)
    px[ex, 12] = EYE + (255,); px[ex + 1, 12] = EYE + (255,)
px[10, 12] = PUPIL + (255,); px[13, 12] = PUPIL + (255,)
for (x, y) in [(9, 10), (10, 10), (14, 10), (15, 10)]:
    px[x, y] = SKIN_D + (255,)
for x in range(10, 14):
    px[x, 14] = MOUTH + (255,)
px[10, 14] = TOOTH + (255,); px[13, 14] = TOOTH + (255,)
px[11, 15] = MOUTH + (255,); px[12, 15] = MOUTH + (255,)
# bochechas verde-claras
px[9, 13] = SKIN_L + (255,); px[14, 13] = SKIN_L + (255,)

# ---- nariz grande 2x3x2 em (32,0)
box(32, 0, 2, 3, 2, SKIN_L)
px[35, 4] = SKIN_D + (255,)  # narina

# ---- orelhas pontudas 5x3x1: dir (40,0), esq (40,5)
for (u, v) in ((40, 0), (40, 5)):
    box(u, v, 5, 3, 1, SKIN, jitter=4)
    fill(u + 1, v + 1, 5, 3, SKIN, 3)      # frente
    for x in range(u + 2, u + 5):
        px[x, v + 2] = EAR_IN + (255,)

# ---- chapeu: base 6x3x6 em (0,16) e ponta 3x3x3 em (24,16)
box(0, 16, 6, 3, 6, HAT, jitter=8)
for x in range(6, 12):
    px[x, 22] = HAT_D + (255,)   # faixa
fill(0, 22, 24, 1, HAT_D, 2)
box(24, 16, 3, 3, 3, LEAF, jitter=8)
for i in range(3):
    px[27 + i, 19 + i % 3] = LEAF_D + (255,)

# ---- corpo 7x7x4 em (0,28): colete de couro com botoes de banana
box(0, 28, 7, 7, 4, VEST, jitter=8)
for y in range(32, 39):
    px[7 + 3, y] = VEST_D + (255,)        # costura central na frente
for y in (33, 35, 37):
    px[7 + 3, y] = BUTTON + (255,)
    px[7 + 4, y] = sh(BUTTON, 0.8)
fill(4, 32, 7, 1, SKIN, 0)               # (nada: mantem o colete inteiro)
for x in range(4, 11):
    px[x, 32] = VEST_D + (255,)
# gola de pele verde
for x in range(4, 11):
    px[x, 32] = SKIN_D + (255,)

# ---- bracos 2x6x2 em (24,28) e (32,28)
for u in (24, 32):
    box(u, 28, 2, 6, 2, SKIN, jitter=5)
    fill(u + 2, 30, 2, 2, VEST, 5)       # manga curta do colete (frente)
    fill(u + 2, 30, 2, 2, VEST, 5)

# ---- pernas 2x5x2 em (40,28) e (48,28): calca roxa + bota (2 ultimas linhas das faces laterais)
for u in (40, 48):
    box(u, 28, 2, 5, 2, PANTS, jitter=5)
    for xx in range(u, u + 8):
        for yy in (33, 34):          # faces laterais: v+d .. v+d+h-1 = 30..34
            if px[xx, yy][3]:
                px[xx, yy] = BOOT + (255,)

# ---- mochila 6x5x3 em (0,42) + banana pendurada 2x5x2 em (20,42)
box(0, 42, 6, 5, 3, PACK, jitter=8)
for x in range(3, 9):
    px[x, 45] = PACK_D + (255,)
fill(20, 42, 8, 7, BAN, 5)
box(20, 42, 2, 5, 2, BAN, jitter=5)
px[22, 45] = BAN_D + (255,); px[23, 45] = BAN_D + (255,)

out = os.path.join(RES, 'textures/entity/mini_goblin.png')
im.save(out)
print('ok', out)
