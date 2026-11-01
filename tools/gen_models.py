#!/usr/bin/env python3
"""Gera modelos de bloco/item detalhados (estilo Create) e a textura do cyberware vestivel."""
import json, os, random
from PIL import Image, ImageDraw
RES = os.path.join(os.path.dirname(__file__), '..', 'src/main/resources')
A = os.path.join(RES, 'assets/bananacreate')
random.seed(11)

def wj(rel, obj):
    p = os.path.join(A, rel); os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w') as f: json.dump(obj, f, indent=2)

def box(f, t, side, end=None, rot=None, shade=True):
    end = end or side
    faces = {}
    for k in ('north', 'south', 'east', 'west'): faces[k] = {'texture': side}
    faces['up'] = {'texture': end}; faces['down'] = {'texture': end}
    e = {'from': f, 'to': t, 'faces': faces}
    if not shade: e['shade'] = False
    if rot: e['rotation'] = rot
    return e

DISPLAY = {
  'gui': {'rotation': [30, 225, 0], 'scale': [0.62, 0.62, 0.62]},
  'ground': {'scale': [0.25, 0.25, 0.25], 'translation': [0, 3, 0]},
  'fixed': {'scale': [0.5, 0.5, 0.5]},
  'thirdperson_righthand': {'rotation': [75, 45, 0], 'translation': [0, 2.5, 0], 'scale': [0.375, 0.375, 0.375]},
  'firstperson_righthand': {'rotation': [0, 45, 0], 'scale': [0.4, 0.4, 0.4]},
}

def model(rel, textures, elements, display=True):
    m = {'textures': textures, 'elements': elements}
    if display: m['display'] = DISPLAY
    wj(f'models/block/{rel}.json', m)

def axis_state(name):
    wj(f'blockstates/{name}.json', {'variants': {
        'axis=y': {'model': f'bananacreate:block/{name}'},
        'axis=z': {'model': f'bananacreate:block/{name}', 'x': 90},
        'axis=x': {'model': f'bananacreate:block/{name}', 'x': 90, 'y': 90}}})
    wj(f'models/item/{name}.json', {'parent': f'bananacreate:block/{name}'})

def machine(name, extra, tex_prefix):
    tx = {'side': f'bananacreate:block/{tex_prefix}_side', 'end': f'bananacreate:block/{tex_prefix}_end',
          'shaft': 'bananacreate:block/shaft_part', 'metal': 'bananacreate:block/chrome_plain',
          'glass': 'bananacreate:block/blood_glass', 'cas': 'bananacreate:block/chrome_casing',
          'particle': f'bananacreate:block/{tex_prefix}_side'}
    els = [
      box([6, 0, 6], [10, 16, 10], '#shaft'),                      # eixo atravessando (Create)
      box([1, 0, 1], [15, 5, 15], '#side', '#end'),                 # base
      box([0.5, 4.5, 0.5], [15.5, 5.5, 15.5], '#metal'),           # cinta metalica
      box([0.5, 10.5, 0.5], [15.5, 11.5, 15.5], '#metal'),
      box([2, 11.5, 2], [14, 16, 14], '#side', '#end'),             # cabeca
      box([1.5, 5.5, 1.5], [3, 10.5, 3], '#metal'),                  # 4 pilares
      box([13, 5.5, 1.5], [14.5, 10.5, 3], '#metal'),
      box([1.5, 5.5, 13], [3, 10.5, 14.5], '#metal'),
      box([13, 5.5, 13], [14.5, 10.5, 14.5], '#metal'),
    ] + extra
    model(name, tx, els)
    axis_state(name)

# Extrator: tanque de sangue + agulhas
machine('blood_extractor', [
    box([3.5, 5.5, 3.5], [12.5, 10.5, 12.5], '#glass'),
    box([4, 14.8, 4], [5, 16, 5], '#metal'), box([11, 14.8, 4], [12, 16, 5], '#metal'),
    box([4, 14.8, 11], [5, 16, 12], '#metal'), box([11, 14.8, 11], [12, 16, 12], '#metal'),
    box([7.5, 14.8, 7.5], [8.5, 16, 8.5], '#metal'),
    box([14.5, 6, 6], [16, 9, 10], '#metal')], 'blood_extractor')
# Centrifuga: tambor + cano de saida
machine('blood_centrifuge', [
    box([3, 5.5, 3], [13, 10.5, 13], '#glass'),
    box([2.5, 7, 2.5], [13.5, 8, 13.5], '#metal'),
    box([14.5, 1.5, 6], [16, 4.5, 10], '#metal'), box([0, 12, 6], [1.5, 15, 10], '#metal'),
    box([6.2, 16, 6.2], [9.8, 16.01, 9.8], '#cas')], 'blood_centrifuge')
