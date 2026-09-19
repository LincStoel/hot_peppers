package com.lincstoel.hotpeppers;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

// A loot condition (rather than a check baked into AddItemModifier) so the config gate lives
// alongside the rest of jungle_grass_seeds.json's gating (biome, block, chance), matching
// AddItemModifier's "all gating lives in the JSON conditions" design.
public class JungleGrassSeedsEnabledCondition implements LootItemCondition {

    public static final JungleGrassSeedsEnabledCondition INSTANCE = new JungleGrassSeedsEnabledCondition();
    public static final MapCodec<JungleGrassSeedsEnabledCondition> CODEC = MapCodec.unit(INSTANCE);

    private JungleGrassSeedsEnabledCondition() {
    }

    @Override
    public boolean test(LootContext context) {
        return HotPeppersConfig.ENABLE_JUNGLE_GRASS_SEEDS.get();
    }

    @Override
    public LootItemConditionType getType() {
        return HotPeppers.JUNGLE_GRASS_SEEDS_ENABLED.get();
    }
}
