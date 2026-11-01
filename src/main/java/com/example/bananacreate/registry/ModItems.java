package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.item.BananaCannonItem;
import com.example.bananacreate.item.BananaAxeItem;
import com.example.bananacreate.item.BananaHoeItem;
import com.example.bananacreate.item.BananaPickaxeItem;
import com.example.bananacreate.item.BananaShovelItem;
import com.example.bananacreate.item.BananaSwordItem;
import com.example.bananacreate.item.BananaTier;
import com.example.bananacreate.item.BloodBladeItem;
import com.example.bananacreate.cyber.Cyberware;
import com.example.bananacreate.item.ChromeArmBladesItem;
import com.example.bananacreate.item.ChromeScalpelItem;
import com.example.bananacreate.item.ImplantItem;
import com.example.bananacreate.item.CopyrightHammerItem;
import com.example.bananacreate.item.GoblinHairSwordItem;
import com.example.bananacreate.item.OthersideEyeItem;
import com.example.bananacreate.item.OverclockInjectorItem;
import com.example.bananacreate.item.TitanSyringeItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BananaCreate.MOD_ID);

    // --- Banana ---
    public static final DeferredItem<Item> BANANA = ITEMS.register("banana",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).build())));

    // --- Variacoes de banana e materiais da progressao ---
    public static final DeferredItem<Item> GREEN_BANANA = ITEMS.register("green_banana",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).build())));

    public static final DeferredItem<Item> GOLDEN_BANANA = ITEMS.register("golden_banana",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).food(
                    new FoodProperties.Builder().nutrition(6).saturationModifier(1.0f).alwaysEdible()
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0), 1.0f)
                            .build())));

    // --- Mais variacoes de banana (etapa 8) ---
    /** Banana Vermelha: rara (colhida madura, ou comprada do goblin). Forca ao comer; entra no Nucleo e na Seringa. */
    public static final DeferredItem<Item> RED_BANANA = ITEMS.register("red_banana",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).food(
                    new FoodProperties.Builder().nutrition(5).saturationModifier(0.6f)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0), 1.0f)
                            .build())));

    /** Banana Podre: feita no Haunting (ventilador sobre fogo da alma). Entra no Sifao da Morte e no Selo do Medo. */
    public static final DeferredItem<Item> ROTTEN_BANANA = ITEMS.register("rotten_banana",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(1).saturationModifier(0.0f).alwaysEdible()
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 0), 0.6f)
                            .build())));

    /** Banana Cosmica: material final (Montagem Sequencial com as 4 essencias). Exigida pelos implantes. */
    public static final DeferredItem<Item> COSMIC_BANANA = ITEMS.register("cosmic_banana",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)) {
                @Override
                public boolean isFoil(ItemStack stack) {
                    return true;
                }
            });

    /** Polpa de Banana: banana moida no Moinho/Triturador do Create. Base da Liga e da Celula. */
    public static final DeferredItem<Item> BANANA_PULP = ITEMS.register("banana_pulp",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(1).saturationModifier(0.1f).fast().build())));

    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_BANANA_CORE = ITEMS.register("incomplete_banana_core",
            () -> new SequencedAssemblyItem(new Item.Properties()));
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_COSMIC_BANANA = ITEMS.register("incomplete_cosmic_banana",
            () -> new SequencedAssemblyItem(new Item.Properties()));
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CHROME_PLATE = ITEMS.register("incomplete_chrome_plate",
            () -> new SequencedAssemblyItem(new Item.Properties()));

    public static final DeferredItem<Item> BANANA_HANDLE = ITEMS.register("banana_handle",
            () -> new Item(new Item.Properties()));

    /** Liga de Banana: feita na bacia aquecida do Create (ferro + bananas). Base de ferramentas e celulas. */
    public static final DeferredItem<Item> BANANA_ALLOY = ITEMS.register("banana_alloy",
            () -> new Item(new Item.Properties()));

    /** Celula de Banana: a "energia" do addon. Quase tudo precisa dela para ser fabricado. */
    public static final DeferredItem<Item> BANANA_CELL = ITEMS.register("banana_cell",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    /** Nucleo de Banana: energia concentrada para maquinas e implantes avancados. */
    public static final DeferredItem<Item> BANANA_CORE = ITEMS.register("banana_core",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_BANANA_CELL = ITEMS.register("incomplete_banana_cell",
            () -> new SequencedAssemblyItem(new Item.Properties()));

    /** Casca de banana: agora e um bloco 3D que pode ser colocado no chao. */
    public static final DeferredItem<BlockItem> BANANA_PEEL = ITEMS.register("banana_peel",
            () -> new BlockItem(ModBlocks.BANANA_PEEL.get(), new Item.Properties()));

    public static final DeferredItem<SwordItem> BANANA_SWORD = ITEMS.register("banana_sword",
            () -> new BananaSwordItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(BananaTier.INSTANCE, 3, -2.4f))));

    public static final DeferredItem<PickaxeItem> BANANA_PICKAXE = ITEMS.register("banana_pickaxe",
            () -> new BananaPickaxeItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(BananaTier.INSTANCE, 1.0f, -2.8f))));

    public static final DeferredItem<AxeItem> BANANA_AXE = ITEMS.register("banana_axe",
            () -> new BananaAxeItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(AxeItem.createAttributes(BananaTier.INSTANCE, 6.0f, -3.1f))));

    public static final DeferredItem<ShovelItem> BANANA_SHOVEL = ITEMS.register("banana_shovel",
            () -> new BananaShovelItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(BananaTier.INSTANCE, 1.5f, -3.0f))));

    public static final DeferredItem<HoeItem> BANANA_HOE = ITEMS.register("banana_hoe",
            () -> new BananaHoeItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(HoeItem.createAttributes(BananaTier.INSTANCE, -2.0f, -1.0f))));

    public static final DeferredItem<BananaCannonItem> BANANA_CANNON = ITEMS.register("banana_cannon",
            () -> new BananaCannonItem(new Item.Properties().stacksTo(1).durability(500)));

    // --- Paranormal / Sangue ---
    public static final DeferredItem<Item> BLOOD_VIAL = ITEMS.register("blood_vial",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<BucketItem> BLOOD_BUCKET = ITEMS.register("blood_bucket",
            () -> new BucketItem(ModFluids.BLOOD.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<BloodBladeItem> BLOOD_BLADE = ITEMS.register("blood_blade",
            () -> new BloodBladeItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.2f))));

    public static final DeferredItem<OthersideEyeItem> OTHERSIDE_EYE = ITEMS.register("otherside_eye",
            () -> new OthersideEyeItem(new Item.Properties().rarity(Rarity.EPIC).durability(64)));

    // --- Titas / Copyright ---
    public static final DeferredItem<TitanSyringeItem> TITAN_SYRINGE = ITEMS.register("titan_syringe",
            () -> new TitanSyringeItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(16)));

    public static final DeferredItem<CopyrightHammerItem> COPYRIGHT_HAMMER = ITEMS.register("copyright_hammer",
            () -> new CopyrightHammerItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 4, -2.8f))));

    // --- Cyberware ---
    public static final DeferredItem<Item> CHROME_PLATE = ITEMS.register("chrome_plate",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NEURAL_CHIP = ITEMS.register("neural_chip",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<ChromeArmBladesItem> CHROME_ARM_BLADES = ITEMS.register("chrome_arm_blades",
            () -> new ChromeArmBladesItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 2, -1.2f))));

    public static final DeferredItem<OverclockInjectorItem> OVERCLOCK_INJECTOR = ITEMS.register("overclock_injector",
            () -> new OverclockInjectorItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));

    // --- Mini Goblin ---
    public static final DeferredItem<DeferredSpawnEggItem> MINI_GOBLIN_SPAWN_EGG = ITEMS.register("mini_goblin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MINI_GOBLIN, 0x4a8f3a, 0xc98a3c, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> KASANE_TETO_SPAWN_EGG = ITEMS.register("kasane_teto_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.KASANE_TETO, 0xd02a4a, 0x3a3a44, new Item.Properties()));

    public static final DeferredItem<Item> GOBLIN_HAIR_TUFT = ITEMS.register("goblin_hair_tuft",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> HAIR_PLATE = ITEMS.register("hair_plate",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<GoblinHairSwordItem> GOBLIN_HAIR_SWORD = ITEMS.register("goblin_hair_sword",
            () -> new GoblinHairSwordItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -1.8f))));

    // --- Cyberware corporal (instalado no corpo de V; ver pacote cyber) ---
    public static final DeferredItem<ImplantItem> GORILLA_ARMS = ITEMS.register("gorilla_arms",
            () -> new ImplantItem(Cyberware.GORILLA_ARMS, new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
    public static final DeferredItem<ImplantItem> MANTIS_BLADES = ITEMS.register("mantis_blades",
            () -> new ImplantItem(Cyberware.MANTIS_BLADES, new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
    public static final DeferredItem<ImplantItem> PROJECTILE_LAUNCHER = ITEMS.register("projectile_launcher",
            () -> new ImplantItem(Cyberware.PROJECTILE_LAUNCHER, new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
    public static final DeferredItem<ImplantItem> MONOWIRE = ITEMS.register("monowire",
            () -> new ImplantItem(Cyberware.MONOWIRE, new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
    public static final DeferredItem<ChromeScalpelItem> CHROME_SCALPEL = ITEMS.register("chrome_scalpel",
            () -> new ChromeScalpelItem(new Item.Properties().durability(64)));
    public static final DeferredItem<Item> MINI_ROCKET = ITEMS.register("mini_rocket",
            () -> new Item(new Item.Properties()));

    // Itens intermediarios das montagens sequenciais do Create
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_GORILLA_ARMS = ITEMS.register("incomplete_gorilla_arms",
            () -> new SequencedAssemblyItem(new Item.Properties()));
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MANTIS_BLADES = ITEMS.register("incomplete_mantis_blades",
            () -> new SequencedAssemblyItem(new Item.Properties()));
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_PROJECTILE_LAUNCHER = ITEMS.register("incomplete_projectile_launcher",
            () -> new SequencedAssemblyItem(new Item.Properties()));
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MONOWIRE = ITEMS.register("incomplete_monowire",
            () -> new SequencedAssemblyItem(new Item.Properties()));

    // --- Elementos paranormais: essencias (feitas por maquinas/ventiladores do Create) ---
    public static final DeferredItem<Item> DEATH_ESSENCE = ITEMS.register("death_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> CHAOS_SHARD = ITEMS.register("chaos_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> FEAR_ESSENCE = ITEMS.register("fear_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> KNOWLEDGE_ESSENCE = ITEMS.register("knowledge_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    // --- Block items ---
    public static final DeferredItem<BlockItem> CHROME_CASING = ITEMS.registerSimpleBlockItem("chrome_casing", ModBlocks.CHROME_CASING);
    public static final DeferredItem<BlockItem> SERVO_ARM = ITEMS.registerSimpleBlockItem("servo_arm", ModBlocks.SERVO_ARM);
    public static final DeferredItem<BlockItem> CHROME_COGWHEEL = ITEMS.registerSimpleBlockItem("chrome_cogwheel", ModBlocks.CHROME_COGWHEEL);
    public static final DeferredItem<BlockItem> BANANA_LOG = ITEMS.registerSimpleBlockItem("banana_log", ModBlocks.BANANA_LOG);
    public static final DeferredItem<BlockItem> BANANA_PLANKS = ITEMS.registerSimpleBlockItem("banana_planks", ModBlocks.BANANA_PLANKS);
    public static final DeferredItem<BlockItem> BANANA_LEAVES = ITEMS.registerSimpleBlockItem("banana_leaves", ModBlocks.BANANA_LEAVES);
    public static final DeferredItem<BlockItem> BANANA_SAPLING = ITEMS.registerSimpleBlockItem("banana_sapling", ModBlocks.BANANA_SAPLING);
    public static final DeferredItem<BlockItem> BLOOD_EXTRACTOR = ITEMS.registerSimpleBlockItem("blood_extractor", ModBlocks.BLOOD_EXTRACTOR);
    public static final DeferredItem<BlockItem> BLOOD_CENTRIFUGE = ITEMS.registerSimpleBlockItem("blood_centrifuge", ModBlocks.BLOOD_CENTRIFUGE);
    public static final DeferredItem<BlockItem> IMPLANT_TABLE = ITEMS.registerSimpleBlockItem("implant_table", ModBlocks.IMPLANT_TABLE);
    public static final DeferredItem<BlockItem> IMPLANT_REMOVAL_TABLE = ITEMS.registerSimpleBlockItem("implant_removal_table", ModBlocks.IMPLANT_REMOVAL_TABLE);
    public static final DeferredItem<BlockItem> TITAN_TREADMILL = ITEMS.registerSimpleBlockItem("titan_treadmill", ModBlocks.TITAN_TREADMILL);
    public static final DeferredItem<BlockItem> CHAOS_DYNAMO = ITEMS.registerSimpleBlockItem("chaos_dynamo", ModBlocks.CHAOS_DYNAMO);
    public static final DeferredItem<BlockItem> DEATH_SIPHON = ITEMS.registerSimpleBlockItem("death_siphon", ModBlocks.DEATH_SIPHON);
    public static final DeferredItem<BlockItem> FEAR_SEAL = ITEMS.registerSimpleBlockItem("fear_seal", ModBlocks.FEAR_SEAL);
}
