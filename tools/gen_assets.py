#!/usr/bin/env python3
"""Gera texturas (estilo Create: bordas chanfradas, rebites, bandas de latao) e modelos de bloco/item."""
import json, os, random
from PIL import Image, ImageDraw

RES = os.path.join(os.path.dirname(__file__), '..', 'src/main/resources/assets/bananacreate')
random.seed(7)

def save(img, rel):
    p = os.path.join(RES, rel); os.makedirs(os.path.dirname(p), exist_ok=True); img.save(p)

def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + ((c[3],) if len(c) > 3 else (255,))

def noise(img, amt=10):
    px = img.load()
    for x in range(img.width):
        for y in range(img.height):
            r, g, b, a = px[x, y]
            if a == 0: continue
            d = random.randint(-amt, amt)
            px[x, y] = (max(0, min(255, r + d)), max(0, min(255, g + d)), max(0, min(255, b + d)), a)

def rivet(d, x, y, base):
    d.point((x, y), shade(base, 1.45)); d.point((x + 1, y), shade(base, 0.95))
    d.point((x, y + 1), shade(base, 0.95)); d.point((x + 1, y + 1), shade(base, 0.6))

def casing(base, band, name, glow=None):
    """Revestimento tipo Create: moldura chanfrada + rebites nos cantos."""
    im = Image.new('RGBA', (16, 16), base + (255,)); d = ImageDraw.Draw(im)
    noise(im, 6)
    for i in range(16):
        d.point((i, 0), shade(band, 1.3)); d.point((0, i), shade(band, 1.3))
        d.point((i, 15), shade(band, 0.55)); d.point((15, i), shade(band, 0.55))
    d.rectangle([2, 2, 13, 13], outline=shade(base, 0.72))
    d.rectangle([3, 3, 12, 12], outline=shade(base, 1.18))
    for (x, y) in [(1, 1), (13, 1), (1, 13), (13, 13)]: rivet(d, x, y, band)
    d.rectangle([5, 5, 10, 10], fill=shade(base, 0.88))
    if glow: d.rectangle([7, 7, 8, 8], fill=glow)
    save(im, name)

BRASS = (196, 150, 70); ANDES = (143, 139, 128); CHROME = (176, 188, 200); DARK = (54, 56, 66)
CRIMSON = (150, 24, 36)

# --- blocos
casing(CHROME, BRASS, 'textures/block/chrome_casing.png', glow=(60, 220, 255, 255))
casing(ANDES, BRASS, 'textures/block/blood_extractor_side.png', glow=CRIMSON + (255,))
casing(ANDES, BRASS, 'textures/block/blood_centrifuge_side.png', glow=CRIMSON + (255,))
casing(BRASS, ANDES, 'textures/block/titan_treadmill_side.png', glow=(255, 240, 120, 255))
casing((98, 70, 140), BRASS, 'textures/block/chaos_dynamo_side.png', glow=(190, 90, 255, 255))
casing((34, 36, 46), (120, 120, 140), 'textures/block/death_siphon_side.png', glow=(120, 255, 190, 255))
casing((120, 118, 108), (210, 200, 160), 'textures/block/fear_seal_side.png', glow=(255, 170, 60, 255))
casing(DARK, CHROME, 'textures/block/cyber_base.png', glow=(255, 60, 120, 255))

def end(name, base, ring, core):
    im = Image.new('RGBA', (16, 16), base + (255,)); d = ImageDraw.Draw(im); noise(im, 5)
    d.rectangle([0, 0, 15, 15], outline=shade(base, 1.3)); d.rectangle([1, 1, 14, 14], outline=shade(base, 0.6))
    d.ellipse([3, 3, 12, 12], fill=ring + (255,), outline=shade(ring, 0.6)); d.ellipse([5, 5, 10, 10], fill=shade(ring, 0.5))
    d.rectangle([7, 7, 8, 8], fill=core + (255,))
    for (x, y) in [(1, 1), (13, 1), (1, 13), (13, 13)]: rivet(d, x, y, ring)
    save(im, name)

end('textures/block/blood_extractor_end.png', ANDES, BRASS, CRIMSON)
end('textures/block/blood_centrifuge_end.png', ANDES, BRASS, CRIMSON)
end('textures/block/titan_treadmill_end.png', BRASS, ANDES, (255, 240, 120))
end('textures/block/chaos_dynamo_end.png', (98, 70, 140), BRASS, (190, 90, 255))
end('textures/block/death_siphon_end.png', (34, 36, 46), (110, 110, 130), (120, 255, 190))
end('textures/block/fear_seal_end.png', (120, 118, 108), (210, 200, 160), (255, 170, 60))

# eixo (shaft) estilo Create
sh = Image.new('RGBA', (16, 16), ANDES + (255,)); d = ImageDraw.Draw(sh)
for y in range(16):
    d.line([(0, y), (15, y)], fill=shade(ANDES, 0.85 + 0.25 * ((y % 8) < 4)))
d.rectangle([0, 0, 15, 15], outline=shade(ANDES, 0.6)); save(sh, 'textures/block/shaft_part.png')

