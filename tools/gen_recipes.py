#!/usr/bin/env python3
"""Etapa 8: progressao longa e dificil, com banana como energia. Reescreve as receitas em data/bananacreate/recipe.
Uso: python3 tools/gen_recipes.py"""
import json, os, glob

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/resources/data/bananacreate/recipe')
B = 'bananacreate:'


def w(rel, obj):
    p = os.path.join(BASE, rel + '.json')
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w') as f:
        json.dump(obj, f, indent=2)


def it(i):
    return {"item": i}


def shaped(name, pattern, key, result, count=1, cat="misc"):
    w(name, {"type": "minecraft:crafting_shaped", "category": cat, "pattern": pattern,
             "key": {k: it(v) for k, v in key.items()}, "result": {"id": result, "count": count}})


def shapeless(name, ings, result, count=1, cat="misc"):
    w(name, {"type": "minecraft:crafting_shapeless", "category": cat,
             "ingredients": [it(i) for i in ings], "result": {"id": result, "count": count}})


def sequenced(name, start, transitional, loops, steps, result):
    seq = []
    for s in steps:
        if s == 'press':
            seq.append({"type": "create:pressing", "ingredients": [it(transitional)], "results": [{"id": transitional}]})
        else:
            seq.append({"type": "create:deploying", "ingredients": [it(transitional), it(s)], "results": [{"id": transitional}]})
    w('sequenced/' + name, {"type": "create:sequenced_assembly", "ingredient": it(start), "loops": loops,
                            "results": [{"id": result}], "sequence": seq, "transitional_item": {"id": transitional}})


# ---------------------------------------------------------------- limpeza das receitas que mudam de tipo
for old in ['pressing/chrome_plate']:
    p = os.path.join(BASE, old + '.json')
    if os.path.exists(p):
        os.remove(p)

# =============================================================== TIER 0: bananeira
# Polpa de banana (Moinho do Create)
w('milling/banana_pulp_ripe', {"type": "create:milling", "ingredients": [it(B + 'banana')],
                               "results": [{"id": B + 'banana_pulp', "count": 2}], "processing_time": 80})
w('milling/banana_pulp_green', {"type": "create:milling", "ingredients": [it(B + 'green_banana')],
                                "results": [{"id": B + 'banana_pulp', "count": 1}], "processing_time": 80})
# Banana podre (Haunting)
w('haunting/rotten_banana', {"type": "create:haunting", "ingredients": [it(B + 'banana')],
                             "results": [{"id": B + 'rotten_banana'}]})
# Banana dourada: banana + 3 chapas de ouro
shapeless('golden_banana', [B + 'banana', 'create:golden_sheet', 'create:golden_sheet', 'create:golden_sheet'],
          B + 'golden_banana')
# Cabo
shaped('banana_handle', ["P", "P"], {"P": B + 'banana_planks'}, B + 'banana_handle', 2)

# =============================================================== TIER 1: Liga e Celula
# Liga: precisa de zinco do Create alem de ferro + polpa + banana verde
w('mixing/banana_alloy', {"type": "create:mixing", "heat_requirement": "heated",
                          "ingredients": [it('minecraft:iron_ingot'), it('create:zinc_ingot'), it(B + 'banana_pulp'),
                                          it(B + 'banana_pulp'), it(B + 'green_banana')],
                          "results": [{"id": B + 'banana_alloy', "count": 2}]})
# Celula: 2 voltas na Liga
sequenced('banana_cell', B + 'banana_alloy', B + 'incomplete_banana_cell', 2,
          ['create:copper_sheet', B + 'banana_pulp', B + 'green_banana', 'press'], B + 'banana_cell')
# Placa cromada: chapa de ferro polida com polpa (precisa da implante deployer)
sequenced('chrome_plate', 'create:iron_sheet', B + 'incomplete_chrome_plate', 1,
          [B + 'banana_pulp', 'press'], B + 'chrome_plate')

