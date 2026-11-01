#!/usr/bin/env python3
"""Separa a parte girante (rotor) de cada maquina cinetica.

- models/block/<m>.json        -> so a parte ESTATICA (carcaca, pilares...)
- models/block/<m>_rotor.json  -> eixo + parte que gira (desenhada pelo MachineRenderer)
- models/item/<m>.json         -> modelo completo (estatico + rotor) para o inventario/mao
"""
import json, os, copy
A = os.path.join(os.path.dirname(__file__), '..', 'src/main/resources/assets/bananacreate')
# indices dos elementos que giram (0 = eixo atravessando a maquina)
ROTORS = {
    'blood_extractor': [0, 9],
    'blood_centrifuge': [0, 9, 10],
    'titan_treadmill': [0, 10, 11, 12],
    'chaos_dynamo': [0, 9],
    'death_siphon': [0, 9],
    'fear_seal': [0, 9, 10, 11],
}
def rd(p):
    with open(os.path.join(A, p)) as f: return json.load(f)
def wr(p, o):
    with open(os.path.join(A, p), 'w') as f: json.dump(o, f, indent=2)

for name, idx in ROTORS.items():
    if os.path.exists(os.path.join(A, f'models/block/{name}_rotor.json')):
        print('ja separado:', name); continue
    full = rd(f'models/block/{name}.json')
    els = full['elements']
    assert els[0]['from'] == [6, 0, 6] and els[0]['to'] == [10, 16, 10], name  # eixo
    static = copy.deepcopy(full)
    static['elements'] = [e for i, e in enumerate(els) if i not in idx]
    static.pop('display', None)
    rotor = {'textures': full['textures'], 'elements': [els[i] for i in idx]}
    # item = modelo completo (como era antes), sem depender do bloco
    wr(f'models/item/{name}.json', full)
    wr(f'models/block/{name}.json', static)
    wr(f'models/block/{name}_rotor.json', rotor)
    print(name, 'estatico:', len(static['elements']), 'rotor:', len(rotor['elements']))