# Esteira de Tita: roda de pisar
machine('titan_treadmill', [
    box([3, 5.5, 3], [13, 10.5, 13], '#cas'),
    box([1, 6.5, 6.5], [15, 9.5, 9.5], '#metal'), box([6.5, 6.5, 1], [9.5, 9.5, 15], '#metal'),
    box([0, 7, 7], [16, 9, 9], '#shaft')], 'titan_treadmill')
# Novos: Sinfonia paranormal
machine('chaos_dynamo', [
    box([3.5, 5.5, 3.5], [12.5, 10.5, 12.5], '#cas'),
    box([5, 5.5, 0], [11, 10.5, 1.5], '#metal'), box([5, 5.5, 14.5], [11, 10.5, 16], '#metal'),
    box([0, 5.5, 5], [1.5, 10.5, 11], '#metal'), box([14.5, 5.5, 5], [16, 10.5, 11], '#metal')], 'chaos_dynamo')
machine('death_siphon', [
    box([3, 5.5, 3], [13, 10.5, 13], '#cas'),
    box([4, 11.5, 0.5], [5, 15.5, 2], '#metal'), box([11, 11.5, 0.5], [12, 15.5, 2], '#metal'),
    box([4, 11.5, 14], [5, 15.5, 15.5], '#metal'), box([11, 11.5, 14], [12, 15.5, 15.5], '#metal')], 'death_siphon')
machine('fear_seal', [
    box([3.5, 5.5, 3.5], [12.5, 10.5, 12.5], '#cas'),
    box([7, 10.5, 1], [9, 11.5, 15], '#metal'), box([1, 10.5, 7], [15, 11.5, 9], '#metal')], 'fear_seal')

# Engrenagem cromada (eixo X/Y/Z)
tx = {'metal': 'bananacreate:block/chrome_plain', 'shaft': 'bananacreate:block/shaft_part', 'cas': 'bananacreate:block/chrome_casing', 'particle': 'bananacreate:block/chrome_plain'}
els = [box([6, 0, 6], [10, 16, 10], '#shaft'), box([0, 6.5, 0], [16, 9.5, 16], '#metal')]
for i in range(1):
    import math
    a = i * 45
    els.append(box([6.5, 6, 0], [9.5, 10, 2], '#metal', rot={'origin': [8, 8, 8], 'axis': 'y', 'angle': 0}) if False else box([6.5, 6, 0], [9.5, 10, 2], '#cas'))
model('chrome_cogwheel', tx, [e for e in els])
axis_state('chrome_cogwheel')

# Braco servo (articulado)
tx = {'metal': 'bananacreate:block/chrome_plain', 'cas': 'bananacreate:block/chrome_casing', 'dark': 'bananacreate:block/cyber_base', 'particle': 'bananacreate:block/chrome_plain'}
els = [box([2, 0, 2], [14, 3, 14], '#cas', '#dark'), box([4, 3, 4], [12, 5, 12], '#dark'),
       box([5.5, 5, 5.5], [10.5, 12, 10.5], '#metal'), box([4.5, 11, 4.5], [11.5, 13, 11.5], '#cas'),
       box([6.5, 12, 0], [9.5, 14.5, 7], '#metal'), box([6, 10.5, -0.5], [7.2, 13, 3], '#dark'), box([8.8, 10.5, -0.5], [10, 13, 3], '#dark'),
       box([7.2, 11.5, 0.5], [8.8, 12.5, 2.5], '#cas'), box([7, 5, 3.5], [9, 6, 5.5], '#cas')]
model('servo_arm', tx, els)

# Casca de banana 3D (petalas abertas curvando para cima)
tx = {'p': 'bananacreate:block/banana_peel', 'particle': 'bananacreate:block/banana_peel'}
def pl(f, t, rot=None):
    e = box(f, t, '#p'); 
    if rot: e['rotation'] = rot
    return e
els = [pl([5.5, 0, 5.5], [10.5, 1.6, 10.5]),
       pl([10.5, 0.1, 6.2], [15.2, 0.8, 9.8], {'origin': [10.5, 0.4, 8], 'axis': 'z', 'angle': 22.5}),
       pl([0.8, 0.1, 6.2], [5.5, 0.8, 9.8], {'origin': [5.5, 0.4, 8], 'axis': 'z', 'angle': -22.5}),
       pl([6.2, 0.1, 10.5], [9.8, 0.8, 15.2], {'origin': [8, 0.4, 10.5], 'axis': 'x', 'angle': -22.5}),
       pl([6.2, 0.1, 0.8], [9.8, 0.8, 5.5], {'origin': [8, 0.4, 5.5], 'axis': 'x', 'angle': 22.5}),
       pl([7.2, 1.6, 7.2], [8.8, 3.4, 8.8], {'origin': [8, 1.6, 8], 'axis': 'x', 'angle': 22.5}),
       pl([10.3, 0.1, 10.3], [13, 0.7, 13]), pl([3, 0.1, 3], [5.7, 0.7, 5.7])]