# Ferramentas: Liga + CELULA + cabo
shaped('banana_pickaxe', ["AAA", " C ", " H "], {"A": B + 'banana_alloy', "C": B + 'banana_cell', "H": B + 'banana_handle'}, B + 'banana_pickaxe', cat="equipment")
shaped('banana_axe', ["AA", "AC", " H"], {"A": B + 'banana_alloy', "C": B + 'banana_cell', "H": B + 'banana_handle'}, B + 'banana_axe', cat="equipment")
shaped('banana_shovel', ["A", "C", "H"], {"A": B + 'banana_alloy', "C": B + 'banana_cell', "H": B + 'banana_handle'}, B + 'banana_shovel', cat="equipment")
shaped('banana_hoe', ["AA", " C", " H"], {"A": B + 'banana_alloy', "C": B + 'banana_cell', "H": B + 'banana_handle'}, B + 'banana_hoe', cat="equipment")
shaped('banana_sword', ["AC", "A ", "H "], {"A": B + 'banana_alloy', "C": B + 'banana_cell', "H": B + 'banana_handle'}, B + 'banana_sword', cat="equipment")
shaped('banana_cannon', ["AAA", "ECE", " H "],
       {"A": B + 'banana_alloy', "E": 'create:electron_tube', "C": B + 'banana_cell', "H": B + 'banana_handle'}, B + 'banana_cannon', cat="equipment")

# Casing e bisturi
shaped('chrome_casing', ["PAP", "BCB", "PAP"],
       {"P": B + 'chrome_plate', "A": B + 'banana_alloy', "B": B + 'banana_cell', "C": 'create:andesite_casing'}, B + 'chrome_casing', 2, "building")
shaped('chrome_cogwheel', ["P", "ACA", "P"],
       {"P": B + 'chrome_plate', "A": B + 'banana_alloy', "C": 'create:cogwheel'}, B + 'chrome_cogwheel')
shaped('chrome_scalpel', ["P", "C", "H"], {"P": B + 'chrome_plate', "C": B + 'banana_cell', "H": B + 'banana_handle'}, B + 'chrome_scalpel', cat="equipment")

# Maquinas de sangue / paranormais (tier 1: precisam de Celulas; as paranormais de Banana Podre e essencias)
shaped('blood_extractor', ["BSB", "ICI", "AAA"],
       {"B": B + 'banana_cell', "S": 'create:shaft', "I": 'minecraft:iron_bars', "C": 'create:andesite_casing', "A": B + 'banana_alloy'}, B + 'blood_extractor')
shaped('blood_centrifuge', ["S", "BCB", "PPP"],
       {"S": 'create:shaft', "B": B + 'banana_cell', "C": 'create:brass_casing', "P": 'create:brass_sheet'}, B + 'blood_centrifuge')
shaped('death_siphon', ["DSD", "ICI", "RBR"],
       {"D": B + 'death_essence', "S": 'create:shaft', "I": 'minecraft:iron_bars', "C": 'create:andesite_casing',
        "R": B + 'rotten_banana', "B": B + 'banana_cell'}, B + 'death_siphon')
shaped('fear_seal', ["FSF", "RCR", "GBG"],
       {"F": B + 'fear_essence', "S": 'create:shaft', "R": B + 'rotten_banana', "C": 'create:brass_casing',
        "G": 'create:brass_sheet', "B": B + 'banana_cell'}, B + 'fear_seal')
shaped('chaos_dynamo', ["ESE", "SCS", "BLB"],
       {"E": 'create:electron_tube', "S": B + 'chaos_shard', "C": 'create:brass_casing', "B": B + 'banana_cell',
        "L": 'create:large_cogwheel'}, B + 'chaos_dynamo')

# Itens paranormais de combate
shapeless('blood_blade', ['minecraft:diamond_sword', B + 'blood_vial', B + 'blood_vial', B + 'blood_vial',
                          B + 'banana_cell', B + 'golden_banana'], B + 'blood_blade', cat="equipment")
shaped('otherside_eye', ["A", "AEA", "C"],
       {"A": 'minecraft:amethyst_shard', "E": 'minecraft:ender_eye', "C": B + 'banana_cell'}, B + 'otherside_eye')

# Chip neural: agora precisa de Essencia de Conhecimento
shaped('neural_chip', ["PEP", "CMC", "KB "],
       {"P": B + 'chrome_plate', "E": 'create:electron_tube', "C": 'create:copper_sheet', "M": 'create:precision_mechanism',
        "K": B + 'knowledge_essence', "B": B + 'banana_cell'}, B + 'neural_chip')
shaped('chrome_arm_blades', ["PP", "PNP", "AA"],
       {"P": B + 'chrome_plate', "N": B + 'neural_chip', "A": B + 'banana_alloy'}, B + 'chrome_arm_blades', cat="equipment")

