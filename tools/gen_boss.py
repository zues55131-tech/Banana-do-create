"""Gera texturas, modelos, receita e traducoes do Rei Banana e da sua armadura."""
import json, os, random
from PIL import Image

A = "src/main/resources/assets/bananacreate"
D = "src/main/resources/data/bananacreate"
MOD = "bananacreate"
rng = random.Random(5)

def wj(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, indent=2, ensure_ascii=False)

def jit(c, a=10):
    return tuple(max(0, min(255, v + rng.randint(-a, a))) for v in c) + (255,)

YEL, DYEL, BRN, BLK, WHT, RED, GOLD = (250, 220, 60), (205, 170, 30), (120, 80, 30), (20, 20, 24), (245, 245, 245), (210, 40, 40), (255, 200, 40)

# ------------------------------------------------ textura do boss (layout do zumbi, 64x64)
t = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
def fill(x1, y1, x2, y2, c, a=8):
    for y in range(y1, y2):
        for x in range(x1, x2):
            t.putpixel((x, y), jit(c, a))
fill(0, 0, 64, 64, YEL, 12)                      # corpo todo amarelo-banana
fill(0, 16, 16, 32, BRN, 8); fill(16, 48, 32, 64, BRN, 8)     # calcas marrons
fill(20, 20, 28, 32, YEL, 6)                     # peito
for (x, y) in [(21, 22), (25, 26), (22, 29), (26, 21), (24, 24)]:
    t.putpixel((x, y), (*BRN, 255))              # pintinhas de banana madura
fill(20, 30, 28, 32, BRN, 4)                     # cinto
fill(8, 0, 16, 8, BRN, 8)                        # topo da cabeca: cabinho da banana
# rosto (frente da cabeca: x 8..16, y 8..16)
for (x, y) in [(10, 11), (11, 11), (13, 11), (14, 11)]: t.putpixel((x, y), (*WHT, 255))
for (x, y) in [(11, 11), (13, 11)]: t.putpixel((x, y), (*BLK, 255))
for x in range(10, 15): t.putpixel((x, 14), (*BLK, 255))      # sorrisao
for (x, y) in [(10, 13), (14, 13)]: t.putpixel((x, y), (*BLK, 255))
for x in (11, 13): t.putpixel((x, 15), (*WHT, 255))           # dentes
for (x, y) in [(10, 12), (14, 12)]: t.putpixel((x, y), (255, 150, 150, 255))   # bochechas
# coroa de casca na camada externa (hat: x 32..64, y 0..16)
fill(32, 0, 64, 8, (0, 0, 0, 0), 0)
for x in range(40, 48):
    for y in range(8, 10): t.putpixel((x, y), (*GOLD, 255))
t.save(f"{A}/textures/entity/banana_king.png")

# ------------------------------------------------ texturas da armadura (64x32)
def armor_layer(path, leg):
    im = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    for y in range(32):
        for x in range(64):
            im.putpixel((x, y), jit(GOLD if (x + y) % 7 else DYEL, 10))
    if not leg:
        for x in range(9, 15): im.putpixel((x, 12), (*RED, 255))   # viseira
    for y in range(32):
        for x in (16, 23, 24, 27): im.putpixel((x, y), (*BRN, 255))  # listras de casca
    im.save(path)
os.makedirs(f"{A}/textures/models/armor", exist_ok=True)
armor_layer(f"{A}/textures/models/armor/banana_lord_layer_1.png", False)
armor_layer(f"{A}/textures/models/armor/banana_lord_layer_2.png", True)

# ------------------------------------------------ icones
def icon(name, shape, colors=(GOLD, DYEL)):
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            v = shape(x, y)
            if v: im.putpixel((x, y), (*colors[v - 1], 255))
    im.save(f"{A}/textures/item/{name}.png")
    wj(f"{A}/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{MOD}:item/{name}"}})
def helmet(x, y):
    if 3 <= x <= 12 and 3 <= y <= 5: return 1
    if (3 <= x <= 4 or 11 <= x <= 12) and 6 <= y <= 11: return 1
    if 5 <= x <= 10 and 6 <= y <= 7: return 2
    return 0
def chest(x, y):
    if 4 <= x <= 11 and 3 <= y <= 13: return 1 if (x + y) % 5 else 2
    if (1 <= x <= 3 or 12 <= x <= 14) and 3 <= y <= 8: return 2
    return 0
def legs(x, y):
    if 3 <= x <= 12 and 2 <= y <= 4: return 2
    if (3 <= x <= 6 or 9 <= x <= 12) and 5 <= y <= 14: return 1
    return 0
def boots(x, y):
    if (2 <= x <= 6 or 9 <= x <= 13) and 8 <= y <= 12: return 1
    if (1 <= x <= 7 or 8 <= x <= 14) and 12 <= y <= 13: return 2
    return 0