model('banana_peel', tx, els)
wj('blockstates/banana_peel.json', {'variants': {'': {'model': 'bananacreate:block/banana_peel'}}})
wj('models/item/banana_peel.json', {'parent': 'bananacreate:block/banana_peel', 'display': {
    'gui': {'rotation': [30, 225, 0], 'scale': [1.2, 1.2, 1.2], 'translation': [0, -1, 0]},
    'ground': {'scale': [0.7, 0.7, 0.7]}, 'fixed': {'scale': [1, 1, 1]},
    'thirdperson_righthand': {'rotation': [0, 0, 0], 'translation': [0, 3, 1], 'scale': [0.7, 0.7, 0.7]},
    'firstperson_righthand': {'rotation': [0, 45, 0], 'scale': [0.8, 0.8, 0.8]}}})

# blocos de bloco simples
wj('models/block/chrome_casing.json', {'parent': 'minecraft:block/cube_all', 'textures': {'all': 'bananacreate:block/chrome_casing'}})
wj('blockstates/chrome_casing.json', {'variants': {'': {'model': 'bananacreate:block/chrome_casing'}}})
wj('models/item/chrome_casing.json', {'parent': 'bananacreate:block/chrome_casing'})

# itens novos 2D
for n in ['gorilla_arms', 'mantis_blades', 'projectile_launcher', 'monowire', 'chrome_scalpel', 'mini_rocket',
          'death_essence', 'chaos_shard', 'fear_essence', 'knowledge_essence']:
    wj(f'models/item/{n}.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': f'bananacreate:item/{n}'}})
# itens intermediarios das montagens sequenciais do Create
for n, tex in [('incomplete_gorilla_arms', 'gorilla_arms'), ('incomplete_mantis_blades', 'mantis_blades'),
               ('incomplete_projectile_launcher', 'projectile_launcher'), ('incomplete_monowire', 'monowire')]:
    wj(f'models/item/{n}.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': f'bananacreate:item/{tex}'}})
wj('models/item/fear_seal_item.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': 'bananacreate:item/fear_seal_item'}})

# ---------------- textura do cyberware vestivel (64x64)
FUR = (58, 44, 36); CH = (176, 188, 200); DK = (44, 46, 56)
img = Image.new('RGBA', (64, 64), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
def sh(c, f): return tuple(max(0, min(255, int(v * f))) for v in c)
def region(x, y, w, h, fn):
    for i in range(w):
        for j in range(h): img.putpixel((x + i, y + j), fn(i, j, w, h) + (255,))
def fur(i, j, w, h):
    base = FUR; n = random.randint(-9, 9); s = 0.82 if (i + j * 2) % 5 == 0 else 1.0
    return tuple(max(0, min(255, int(v * s) + n)) for v in base)
def chrome(i, j, w, h):
    c = CH; n = random.randint(-6, 6)
    if i == 0 or j == 0: c = sh(CH, 1.25)
    elif i == w - 1 or j == h - 1: c = sh(CH, 0.6)
    elif i % 8 == 4 or j % 8 == 4: c = sh(CH, 0.78)
    if (i % 8, j % 8) in ((1, 1), (6, 1), (1, 6), (6, 6)): c = sh(CH, 1.5)
    if j % 8 == 2 and 2 < i % 8 < 5: c = (255, 60, 120)
    return tuple(max(0, min(255, v + n)) for v in c)
def dark(i, j, w, h):
    c = DK
    if i == 0 or j == 0: c = sh(DK, 1.8)
    if (i + j) % 7 == 0: c = (255, 60, 120)
    return c
def blade(i, j, w, h):
    t = 235 - abs(i - w // 2) * 25 - (j * 40 // max(1, h)); return (max(80, t), max(95, t + 5), max(110, t + 15))
def glow(i, j, w, h):
    return (255, 150, 40) if (i + j) % 3 else (255, 220, 120)
def paint_box(u, v, w, h, d, fn):
    region(u + d, v, w, d, fn); region(u + d + w, v, w, d, fn)
    region(u, v + d, d, h, fn); region(u + d, v + d, w, h, fn); region(u + d + w, v + d, d, h, fn); region(u + 2 * d + w, v + d, w, h, fn)
paint_box(0, 0, 7, 9, 7, fur); paint_box(0, 16, 8, 9, 8, chrome); paint_box(0, 34, 7, 3, 7, chrome)
paint_box(32, 0, 2, 10, 2, dark); paint_box(40, 0, 5, 4, 2, dark); paint_box(40, 8, 1, 14, 3, blade); paint_box(50, 8, 1, 6, 2, blade)
paint_box(0, 46, 4, 4, 10, chrome); paint_box(30, 46, 5, 3, 5, dark); paint_box(52, 46, 3, 3, 2, glow); paint_box(30, 54, 6, 3, 6, chrome)
p = os.path.join(A, 'textures/entity/cyberware.png'); os.makedirs(os.path.dirname(p), exist_ok=True); img.save(p)
print('ok')
