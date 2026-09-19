package com.lincstoel.hotpeppers;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Single source of truth for stacking the Spicy effect and for reading/writing spice_level on food items. */
final class Spice {

    private static final int DURATION_TICKS = 15 * 20;
    private static final int MAX_LEVEL = 3;
    private static final int IGNITE_SECONDS = 3;

    private Spice() {
    }

    static void apply(Player player, int doses) {
        MobEffectInstance current = player.getEffect(HotPeppers.SPICY);
        int level = current == null ? 0 : current.getAmplifier() + 1;
        int target = level + doses;

        player.addEffect(new MobEffectInstance(HotPeppers.SPICY, DURATION_TICKS, Math.min(MAX_LEVEL, target) - 1,
                false, true, true));

        if (target > MAX_LEVEL) {
            player.igniteForSeconds(IGNITE_SECONDS);
        }
    }

    static int getSpiceLevel(ItemStack stack) {
        return stack.getOrDefault(HotPeppers.SPICE_LEVEL, 0);
    }

    static ItemStack withSpiceLevel(ItemStack stack, int level) {
        stack.set(HotPeppers.SPICE_LEVEL, level);
        return stack;
    }
}
