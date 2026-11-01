"""Corrige a pose na mao dos itens 3D (armas/ferramentas).

Inclina o modelo 45 graus, igual a um sprite 'handheld', e usa a pose de mao
das espadas do Minecraft. Armas com o cano em -Z (canhao, laminas de braco)
sao antes giradas para apontar o cano para +X.
"""
import glob, json, os

A = "src/main/resources/assets/bananacreate/models/item"
HELD = {
    "thirdperson_righthand": {"rotation": [0, -90, 55], "translation": [0, 4.0, 0.5], "scale": [0.85, 0.85, 0.85]},
    "thirdperson_lefthand": {"rotation": [0, 90, -55], "translation": [0, 4.0, 0.5], "scale": [0.85, 0.85, 0.85]},
    "firstperson_righthand": {"rotation": [0, -90, 25], "translation": [1.13, 3.2, 1.13], "scale": [0.68, 0.68, 0.68]},
    "firstperson_lefthand": {"rotation": [0, 90, -25], "translation": [1.13, 3.2, 1.13], "scale": [0.68, 0.68, 0.68]},
    "gui": {"rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [0.6, 0.6, 0.6]},
    "ground": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.5, 0.5, 0.5]},
    "fixed": {"rotation": [0, 180, 0], "translation": [0, 0, 0], "scale": [0.55, 0.55, 0.55]},
}
TOOLS = {"copyright_hammer", "blood_blade", "goblin_hair_sword"}   # lamina/cabo em +Y
GUNS = {"banana_cannon", "chrome_arm_blades"}                      # cano em -Z

def tilt(el):
    el["rotation"] = {"origin": [8, 8, 8], "axis": "z", "angle": -45}

for name in sorted(TOOLS | GUNS):
    p = f"{A}/{name}.json"
    if not os.path.exists(p):
        print("ausente:", name); continue
    m = json.load(open(p))
    if any("rotation" in e for e in m["elements"]):
        print("ja corrigido:", name); continue
    for e in m["elements"]:
        if name in GUNS:   # (x,y,z) -> (16 - z, y, x): cano de -Z para +X
            (x1, y1, z1), (x2, y2, z2) = e["from"], e["to"]
            e["from"] = [16 - z2, y1, x1]
            e["to"] = [16 - z1, y2, x2]
        tilt(e)
    m["display"] = HELD
    json.dump(m, open(p, "w"), indent=2)
    print("corrigido:", name, "(arma)" if name in GUNS else "(ferramenta)")
