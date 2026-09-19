package com.lincstoel.hotpeppers;

import net.neoforged.neoforge.common.ModConfigSpec;

public class HotPeppersConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_JUNGLE_GRASS_SEEDS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ENABLE_JUNGLE_GRASS_SEEDS = builder
                .comment(
                        "Whether breaking jungle grass/tall grass has a small chance to drop hot pepper seeds,",
                        "in addition to the wild hot pepper plant. Disabled by default since the wild hot",
                        "pepper plant is the intended way to forage seeds outside of farming.")
                .define("enableJungleGrassSeeds", false);

        SPEC = builder.build();
    }

    private HotPeppersConfig() {
    }
}
