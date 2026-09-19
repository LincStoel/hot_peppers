package com.lincstoel.hotpeppers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(HotPeppers.MOD_ID)
public class HotPeppers {

    public static final String MOD_ID = "hot_peppers";

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);
    private static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);
    private static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MOD_ID);

    public static final DeferredBlock<HotPepperCropBlock> HOT_PEPPER_CROP = BLOCKS.register("hot_pepper_crop",
            () -> new HotPepperCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));

    // Built from scratch rather than ofFullCopy(Blocks.WHEAT) since it has no growth at all, so
    // wheat's randomTicks() would be dead weight.
    public static final DeferredBlock<WildHotPepperBlock> WILD_HOT_PEPPER = BLOCKS.register("wild_hot_pepper",
            () -> new WildHotPepperBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .replaceable()));

    // A BlockItem (as Farmer's Delight also does for its wild crops) so the block is give/pick
    // block-able for testing and creative use; it's still "not farmable" in survival since
    // nothing in a loot table or recipe ever yields it - the only organic source of the seed
    // stays the worldgen-placed plant itself.
    public static final DeferredItem<BlockItem> WILD_HOT_PEPPER_ITEM =
            ITEMS.registerSimpleBlockItem(WILD_HOT_PEPPER);

    // nutrition x saturationModifier x 2 = saturation restored; 4 x 0.125 x 2 = 1.0.
    // alwaysEdible() because the point of eating one is the Spicy effect, not the nutrition.
    public static final DeferredItem<Item> HOT_PEPPER = ITEMS.registerItem("hot_pepper",
            props -> new Item(props.food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(0.125f)
                    .alwaysEdible()
                    .build())));

    public static final DeferredItem<ItemNameBlockItem> HOT_PEPPER_SEEDS = ITEMS.register("hot_pepper_seeds",
            () -> new ItemNameBlockItem(HOT_PEPPER_CROP.get(), new Item.Properties()));

    public static final DeferredHolder<MobEffect, SpicyEffect> SPICY = MOB_EFFECTS.register("spicy",
            () -> new SpicyEffect(MobEffectCategory.BENEFICIAL, 0xD3312A));

    // Rank of an already-spicy food item; 1..3, both saved to disk and sent to the client for the tooltip.
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SPICE_LEVEL =
            DATA_COMPONENTS.registerComponentType("spice_level", builder -> builder
                    .persistent(Codec.intRange(1, 3))
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<SpicyFoodRecipe>> SPICY_FOOD =
            RECIPE_SERIALIZERS.register("spicy_food", () -> new SimpleCraftingRecipeSerializer<>(SpicyFoodRecipe::new));

    // The DamageType itself is datapack JSON (data/hot_peppers/damage_type/spicy_burn.json); this key just
    // lets Java code reference it without a hard dependency on the registry being loaded yet.
    public static final ResourceKey<DamageType> SPICY_BURN = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "spicy_burn"));

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddItemModifier>> ADD_ITEM_MODIFIER =
            LOOT_MODIFIER_SERIALIZERS.register("add_item", () -> AddItemModifier.CODEC);

    // Referenced from data/hot_peppers/loot_modifiers/jungle_grass_seeds.json to gate that
    // modifier behind HotPeppersConfig.ENABLE_JUNGLE_GRASS_SEEDS.
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> JUNGLE_GRASS_SEEDS_ENABLED =
            LOOT_CONDITION_TYPES.register("jungle_grass_seeds_enabled",
                    () -> new LootItemConditionType(JungleGrassSeedsEnabledCondition.CODEC));

    public HotPeppers(IEventBus modEventBus, ModContainer container) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        LOOT_CONDITION_TYPES.register(modEventBus);

        container.registerConfig(ModConfig.Type.COMMON, HotPeppersConfig.SPEC);

        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(HOT_PEPPER);
            event.accept(HOT_PEPPER_SEEDS);
        } else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(HOT_PEPPER_SEEDS);
            event.accept(WILD_HOT_PEPPER_ITEM);
        }
    }
}