icon("banana_lord_helmet", helmet); icon("banana_lord_chestplate", chest)
icon("banana_lord_leggings", legs); icon("banana_lord_boots", boots)
def relic(x, y):   # banana grande e brilhante em arco
    if abs(((x - 8) ** 2) / 7 - (y - 6)) < 2.0 and 1 <= x <= 14: return 1
    if (x - 8) ** 2 + (y - 12) ** 2 <= 4: return 2
    return 0
icon("banana_relic", relic, (YEL, GOLD))
wj(f"{A}/models/item/banana_king_spawn_egg.json", {"parent": "minecraft:item/template_spawn_egg"})

# ------------------------------------------------ receita da reliquia
wj(f"{D}/recipe/banana_relic.json", {
    "type": "minecraft:crafting_shaped", "category": "misc",
    "pattern": ["GRG", "RCR", "GRG"],
    "key": {"G": {"item": f"{MOD}:golden_banana"}, "R": {"item": f"{MOD}:red_banana"}, "C": {"item": f"{MOD}:cosmic_banana"}},
    "result": {"id": f"{MOD}:banana_relic", "count": 1}})

# ------------------------------------------------ traducoes
pt = {
 "entity.bananacreate.banana_king": "Rei Banana",
 "item.bananacreate.banana_king_spawn_egg": "Ovo do Rei Banana",
 "item.bananacreate.banana_relic": "Relíquia do Rei Banana",
 "item.bananacreate.banana_relic.desc": "Clique numa Tábua de Bananeira para invocar o Rei Banana. Cuidado!",
 "item.bananacreate.banana_lord_helmet": "Elmo do Rei Banana",
 "item.bananacreate.banana_lord_chestplate": "Peitoral do Rei Banana",
 "item.bananacreate.banana_lord_leggings": "Calças do Rei Banana",
 "item.bananacreate.banana_lord_boots": "Botas do Rei Banana",
 "message.bananacreate.banana_king.say": "<%s> %s",
 "message.bananacreate.banana_king.already": "Já existe um Rei Banana por perto!",
 "message.bananacreate.banana_king.line0": "Quem foi que invocou o bananão aqui?! Trouxeram lanche?",
 "message.bananacreate.banana_king.line1": "AGORA EU FICO MADURO DE VERDADE!",
 "message.bananacreate.banana_king.line2": "Casca no chão, tombo na certa!",
 "message.bananacreate.banana_king.line3": "Esse cacho é do tamanho do meu orgulho!",
 "message.bananacreate.banana_king.line4": "Vem dançar comigo, descascado!",
 "message.bananacreate.banana_king.line5": "Eu sou potássio em forma de gente!",
 "message.bananacreate.banana_king.line6": "Sua armadura não protege de piada ruim!",
 "message.bananacreate.banana_king.line7": "Apodreci com dignidade... descasquem o meu legado.",
}
en = {
 "entity.bananacreate.banana_king": "Banana King",
 "item.bananacreate.banana_king_spawn_egg": "Banana King Spawn Egg",
 "item.bananacreate.banana_relic": "Banana King Relic",
 "item.bananacreate.banana_relic.desc": "Use on Banana Planks to summon the Banana King. Be careful!",
 "item.bananacreate.banana_lord_helmet": "Banana King Helmet",
 "item.bananacreate.banana_lord_chestplate": "Banana King Chestplate",
 "item.bananacreate.banana_lord_leggings": "Banana King Leggings",
 "item.bananacreate.banana_lord_boots": "Banana King Boots",
 "message.bananacreate.banana_king.say": "<%s> %s",
 "message.bananacreate.banana_king.already": "A Banana King is already nearby!",
 "message.bananacreate.banana_king.line0": "Who summoned the big banana?! Did you bring snacks?",
 "message.bananacreate.banana_king.line1": "NOW I GET PROPERLY RIPE!",
 "message.bananacreate.banana_king.line2": "Peel on the floor, slip for sure!",
 "message.bananacreate.banana_king.line3": "This bunch is as big as my pride!",
 "message.bananacreate.banana_king.line4": "Come dance with me, peeled one!",
 "message.bananacreate.banana_king.line5": "I'm potassium in human form!",
 "message.bananacreate.banana_king.line6": "Your armor can't protect you from bad jokes!",
 "message.bananacreate.banana_king.line7": "I rotted with dignity... peel away my legacy.",
}
for lang, extra in (("pt_br", pt), ("en_us", en)):
    p = f"{A}/lang/{lang}.json"
    d = json.load(open(p, encoding="utf-8")); d.update(extra)
    wj(p, d)
print("ok")