# =============================================================== TIER 2: Nucleo (Montagem Sequencial sobre latao)
sequenced('banana_core', 'create:brass_casing', B + 'incomplete_banana_core', 2,
          [B + 'banana_cell', 'create:precision_mechanism', B + 'red_banana', 'press'], B + 'banana_core')

shaped('titan_syringe', ["IRI", "EVE", "CGC"],
       {"I": 'create:iron_sheet', "R": B + 'red_banana', "E": 'create:electron_tube', "V": B + 'blood_vial',
        "C": B + 'banana_cell', "G": 'minecraft:ghast_tear'}, B + 'titan_syringe')
shaped('titan_treadmill', ["LSL", "BCB", "IKI"],
       {"L": 'create:large_cogwheel', "S": 'create:shaft', "B": B + 'banana_cell', "C": 'create:brass_casing',
        "I": 'minecraft:iron_ingot', "K": B + 'banana_core'}, B + 'titan_treadmill')
shaped('servo_arm', ["PNP", "S", "CKC"],
       {"P": B + 'chrome_plate', "N": B + 'neural_chip', "S": 'create:shaft', "C": B + 'chrome_casing', "K": B + 'banana_core'}, B + 'servo_arm')
shaped('copyright_hammer', ["PKP", "PNP", " H "],
       {"P": B + 'chrome_plate', "K": B + 'banana_core', "N": B + 'neural_chip', "H": B + 'banana_handle'}, B + 'copyright_hammer', cat="equipment")
shaped('overclock_injector', ["N", "PVP", "RG"],
       {"N": B + 'neural_chip', "P": B + 'chrome_plate', "V": B + 'blood_vial', "R": B + 'red_banana', "G": B + 'golden_banana'}, B + 'overclock_injector')
shaped('implant_table', ["PCP", "BKB", "IBI"],
       {"P": 'create:precision_mechanism', "C": B + 'banana_core', "B": B + 'banana_cell', "K": B + 'chrome_casing', "I": 'minecraft:iron_ingot'}, B + 'implant_table')
shaped('implant_removal_table', ["PSP", "BKB", "IBI"],
       {"P": 'create:precision_mechanism', "S": B + 'chrome_scalpel', "B": B + 'banana_cell', "K": B + 'chrome_casing', "I": 'minecraft:iron_ingot'}, B + 'implant_removal_table')

# =============================================================== TIER 3: Banana Cosmica (exige as 4 essencias paranormais)
sequenced('cosmic_banana', B + 'golden_banana', B + 'incomplete_cosmic_banana', 1,
          ['minecraft:ender_pearl', B + 'death_essence', B + 'fear_essence', B + 'chaos_shard', B + 'knowledge_essence', 'press'],
          B + 'cosmic_banana')

# =============================================================== TIER 3: implantes (cada um gasta 1 Banana Cosmica + 1 Nucleo)
sequenced('gorilla_arms', B + 'chrome_casing', B + 'incomplete_gorilla_arms', 1,
          [B + 'chrome_plate', B + 'chrome_plate', 'create:large_cogwheel', 'press', 'minecraft:leather',
           B + 'banana_core', B + 'banana_cell', B + 'cosmic_banana', 'press'], B + 'gorilla_arms')
sequenced('mantis_blades', B + 'chrome_arm_blades', B + 'incomplete_mantis_blades', 1,
          ['create:precision_mechanism', 'create:brass_sheet', 'press', B + 'fear_essence',
           B + 'banana_core', B + 'banana_cell', B + 'cosmic_banana', 'press'], B + 'mantis_blades')
sequenced('projectile_launcher', B + 'chrome_casing', B + 'incomplete_projectile_launcher', 1,
          ['create:electron_tube', 'minecraft:gunpowder', 'minecraft:gunpowder', 'press', B + 'chaos_shard',
           B + 'banana_core', B + 'banana_cell', B + 'cosmic_banana', 'press'], B + 'projectile_launcher')
sequenced('monowire', 'minecraft:string', B + 'incomplete_monowire', 1,
          [B + 'chrome_plate', B + 'neural_chip', 'press', B + 'knowledge_essence',
           B + 'banana_core', B + 'banana_cell', B + 'cosmic_banana', 'press'], B + 'monowire')
print('receitas ok')