# metal liso / vidro de tanque / casca de banana / sangue
met = Image.new('RGBA', (16, 16), CHROME + (255,)); noise(met, 8); d = ImageDraw.Draw(met)
d.rectangle([0, 0, 15, 15], outline=shade(CHROME, 0.6)); save(met, 'textures/block/chrome_plain.png')
tank = Image.new('RGBA', (16, 16), (180, 40, 50, 210)); d = ImageDraw.Draw(tank)
d.rectangle([0, 0, 15, 15], outline=(220, 220, 230, 255)); d.line([(3, 2), (3, 13)], fill=(255, 200, 200, 200))
save(tank, 'textures/block/blood_glass.png')

peel = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(peel)
Y = (250, 214, 50); Y2 = (225, 175, 25); BR = (120, 84, 24)
d.ellipse([5, 5, 10, 10], fill=Y2 + (255,), outline=BR + (255,))
for i, (a, b) in enumerate([((8, 8), (1, 6)), ((8, 8), (7, 0)), ((8, 8), (15, 6)), ((8, 8), (14, 14)), ((8, 8), (2, 14))]):
    d.polygon([a, (b[0], b[1]), ((a[0] + b[0]) // 2 + 2, (a[1] + b[1]) // 2 + 2)], fill=Y + (255,), outline=BR + (255,))
d.point((8, 8), BR + (255,)); save(peel, 'textures/block/banana_peel.png')

# --- itens novos (16x16)
def item(name, painter):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(im); painter(d, im); save(im, f'textures/item/{name}.png')

def metal_rect(d, box, c=CHROME):
    d.rectangle(box, fill=c + (255,), outline=shade(c, 0.55)); d.line([(box[0] + 1, box[1] + 1), (box[2] - 1, box[1] + 1)], fill=shade(c, 1.35))

def essence(name, col, core):
    def p(d, im):
        d.ellipse([3, 3, 12, 12], fill=col + (255,), outline=shade(col, 0.5)); d.ellipse([5, 5, 10, 10], fill=shade(col, 1.4)); d.rectangle([7, 7, 8, 8], fill=core + (255,)); d.point((4, 4), (255, 255, 255, 255))
    item(name, p)
essence('death_essence', (60, 70, 90), (120, 255, 190))
essence('chaos_shard', (140, 70, 220), (255, 255, 255))
essence('fear_essence', (230, 140, 40), (255, 240, 160))
essence('knowledge_essence', (60, 170, 230), (255, 255, 255))

def gorilla(d, im):
    metal_rect(d, [3, 2, 12, 5], CHROME); d.rectangle([2, 5, 13, 11], fill=(52, 40, 34, 255), outline=(30, 22, 18, 255))
    for x in range(3, 13, 2): d.point((x, 7), (86, 66, 52, 255)); d.point((x + 1, 9), (86, 66, 52, 255))
    metal_rect(d, [4, 11, 11, 14], CHROME); d.point((7, 12), (255, 60, 120, 255))
item('gorilla_arms', gorilla)

def mantis(d, im):
    metal_rect(d, [1, 10, 6, 13], DARK)
    for i in range(10): d.point((5 + i, 10 - i), (230, 240, 250, 255)); d.point((6 + i, 10 - i), (170, 190, 210, 255)); d.point((7 + i, 10 - i), (120, 135, 155, 255))
    d.point((14, 1), (255, 60, 120, 255))
item('mantis_blades', mantis)

def launcher(d, im):
    metal_rect(d, [2, 6, 13, 10], CHROME); metal_rect(d, [1, 5, 4, 11], DARK); d.rectangle([11, 7, 13, 9], fill=(20, 20, 24, 255)); d.point((7, 8), (255, 160, 40, 255)); d.rectangle([5, 10, 7, 13], fill=(60, 60, 70, 255))
item('projectile_launcher', launcher)

def monowire(d, im):
    d.ellipse([2, 2, 9, 9], fill=(52, 54, 64, 255), outline=(120, 120, 140, 255)); d.ellipse([4, 4, 7, 7], fill=(255, 60, 120, 255))
    for i in range(7): d.point((8 + i, 8 + (i // 2) * (1 if i % 2 else 0)), (240, 120, 255, 255))
item('monowire', monowire)

def scalpel(d, im):
    for i in range(9): d.point((3 + i, 12 - i), (225, 235, 245, 255)); d.point((4 + i, 12 - i), (150, 165, 185, 255))
    d.rectangle([2, 12, 4, 14], fill=(54, 56, 66, 255)); d.point((11, 3), (255, 255, 255, 255))
item('chrome_scalpel', scalpel)

def mini_rocket(d, im):
    d.rectangle([6, 3, 9, 12], fill=(200, 205, 212, 255), outline=(90, 95, 105, 255)); d.polygon([(6, 3), (9, 3), (7, 0)], fill=(230, 60, 70, 255)); d.rectangle([5, 10, 10, 13], fill=(120, 124, 136, 255)); d.point((7, 14), (255, 170, 60, 255))
item('mini_rocket', mini_rocket)

def seal(d, im):
    d.ellipse([2, 2, 13, 13], outline=(255, 170, 60, 255)); d.line([(8, 3), (8, 12)], fill=(255, 170, 60, 255)); d.line([(4, 6), (12, 6)], fill=(255, 170, 60, 255))
item('fear_seal_item', seal)
